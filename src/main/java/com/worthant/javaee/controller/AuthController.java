package com.worthant.javaee.controller;

import com.worthant.javaee.service.AuthService;
import dto.Token;
import dto.User;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/auth")
public class AuthController {

    @Inject
    private AuthService authService;

    @POST
    @Path("/signup")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response signUp(User userDto) {
        String token = authService.registerUser(userDto.getUsername(), userDto.getPassword());
        return Response.ok().entity(new Token(token)).build();
    }
}