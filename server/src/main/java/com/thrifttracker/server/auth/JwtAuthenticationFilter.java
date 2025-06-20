package com.thrifttracker.server.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component // Marks this as a Spring bean, making it a candidate for dependency injection.
@RequiredArgsConstructor // Creates a constructor for our final fields.
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    // OncePerRequestFilter is a helper class that guarantees this filter only runs once per request.

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Get the Authorization header from the request.
        final String authHeader = request.getHeader("Authorization");

        // 2. Check if the header is null or doesn't start with "Bearer ". If so, pass the request to the next filter.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return; // Stop execution of the filter here.
        }

        // 3. Extract the JWT from the header (it's the string after "Bearer ").
        final String jwt = authHeader.substring(7);

        // 4. Extract the username (email) from the token using our JwtService.
        final String userEmail = jwtService.extractUsername(jwt);

        // 5. Check if we have a username AND if the user is not already authenticated for this request.
        // The second check is important to avoid re-authenticating on every filter in the chain.
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // 6. Load the user's details from the database using the email from the token.
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            // 7. Check if the token is valid (matches the user details and is not expired).
            if (jwtService.isTokenValid(jwt, userDetails)) {
                // 8. If the token is valid, create an authentication token.
                // This is the object Spring Security uses to represent the authenticated user.
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null, // We don't need credentials as they are already authenticated by the token.
                        userDetails.getAuthorities()
                );
                // 9. Set additional details on the token from the HTTP request.
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 10. Update the SecurityContextHolder. This is the crucial step that "authenticates" the user for this request.
                SecurityContextHolder.getContext().setAuthentication(authToken);

            }
        }
        // 11. Pass the request and response to the next filter in the chain.
        filterChain.doFilter(request, response);
    }
}