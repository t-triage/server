/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.aaa.filter;

import com.clarolab.aaa.exception.OAuth2AuthenticationProcessingException;
import com.clarolab.aaa.jwt.TokenProvider;
import com.clarolab.aaa.model.UserPrincipal;
import com.clarolab.aaa.user.CustomUserDetailsService;
import com.clarolab.aaa.util.SecurityLog;
import com.clarolab.model.User;
import com.clarolab.model.types.RoleType;
import com.clarolab.service.UserService;
import com.clarolab.util.Constants;
import com.google.common.collect.Lists;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * This filter will authenticate all request with a JWT token present and valid.
 * The request missing JWT will follow the chain and be rejected by RestAuthenticationEntryPoint in the last scenario.
 */
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private SecurityLog logger;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwt = getJwtFromRequest(request);
            setIP(request);

            if (StringUtils.hasText(jwt) && !"null".equals(jwt) && tokenProvider.validateToken(jwt)) {
                Long userId = tokenProvider.getUserIdFromToken(jwt);
                UserDetails userDetails;
                if(userId==0){
                    List<GrantedAuthority> authorities = Lists.newArrayList();
                    authorities.add(new SimpleGrantedAuthority(RoleType.ROLE_SERVICE.name()));
                    userDetails = new UserPrincipal(0L,"","", authorities);
                }
                else {
                    User loggedUser = customUserDetailsService.getUserById(userId);
                    if (userService.canLogin(loggedUser)) {
                        userDetails = customUserDetailsService.loadUser(loggedUser);
                    } else {
                        throw new OAuth2AuthenticationProcessingException("Could not authenticate user");
                    }

                }

               // UserDetails userDetails = customUserDetailsService.loadUserById(userId);

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private void setIP(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (StringUtils.isEmpty(ip)) {
            ip = request.getRemoteAddr();
        }
        MDC.put(Constants.USER_IP, ip);
    }
}
