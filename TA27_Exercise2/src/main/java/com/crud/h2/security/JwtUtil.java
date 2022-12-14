package com.crud.h2.security;

import static java.util.Collections.emptyList;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JwtUtil {
	// Method to create the JWT and send it to the client in the response header
    static void addAuthentication(HttpServletResponse res, String username) {

        String token = Jwts.builder()
            .setSubject(username)

            // Let's assign an expiration time of 10 minutes
            // only for demonstration purposes in the video at the end
            .setExpiration(new Date(System.currentTimeMillis() + 600000))

         // Hash with which we will sign the key
            .signWith(SignatureAlgorithm.HS512, "P@tit0")
            .compact();

        //add the token to the header
        res.addHeader("Authorization", "Bearer " + token);
    }

 // Method to validate the token sent by the client
    static Authentication getAuthentication(HttpServletRequest request) {

    	// We get the token that comes in the request header
        String token = request.getHeader("Authorization");

        // if there is a token present, then we validate it
        if (token != null) {
            String user = Jwts.parser()
                    .setSigningKey("P@tit0")
                    .parseClaimsJws(token.replace("Bearer", "")) //este metodo es el que valida
                    .getBody()
                    .getSubject();

            // We remember that for the other requests that are not /login
            // we don't require username/password authentication
            // for this reason we can return a UsernamePasswordAuthenticationToken without a password
            return user != null ?
                    new UsernamePasswordAuthenticationToken(user, null, emptyList()) :
                    null;
        }
        return null;
    }
}
