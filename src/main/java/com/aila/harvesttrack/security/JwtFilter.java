package com.aila.harvesttrack.security;

import com.aila.harvesttrack.model.Owner;
import com.aila.harvesttrack.repository.OwnerRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private OwnerRepository ownerRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest  request,
            HttpServletResponse response,
            FilterChain         filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        // ── Skip if no token
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String token   = authHeader.substring(7);
            final Integer ownerId = jwtUtil.extractOwnerId(token);

            // ── Set authentication if not already set
            if (ownerId != null &&
                    SecurityContextHolder.getContext().getAuthentication() == null) {

                Owner owner = ownerRepository
                        .findByIdAndDeletedAtIsNull(ownerId)
                        .orElse(null);

                if (owner != null && jwtUtil.validateToken(token, ownerId)) {

                    // ── Set owner in security context
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    owner, null, new ArrayList<>()
                            );
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );
                    SecurityContextHolder.getContext()
                            .setAuthentication(authToken);

                    // ── Pass ownerId to request for controllers
                    request.setAttribute("ownerId", ownerId);
                }
            }
        } catch (Exception e) {
            // ── Invalid token — just continue without auth
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}