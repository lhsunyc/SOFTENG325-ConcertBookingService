package se325.assignment01.concert.service.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se325.assignment01.concert.common.dto.*;
import se325.assignment01.concert.service.domain.*;
import se325.assignment01.concert.service.mapper.ConcertMapper;

import javax.persistence.*;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class contains all methods that related to Concert features/services (i.e. Concert information)
 */
@Path("/concert-service/concerts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ConcertResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConcertResource.class);

    /**
     * The get method will return a specific concert filtered by its id
     * @param id
     * @return
     */
    @GET
    @Path("/{id}")
    public Response retrieveConcert(@PathParam("id") Long id) {
        EntityManager em = PersistenceManager.instance().createEntityManager();

        try {
            em.getTransaction().begin();

            // Concert query
            Concert concert = em.find(Concert.class, id);

            // if the concert is found
            if (concert != null) {
                return Response.ok(ConcertMapper.toDTO(concert)).build();
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
        // else return not found status
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    /**
     * The get method will return all concerts
     * @return
     */
    @GET
    public Response retrieveAllConcerts() {
        GenericEntity<List<ConcertDTO>> entity = null;
        EntityManager em = PersistenceManager.instance().createEntityManager();

        try {
            em.getTransaction().begin();

            // Concert query to get all concerts
            List<Concert> concerts =
                    em.createQuery("select c from Concert c", Concert.class)
                            .getResultList();

            List<ConcertDTO> concertDTOS = new ArrayList<ConcertDTO>();
            for (Concert concert : concerts) {
                concertDTOS.add(ConcertMapper.toDTO(concert));// convert to DTO
            }

            entity = new GenericEntity<List<ConcertDTO>>(concertDTOS) {};
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
        return Response.ok(entity).build();
    }

    /**
     * The get method will return all concert summaries
     * @return
     */
    @GET
    @Path("/summaries")
    public Response retrieveAllConcertSummaries() {

        GenericEntity<List<ConcertSummaryDTO>> entity = null;
        EntityManager em = PersistenceManager.instance().createEntityManager();

        try {
            em.getTransaction().begin();
            List<Concert> concerts =
                    em.createQuery("select c from Concert c", Concert.class)
                            .getResultList();

            List<ConcertSummaryDTO> concertSummaryDTOS = new ArrayList<ConcertSummaryDTO>();
            for (Concert concert : concerts) {
                concertSummaryDTOS.add(ConcertMapper.toSummaryDTO(concert));
            }

            entity = new GenericEntity<List<ConcertSummaryDTO>>(concertSummaryDTOS) {};
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
