package com.worthant.javaee.controller;

import com.worthant.javaee.auth.UserPrincipal;
import com.worthant.javaee.dto.*;
import com.worthant.javaee.exceptions.*;
import com.worthant.javaee.service.AuthService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import lombok.extern.slf4j.Slf4j;

@Path("/auth")
@Slf4j
public class AuthController {

    @Inject
    private AuthService authService;

    @Context
    private SecurityContext securityContext;

    @POST
    @Path("/signup")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response signUp(@Valid UserDTO userDto) {
        try {
            String token = authService.registerUser(userDto.getUsername(), userDto.getPassword(), userDto.getEmail());
            log.info("Authorization successful!)");
            return Response.ok(new TokenDTO(token)).build();
        } catch (UserExistsException | InvalidEmailException e) {
            log.error(e.getMessage());
            return Response.status(Response.Status.CONFLICT).entity(e.getMessage()).build();
        } catch (ServerException | UserNotFoundException e) {
            log.error(e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@Valid SimpleUserDTO userDto) {
        try {
            String token = authService.authenticateUser(userDto.getEmail(), userDto.getPassword());
            log.info("Login successful for user with email: {}", userDto.getEmail());
            return Response.ok(new TokenDTO(token)).build();
        } catch (AuthenticationException e) {
            log.error("Login failed for user with email: {}", userDto.getEmail());
            return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build();
        } catch (UserNotFoundException e) {
            log.error("User not found: {}", e.getMessage());
            return Response.status(Response.Status.NOT_FOUND).entity("User not found").build();
        } catch (ServerException e) {
            log.error("Internal server error: {}", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/logout")
    public Response logout() {
        // TODO: save theme settings on logout
        try {
            UserPrincipal userPrincipal = (UserPrincipal) securityContext.getUserPrincipal();
            authService.endSession(userPrincipal.getUserId());

            log.info("User logged out successfully.");
            return Response.ok().entity("User logged out successfully.").build();
        } catch (Exception e) {
            log.error("Error during logout: {}", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error during logout").build();
        }
    }

    @POST
    @Path("/admin")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response adminLogin(UserDTO userDto) {
        try {
            String token = authService.authenticateAdmin(userDto.getUsername(), userDto.getPassword());
            log.info("Admin login successful for user: {}", userDto.getUsername());
            return Response.ok(new TokenDTO(token)).build();
        } catch (AuthenticationException e) {
            log.error("Admin login failed for user: {}", userDto.getUsername());
            return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build();
        } catch (ServerException e) {
            log.error("Internal server error: {}", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/passwordReminder")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response sendPasswordReminder(EmailDTO emailDTO) {
        try {
            authService.remindPassword(emailDTO.getEmail());
            log.info("Password reset sent successfully to email: {}", emailDTO.getEmail());
            return Response.ok().entity("Password reset sent successfully.").build();
        } catch (UserNotFoundException e) {
            log.error("Error sending resetting password (User not found): {}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            log.error("Internal server error: {}", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Internal server error").build();
        }
    }
}