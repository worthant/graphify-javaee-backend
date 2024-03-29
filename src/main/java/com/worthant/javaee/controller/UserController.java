package com.worthant.javaee.controller;

import com.worthant.javaee.auth.UserPrincipal;
import com.worthant.javaee.dto.PasswordDTO;
import com.worthant.javaee.dto.PointDTO;
import com.worthant.javaee.exceptions.AuthenticationException;
import com.worthant.javaee.exceptions.PointNotFoundException;
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

    @POST
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
            PointDTO createdPoint = userService.addUserPoint(userPrincipal.getUserId(), pointDTO);
            return Response.ok(createdPoint).build();
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

    // TODO: should the PointNotFoundException really return 404?
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
        } catch (PointNotFoundException e) {
            log.error("Point not found: {}", e.getMessage());
            return Response.status(Response.Status.NOT_FOUND).entity("Point not found").build();
        } catch (Exception e) {
            log.error("Internal server error: {}", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Internal server error").build();
        }
    }

    @POST
    @Path("/redrawAllPoints")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response redrawAllPoints(@QueryParam("r") double newRadius) {
        UserPrincipal userPrincipal = (UserPrincipal) securityContext.getUserPrincipal();
        try {
            List<PointDTO> updatedPoints = userService.redrawAllPoints(userPrincipal.getUserId(), newRadius);
            return Response.ok(updatedPoints).build();
        } catch (Exception e) {
            log.error("Error updating points: {}", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/changePassword")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response changePassword(PasswordDTO passwordDTO) {
        UserPrincipal userPrincipal = (UserPrincipal) securityContext.getUserPrincipal();
        try {
            userService.changePassword(userPrincipal.getUserId(), userPrincipal.getEmail(), passwordDTO);
            return Response.ok().entity("Password changed successfully.").build();
        } catch (IllegalStateException | UserNotFoundException e) {
            log.error("Error changing password: {}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (ServerException e) {
            log.error("Server Exception: {}", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Server Exception").build();
        } catch (Exception e) {
            log.error("Internal server error: {}", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Internal server error").build();
        }
    }

}
