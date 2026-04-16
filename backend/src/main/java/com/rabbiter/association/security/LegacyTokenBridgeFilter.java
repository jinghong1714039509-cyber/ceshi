package com.rabbiter.association.security;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
public class LegacyTokenBridgeFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        String paramToken = request.getParameter("token");
        if (!StringUtils.hasText(paramToken) && StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            filterChain.doFilter(new TokenRequestWrapper(request, token), response);
            return;
        }
        filterChain.doFilter(request, response);
    }

    private static class TokenRequestWrapper extends HttpServletRequestWrapper {

        private final Map<String, String[]> params;

        TokenRequestWrapper(HttpServletRequest request, String token) {
            super(request);
            params = new HashMap<>(request.getParameterMap());
            params.put("token", new String[]{token});
        }

        @Override
        public String getParameter(String name) {
            String[] values = params.get(name);
            return values == null || values.length == 0 ? null : values[0];
        }

        @Override
        public Map<String, String[]> getParameterMap() {
            return Collections.unmodifiableMap(params);
        }

        @Override
        public String[] getParameterValues(String name) {
            return params.get(name);
        }
    }
}
