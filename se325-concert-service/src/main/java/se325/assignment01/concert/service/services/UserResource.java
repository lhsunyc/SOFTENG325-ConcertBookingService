package se325.assignment01.concert.service.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se325.assignment01.concert.common.dto.UserDTO;
import se325.assignment01.concert.service.domain.User;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.util.UUID;

/**
 * This class contains all methods that related to user features (i.e. login)
 */
@Path("/concert-service")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserResource.class);

    /**
     * This post method allows users to login to their accounts
     * @param userDTO
     * @return a corresponding response which indicate the process status
     */
    @POST
    @Path("/login")
    public Response login(UserDTO userDTO) {
        String username = userDTO.getUsername();
        String password = userDTO.getPassword();
        EntityManager em = PersistenceManager.instance().createEntityManager();

        try {
            em.getTransaction().begin();
            User user =
                    em.createQuery("select u from User u where u.username = :username AND u.password = :password", User.class)
                            .setLockMode(LockModeType.OPTIMISTIC)
                            .setParameter("username", username)
                            .setParameter("password", password)
                            .getSingleResult();

            // create cookie
            String token = UUID.randomUUID().toString();
            NewCookie cookie = new NewCookie("auth", token);
            user.setToken(token);

            em.merge(user);
            em.getTransaction().commit();

            return Response.ok().cookie(cookie).build();
        } catch (NoResultException e) {
            LOGGER.error(e.getMessage());
            return Response.status(Response.Status.UNAUTHORIZED).build();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
}
