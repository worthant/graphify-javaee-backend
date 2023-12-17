package com.worthant.javaee.controller;

import com.worthant.javaee.auth.UserPrincipal;
import com.worthant.javaee.dto.PointDTO;
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

import java.util.List;

@Path("/admin")
@RolesAllowed("ADMIN")
@Slf4j
public class AdminController {

    @Inject
    private AdminService adminService;

    @Context
    private SecurityContext securityContext;

    @GET
    @Path("/users")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUsers() {
        List<UserDTO> users = adminService.getAllUsers();
        return Response.ok(users).build();
    }

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

}

