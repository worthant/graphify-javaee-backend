package com.worthant.javaee.controller;

import com.worthant.javaee.auth.UserPrincipal;
import com.worthant.javaee.dto.PointDTO;
import com.worthant.javaee.exceptions.AuthenticationException;
import com.worthant.javaee.exceptions.ServerException;
import com.worthant.javaee.exceptions.UserNotFoundException;
import com.worthant.javaee.service.UserService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Path("/user")
@Slf4j
public class UserController {

    @Inject
    UserService userService;

    @Context
    private SecurityContext securityContext;

    @GET
    @Path("/points")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserPoints() {
        UserPrincipal userPrincipal = (UserPrincipal) securityContext.getUserPrincipal();
        try {
            List<PointDTO> points = userService.getUserPoints(userPrincipal.getUserId());
            return Response.ok(points).build();
        } catch (UserNotFoundException e) {
            log.error("Error retrieving points for user {}: {}", userPrincipal.getUserId(), e.getMessage());
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (Exception e) {
            log.error("Internal server error while retrieving points for user {}: {}", userPrincipal.getUserId(), e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Internal server error.").build();
        }
    }

    @POST
    @Path("/addPoint")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addUserPoint(PointDTO pointDTO) {
        UserPrincipal userPrincipal = (UserPrincipal) securityContext.getUserPrincipal();
        try {
            userService.addUserPoint(userPrincipal.getUserId(), pointDTO);
            return Response.ok().entity("Point added.").build();
        } catch (Exception e) {
            log.error("Error adding point for user {}: {}", userPrincipal.getUserId(), e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/deleteAllPoints")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteUserPoint() {
        UserPrincipal userPrincipal = (UserPrincipal) securityContext.getUserPrincipal();
        try {
            Long userId = userPrincipal.getUserId();
            userService.deleteUserPoints(userId);
            return Response.ok().entity("All points deleted successfully.").build();
        } catch (UserNotFoundException e) {
            log.error("User not found: {}", e.getMessage());
            return Response.status(Response.Status.NOT_FOUND).entity("User not found").build();
        } catch (Exception e) {
            log.error("Internal server error: {}", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Internal server error").build();
        }
    }


    @DELETE
    @Path("/deletePoint")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deletePoint(PointDTO pointDTO) {
        UserPrincipal userPrincipal = (UserPrincipal) securityContext.getUserPrincipal();
        try {
            userService.deleteSinglePoint(userPrincipal.getUserId(), pointDTO);
            return Response.ok().entity("Point deleted successfully.").build();
        } catch (UserNotFoundException e) {
            log.error("User not found: {}", e.getMessage());
            return Response.status(Response.Status.NOT_FOUND).entity("User not found").build();
        } catch (Exception e) {
            log.error("Internal server error: {}", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Internal server error").build();
        }
    }

}
