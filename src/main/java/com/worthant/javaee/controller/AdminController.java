package com.worthant.javaee.controller;

import com.worthant.javaee.auth.UserPrincipal;
import com.worthant.javaee.dto.PointDTO;
import com.worthant.javaee.dto.SessionsDTO;
import com.worthant.javaee.dto.UserDTO;
import com.worthant.javaee.exceptions.UserNotFoundException;
import com.worthant.javaee.service.AdminService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.List;

@Path("/admin")
@RolesAllowed("ADMIN")
@Slf4j
public class AdminController {

    @Inject
    private AdminService adminService;

    @Context
    private SecurityContext securityContext;

    // TODO: this endpoint shouldn't show admins
    // only users with the role 'USER'
    @GET
    @Path("/users")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUsers() {
        List<UserDTO> users = adminService.getAllUsers();
        return Response.ok(users).build();
    }

    // TODO: check if the user is already an admin
    // if yes, than don't promote it, but return some "already an admin" msg
    @POST
    @Path("/promote/{userId}")
    public Response promoteToAdmin(@PathParam("userId") Long userId) {
        try {
            adminService.promoteToAdmin(userId);
            return Response.ok().entity("User promoted to admin.").build();
        } catch (UserNotFoundException e) {
            log.error("Error promoting user to admin: {}", e.getMessage());
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (Exception e) {
            log.error("Internal server error: {}", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Internal server error.").build();
        }
    }

    // TODO: don't check the points of an admin
    // if the userId is an id of an 'ADMIN' - return some error msg about it
    @GET
    @Path("/points/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserPoints(@PathParam("userId") Long userId) {
        try {
            List<PointDTO> points = adminService.getUserPoints(userId);
            return Response.ok(points).build();
        } catch (UserNotFoundException e) {
            log.error("Error retrieving points for user {}: {}", userId, e.getMessage());
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (Exception e) {
            log.error("Internal server error while retrieving points for user {}: {}", userId, e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Internal server error.").build();
        }
    }

    @DELETE
    @Path("/removeUser/{userId}")
    public Response removeUser(@PathParam("userId") Long userId) {
        try {
            adminService.removeUser(userId);
            return Response.ok().entity("User removed successfully.").build();
        } catch (UserNotFoundException e) {
            log.error("Error removing user: {}", e.getMessage());
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (Exception e) {
            log.error("Internal server error: {}", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Internal server error.").build();
        }
    }

    @GET
    @Path("/getUserSessions/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserSessions(@PathParam("userId") Long userId) {
        try {
            List<SessionsDTO> sessions = adminService.getUserSessions(userId);
            return Response.ok(sessions).build();
        } catch (UserNotFoundException e) {
            log.error("Error retrieving sessions for user {}: {}", userId, e.getMessage());
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (Exception e) {
            log.error("Internal server error while retrieving sessions for user {}: {}", userId, e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Internal server error.").build();
        }
    }

    @GET
    @Path("/getLastUserSessionDuration/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLastUserSessionDuration(@PathParam("userId") Long userId) {
        try {
            Duration duration = adminService.getLastUserSessionDuration(userId);
            return Response.ok(duration).build();
        } catch (UserNotFoundException e) {
            log.error("Error retrieving last session duration for user {}: {}", userId, e.getMessage());
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (Exception e) {
            log.error("Internal server error while retrieving last session duration for user {}: {}", userId, e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Internal server error.").build();
        }
    }

    @GET
    @Path("/getNumberOfUserPoints/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getNumberOfUserPoints(@PathParam("userId") Long userId) {
        try {
            long pointsCount = adminService.getNumberOfUserPoints(userId);
            return Response.ok(pointsCount).build();
        } catch (UserNotFoundException e) {
            log.error("Error retrieving number of points for user {}: {}", userId, e.getMessage());
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (Exception e) {
            log.error("Internal server error while retrieving number of points for user {}: {}", userId, e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Internal server error.").build();
        }
    }

}

