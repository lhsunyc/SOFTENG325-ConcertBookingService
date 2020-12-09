package se325.assignment01.concert.service.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se325.assignment01.concert.common.dto.SeatDTO;
import se325.assignment01.concert.common.types.BookingStatus;
import se325.assignment01.concert.service.domain.Seat;
import se325.assignment01.concert.service.jaxrs.LocalDateTimeParam;
import se325.assignment01.concert.service.mapper.SeatMapper;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.ws.rs.*;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * This class contains all methods that related to seats features/services (i.e. seats information)
 */
@Path("/concert-service/seats")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SeatResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(SeatResource.class);

    /**
     * This get method will get all seats in one day
     * @param dateTimeParam
     * @param bookingStatus
     * @return
     */
    @GET
    @Path("/{date}")
    public Response retrieveSeats(@PathParam("date") LocalDateTimeParam dateTimeParam, @QueryParam("status") BookingStatus bookingStatus) {

        GenericEntity<List<SeatDTO>> entity = null;
        LocalDateTime date = dateTimeParam.getLocalDateTime();
        EntityManager em = PersistenceManager.instance().createEntityManager();

        try {
            em.getTransaction().begin();
            List<Seat> bookedSeats;

            // if the seat is neither booked nor unbooked
            if (bookingStatus == BookingStatus.Any) {
                bookedSeats = em.createQuery("select s from Seat s Where s.date = :date", Seat.class)
                        .setLockMode(LockModeType.OPTIMISTIC)
                        .setParameter("date", date)
                        .getResultList();
            } else {
                // Set date and booking status
                boolean isBooked = (bookingStatus == BookingStatus.Booked);
                bookedSeats = em.createQuery("select s from Seat s Where s.date=:date and s.isBooked = :isBooked", Seat.class)
                        .setLockMode(LockModeType.OPTIMISTIC)
                        .setParameter("isBooked", isBooked)
                        .setParameter("date", date)
                        .getResultList();
            }

            List<SeatDTO> bookedSeatDTOs = new ArrayList<>();
            for (Seat seat : bookedSeats) {
                bookedSeatDTOs.add(SeatMapper.toDTO(seat));
            }

            entity = new GenericEntity<List<SeatDTO>>(bookedSeatDTOs) {};
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
        return Response.ok(entity).build();
    }

}
