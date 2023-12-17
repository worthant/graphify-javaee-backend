package com.worthant.javaee.controller;

import com.worthant.javaee.exceptions.AuthenticationException;
import com.worthant.javaee.exceptions.ServerException;
import com.worthant.javaee.exceptions.UserExistsException;
import com.worthant.javaee.service.AuthService;
import com.worthant.javaee.dto.TokenDTO;
import com.worthant.javaee.dto.UserDTO;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

@Path("/auth")
@Slf4j
public class AuthController {

    @Inject
    private AuthService authService;

    @POST
    @Path("/signup")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response signUp(@Valid UserDTO userDto) {
        try {
            String token = authService.registerUser(userDto.getUsername(), userDto.getPassword());
            log.info("Authorization successful!)");
            return Response.ok(new TokenDTO(token))
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Credentials", "true")
                    .header("Access-Control-Allow-Headers",
                            "origin, content-type, accept, authorization")
                    .header("Access-Control-Allow-Methods",
                            "GET, POST, PUT, DELETE, OPTIONS, HEAD").build();
        } catch (UserExistsException e) {
            log.error(e.getMessage());
            return Response.status(Response.Status.CONFLICT).entity(e.getMessage()).build();
        } catch (ServerException e) {
            log.error(e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@Valid UserDTO userDto) {
        try {
            String token = authService.authenticateUser(userDto.getUsername(), userDto.getPassword());
            log.info("Login successful for user: {}", userDto.getUsername());
            return Response.ok(new TokenDTO(token)).build();
        } catch (AuthenticationException e) {
            log.error("Login failed for user: {}", userDto.getUsername());
            return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build();
        } catch (ServerException e) {
            log.error("Internal server error: {}", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/logout")
    public Response logout() {
        // TODO: save theme settings on logout
        // TODO: save user session length for displaying it on admin console
        log.info("User logged out successfully.");
        return Response.ok().entity("User logged out successfully.").build();
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
}