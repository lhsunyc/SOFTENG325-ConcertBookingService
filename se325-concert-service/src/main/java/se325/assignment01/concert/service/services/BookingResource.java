package se325.assignment01.concert.service.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se325.assignment01.concert.common.dto.BookingDTO;
import se325.assignment01.concert.common.dto.BookingRequestDTO;
import se325.assignment01.concert.service.domain.Booking;
import se325.assignment01.concert.service.domain.Concert;
import se325.assignment01.concert.service.domain.Seat;
import se325.assignment01.concert.service.domain.User;
import se325.assignment01.concert.service.mapper.BookingMapper;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * This class contains all methods that related to Booking features/services (i.e. make o booking)
 */
@Path("/concert-service/bookings")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class BookingResource {

    private static Logger LOGGER = LoggerFactory.getLogger(BookingResource.class);

    /**
     * This post method allows users to make a booking, while notify other subscribers about this.
     * @param bookingRequestDTO
     * @param cookie
     * @return
     */
    @POST
    public Response attemptBooking(BookingRequestDTO bookingRequestDTO, @CookieParam("auth") Cookie cookie) {

        // Check cookie
        if (cookie == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        Booking booking;
        EntityManager em = PersistenceManager.instance().createEntityManager();

        try {
            em.getTransaction().begin();

            // User query and store the result to user
            User user = em.createQuery("select u from User u where u.token = :token", User.class)
                    .setParameter("token", cookie.getValue())
                    .getSingleResult();

            // Concert query by id
            Concert concert = em.find(Concert.class, bookingRequestDTO.getConcertId());

            // Check if the concert exits and the date is available
            if (concert == null || !concert.getDates().contains(bookingRequestDTO.getDate())) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }

            // Seat query to find unbooked seats in a given date
            List<Seat> seats =
                    em.createQuery("select s from Seat s where s.date = :date and s.isBooked = false and s.label in :seatLabel", Seat.class)
                            .setParameter("seatLabel", bookingRequestDTO.getSeatLabels())
                            .setParameter("date", bookingRequestDTO.getDate())
                            .getResultList();

            // check if the seats are available and whether users have select any seats
            if (seats.size() != bookingRequestDTO.getSeatLabels().size()) {
                return Response.status(Response.Status.FORBIDDEN).build();
            } else if (seats.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }

            // set them to booked
            for (Seat seat : seats) {
                seat.setBooked(true);
            }

            booking = new Booking(bookingRequestDTO.getDate(), bookingRequestDTO.getConcertId() ,user, seats);

            em.persist(booking);
            em.getTransaction().commit();

            // notify subscriber
            SubscribeResource.checkSubscription(bookingRequestDTO.getConcertId(), bookingRequestDTO.getDate());
        } catch (NoResultException e) {
            LOGGER.error(e.getMessage());
            return Response.status(Response.Status.UNAUTHORIZED).build();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
        return Response.created(URI.create("concert-service/bookings/" + booking.getId())).cookie(new NewCookie("auth", cookie.getValue())).build();
    }

    /**
     * This get method will return bookings made by users filtered by its id.
     * @param id
     * @param cookie
     * @return
     */
    @GET
    @Path("/{id}")
    public Response retrieveBooking(@PathParam("id") Long id, @CookieParam("auth") Cookie cookie) {

        // Check cookie
        if (cookie == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        BookingDTO bookingDTO;
        EntityManager em = PersistenceManager.instance().createEntityManager();

        try {
            em.getTransaction().begin();

            // User query to check authentication
            User user = em.createQuery("select u from User u where u.token = :token", User.class)
                    .setParameter("token", cookie.getValue())
                    .getSingleResult();

            // Booking query by id
            Booking booking = em.find(Booking.class, id);

            // if booking not exit
            if (booking == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }

            // if the booking id did not match
            if (booking.getUser().getId() != user.getId()) {
                return Response.status(Response.Status.FORBIDDEN).build();
            }

            bookingDTO = BookingMapper.toDTO(booking);// convert to DTO

        } catch (NoResultException e) {
            // if user not found
            return Response.status(Response.Status.UNAUTHORIZED).build();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
        return Response.ok(bookingDTO).cookie(new NewCookie("auth", cookie.getValue())).build();
    }

    /**
     * This get method will return all bookings
     * @param cookie
     * @return
     */
    @GET
    public Response retrieveAllBookingsForUser(@CookieParam("auth") Cookie cookie) {

        // Check cookie
        if (cookie == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        GenericEntity<List<BookingDTO>> entity;
        EntityManager em = PersistenceManager.instance().createEntityManager();

        try {
            em.getTransaction().begin();

            // User query to check authentication
            User user = em.createQuery("select u from User u where u.token = :token", User.class)
                    .setParameter("token", cookie.getValue())
                    .getSingleResult();

            // Booking query by user
            List<Booking> bookings
                    = em.createQuery("select b from Booking b where b.user = :user", Booking.class)
                    .setParameter("user", user)
                    .getResultList();

            List<BookingDTO> bookingDTOS = new ArrayList<>();
            for (Booking booking : bookings) {
                bookingDTOS.add(BookingMapper.toDTO(booking));// convert to DTO
            }

            entity = new GenericEntity<List<BookingDTO>>(bookingDTOS) {};

        } catch (NoResultException e) {
            // if user not found
            return Response.status(Response.Status.UNAUTHORIZED).build();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
        return Response.ok(entity).cookie(new NewCookie("auth", cookie.getValue())).build();
    }

}
