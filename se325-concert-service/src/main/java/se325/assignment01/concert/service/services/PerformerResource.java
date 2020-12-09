package se325.assignment01.concert.service.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se325.assignment01.concert.common.dto.PerformerDTO;
import se325.assignment01.concert.service.domain.Performer;
import se325.assignment01.concert.service.mapper.PerformerMapper;

import javax.persistence.EntityManager;
import javax.ws.rs.*;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * This class contains all methods that related to Performer features/services (i.e. performer information)
 */
@Path("/concert-service/performers")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PerformerResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(PerformerResource.class);

    /**
     * The get method will return the specific performer's information by his/her id
     * @param id
     * @return
     */
    @GET
    @Path("/{id}")
    public Response retrievePerformer(@PathParam("id") Long id) {

        PerformerDTO performerDTO = null;
        EntityManager em = PersistenceManager.instance().createEntityManager();

        try {
            em.getTransaction().begin();
            Performer performer = em.find(Performer.class, id);

            // if the performer does no exit
            if (performer == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }

            // convert to DTO
            performerDTO = PerformerMapper.toDTO(performer);

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
        return Response.ok(performerDTO).build();
    }

    /**
     * The get method will return all performers
     * @return
     */
    @GET
    public Response retrieveAllPerformers() {
        GenericEntity<List<PerformerDTO>> entity = null;
        EntityManager em = PersistenceManager.instance().createEntityManager();

        try {
            em.getTransaction().begin();
            List<Performer> performers =
                    em.createQuery("select p from Performer p", Performer.class)
                            .getResultList();

            List<PerformerDTO> performerDTOS = new ArrayList<PerformerDTO>();
            for (Performer performer : performers) {
                performerDTOS.add(PerformerMapper.toDTO(performer));// convert to DTO
            }

            entity = new GenericEntity<List<PerformerDTO>>(performerDTOS) {};
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
