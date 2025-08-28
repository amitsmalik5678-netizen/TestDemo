package com.example.demo.test.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TokenHeaderFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String tokenHeader = request.getHeader("token");
        String authHeader = request.getHeader("Authorization");

        if ((authHeader == null || authHeader.isBlank()) && tokenHeader != null && !tokenHeader.isBlank()) {
            HttpServletRequest wrapped = new AuthorizationHeaderRequestWrapper(request, "Bearer " + tokenHeader);
            filterChain.doFilter(wrapped, response);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private static class AuthorizationHeaderRequestWrapper extends HttpServletRequestWrapper {
        private final Map<String, String> extraHeaders = new HashMap<>();

        AuthorizationHeaderRequestWrapper(HttpServletRequest request, String authorizationValue) {
            super(request);
            extraHeaders.put("Authorization", authorizationValue);
        }

        @Override
        public String getHeader(String name) {
            String headerValue = extraHeaders.get(name);
            if (headerValue != null) {
                return headerValue;
            }
            return super.getHeader(name);
        }

        @Override
        public Enumeration<String> getHeaders(String name) {
            if (extraHeaders.containsKey(name)) {
                return Collections.enumeration(List.of(extraHeaders.get(name)));
            }
            return super.getHeaders(name);
        }

        @Override
        public Enumeration<String> getHeaderNames() {
            Set<String> names = new HashSet<>();
            Enumeration<String> original = super.getHeaderNames();
            while (original.hasMoreElements()) {
                names.add(original.nextElement());
            }
            names.addAll(extraHeaders.keySet());
            return Collections.enumeration(names);
        }
    }
}


