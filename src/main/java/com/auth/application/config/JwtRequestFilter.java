package com.auth.application.config;

import ch.qos.logback.core.net.SMTPAppenderBase;
import com.auth.application.model.User;
import com.auth.application.service.impl.UserServiceImpl;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private UserServiceImpl userServiceImpl;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        final String requestTokenHeader = request.getHeader("Authorization");



        String username = null;
        String jwtToken = null;
        // JWT Token is in the form "Bearer token". Remove Bearer word and get
        // only the Token
//        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
//            jwtToken = requestTokenHeader.substring(7);
//
//            try {
//                username = jwtTokenUtil.getUsernameFromToken(jwtToken);
//            } catch (IllegalArgumentException e) {
//                System.out.println("Unable to get JWT Token");
//            } catch (ExpiredJwtException e) {
//                logger.warn("JWT Token has expired");
//                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT Token has expired");
//                return;
//            }
//        } else {
//            logger.warn("Request khong co token trong header");
//        }
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);
        }

        // **🔹 2. Nếu không có trong Header, thử lấy từ Cookie**
        if (jwtToken == null) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("JWT_TOKEN".equals(cookie.getName())) {
                        jwtToken = cookie.getValue();
                        break;
                    }
                }
            }
        }

        // **🔹 3. Nếu có Token, tiến hành xác thực**
        if (jwtToken != null) {
            try {
                username = jwtTokenUtil.getUsernameFromToken(jwtToken);
            } catch (IllegalArgumentException e) {
                System.out.println("Không thể lấy JWT Token");
            } catch (ExpiredJwtException e) {
                logger.warn("JWT Token đã hết hạn");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT Token đã hết hạn");
                return;
            }
        } else {
            logger.warn("Không tìm thấy JWT Token trong Header hoặc Cookie");
        }

        // Once we get the token validate it.
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            User findUsername = userServiceImpl.findUserByUsername(username);
            final UserDetails userDetails = new org.springframework.security.core.userdetails.User(findUsername.getUsername(),findUsername.getPassword() ,new ArrayList<>());
            // if token is valid configure Spring Security to manually set
            // authentication
            if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {

                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // After setting the Authentication in the context, we specify
                // that the current user is authenticated. So it passes the
                // Spring Security Configurations successfully.
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }

        chain.doFilter(request, response);
    }
}
