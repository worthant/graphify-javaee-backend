package com.worthant.javaee.filters;

import com.worthant.javaee.Role;
import com.worthant.javaee.auth.JwtProvider;
import com.worthant.javaee.auth.UserPrincipal;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;

import java.security.Principal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Provider
@Slf4j
@Priority(Priorities.AUTHORIZATION)
public class JwtAuthorizationFilter implements ContainerRequestFilter {

    @Inject
    private JwtProvider jwtProvider;


    private static final Set<String> SKIP_PATHS = new HashSet<>(Arrays.asList(
            "/auth/signup",
            "/auth/login",
            "/auth/admin"
    ));

    @Override
    public void filter(ContainerRequestContext requestContext) {
        String path = requestContext.getUriInfo().getPath();
        if (SKIP_PATHS.contains(path)) {
            return; // Skip JWT check for specified paths
        }
        System.out.println(path);
        log.info(path);

        String authorizationHeader = requestContext.getHeaderString("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            requestContext.abortWith(Response
                    .status(Response.Status.UNAUTHORIZED)
                    .entity("Authorization token is required")
                    .build());
            return;
        }


        String token = authorizationHeader.substring("Bearer ".length());
        String username = jwtProvider.getUsernameFromToken(token);
        Role role = jwtProvider.getRoleFromToken(token);
        Long userId = jwtProvider.getUserIdFromToken(token);

        if (username == null || role == null || userId == null) {
            requestContext.abortWith(Response
                    .status(Response.Status.UNAUTHORIZED)
                    .entity("Invalid or expired token")
                    .build());
            return;
        }

        SecurityContext originalContext = requestContext.getSecurityContext();
        requestContext.setSecurityContext(new SecurityContext() {
            @Override
            public Principal getUserPrincipal() {
                return new UserPrincipal(username, userId, role);
            }

            @Override
            public boolean isUserInRole(String roleName) {
                return Optional.of(role)
                        .map(Enum::name)
                        .map(String::toUpperCase)
                        .map(roleName::equalsIgnoreCase)
                        .orElse(false);
            }

            @Override
            public boolean isSecure() {
                return originalContext.isSecure();
            }

            @Override
            public String getAuthenticationScheme() {
                return "Bearer";
            }
        });
    }
}
