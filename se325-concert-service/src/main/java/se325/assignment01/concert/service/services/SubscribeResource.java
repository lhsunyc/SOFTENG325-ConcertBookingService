package se325.assignment01.concert.service.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se325.assignment01.concert.common.dto.ConcertInfoNotificationDTO;
import se325.assignment01.concert.common.dto.ConcertInfoSubscriptionDTO;
import se325.assignment01.concert.service.domain.Concert;
import se325.assignment01.concert.service.domain.ConcertInfoSubscription;
import se325.assignment01.concert.service.domain.User;
import se325.assignment01.concert.service.mapper.ConcertInfoSubscriptionMapper;

import javax.persistence.EntityManager;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class contains all methods that related to subscribe features/services.
 */

@Path("/concert-service/subscribe")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SubscribeResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubscribeResource.class);
    private static final Map<Long,AsyncResponse> responses = new ConcurrentHashMap<>();
    private static final int VENUE_CAPACITY = 120;// constant number which indicates the max seats number in the venue.

    /**
     * This post method is used to get concert information
     * @param concertInfoSubscriptionDTO an object that contains concert info
     * @param cookie cookie on the web
     * @param response asynchronous response with booking process
     */
    @POST
    @Path("/concertInfo")
    public void subscribe(ConcertInfoSubscriptionDTO concertInfoSubscriptionDTO, @CookieParam("auth") Cookie cookie, @Suspended AsyncResponse response) {
        EntityManager em = PersistenceManager.instance().createEntityManager();
        try {
            em.getTransaction().begin();

            // check if the user is authorised, if not, NoResultException will be thrown
            em.createQuery("select u from User u where u.token = :token", User.class)
                    .setParameter("token", cookie.getValue())
                    .getSingleResult();

            // check the availability of concert (info)
            Concert concert = em.find(Concert.class, concertInfoSubscriptionDTO.getConcertId());
            if (concert == null || !concert.getDates().contains(concertInfoSubscriptionDTO.getDate())) {
                response.resume(Response.status(Response.Status.BAD_REQUEST).build());
                return;
            }

            // convert to domain model in order to persist
            ConcertInfoSubscription concertInfoSubscription = ConcertInfoSubscriptionMapper.toDomainModel(concertInfoSubscriptionDTO);

            em.persist(concertInfoSubscription);
            responses.put(concertInfoSubscription.getId(), response);// add the response to ConcurrentHashMap (ready to use in booking resource)

            em.getTransaction().commit();
        } catch (Exception e){
            response.resume(Response.status(Response.Status.UNAUTHORIZED).build());
            return;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * This method is used to check subscription information
     * @param concertId
     * @param date
     */
    public static void checkSubscription(long concertId, LocalDateTime date) {
        EntityManager em = PersistenceManager.instance().createEntityManager();
        try {
            em.getTransaction().begin();

            // get number of available seats
            int numSeatsRemained =
                    em.createQuery("select count (s) from Seat s where s.date = :date and s.isBooked = :isBooked", Long.class)
                            .setParameter("date", date)
                            .setParameter("isBooked", false)
                            .getSingleResult()
                            .intValue();

            // calculate percentage of booked seats
            int percentageBooked = (int)((double)(VENUE_CAPACITY - numSeatsRemained)/(VENUE_CAPACITY) * 100);

            // call notifySubscribers method
            notifySubscribers(concertId, date, percentageBooked, numSeatsRemained);

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * This method is to notify user with relevant information about concert
     * @param concertId
     * @param date
     * @param percentageBooked
     * @param numSeatsRemaining
     */
    private static void notifySubscribers(long concertId, LocalDateTime date, int percentageBooked, int numSeatsRemaining) {
        EntityManager em = PersistenceManager.instance().createEntityManager();
        try {
            em.getTransaction().begin();

            // get all subscriptions that fulfill the requirement
            // (i.e. the date/concertId is matched and when the percentage of booked seats is lower than what the user set)
            List<ConcertInfoSubscription> subscriptions =
                    em.createQuery("select s from ConcertInfoSubscription s where s.concertId = :concertId and s.date = :date and s.percentageBooked <= :percentageBooked", ConcertInfoSubscription.class)
                            .setParameter("concertId", concertId)
                            .setParameter("date", date)
                            .setParameter("percentageBooked", percentageBooked)
                            .getResultList();

            // Check whether the subscription list is empty
            if (subscriptions.isEmpty()) {
                return;
            }

            // Get the response which contains seat information
            synchronized (responses) {
                for (ConcertInfoSubscription subscription : subscriptions) {
                    responses.get(subscription.getId()).resume(new ConcertInfoNotificationDTO(numSeatsRemaining));
                    responses.remove(subscription.getId());
                    em.remove(subscription);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

}
