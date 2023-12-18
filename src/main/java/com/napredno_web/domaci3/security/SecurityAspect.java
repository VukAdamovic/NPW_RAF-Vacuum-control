package com.napredno_web.domaci3.security;

import com.napredno_web.domaci3.security.service.TokenService;
import io.jsonwebtoken.Claims;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.lang.reflect.Method;
import java.util.Arrays;

@Aspect
@Configuration
public class SecurityAspect {

    @Value("${oauth.jwt.secret}")
    private String jwtSecret;

    private TokenService tokenService;

    public SecurityAspect(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Around("@annotation(com.napredno_web.domaci3.security.CheckSecurity)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        //Get method signature
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        //Check for authorization parameter
        String token = null;
        for (int i = 0; i < methodSignature.getParameterNames().length; i++) {
            if (methodSignature.getParameterNames()[i].equals("authorization")) {
                //Check bearer schema
                if (joinPoint.getArgs()[i].toString().startsWith("Bearer")) {
                    //Get token
                    token = joinPoint.getArgs()[i].toString().split(" ")[1];
                }
            }
        }
        //If token is not presents return UNAUTHORIZED response
        if (token == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        //Try to parse token
        Claims claims = tokenService.parseToken(token);
        //If fails return UNAUTHORIZED response
        if (claims == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        CheckSecurity checkSecurity = method.getAnnotation(CheckSecurity.class);
        String[] permissions = checkSecurity.permissions();

        for (String permission : permissions) {
            boolean allowed = false;
            switch (permission) {
                case "CREATE":
                    allowed = claims.get("create", Boolean.class);
                    break;
                case "READ":
                    allowed = claims.get("read", Boolean.class);
                    break;
                case "UPDATE":
                    allowed = claims.get("update", Boolean.class);
                    break;
                case "DELETE":
                    allowed = claims.get("delete", Boolean.class);
                    break;
                case "SEARCH":
                    allowed = claims.get("search", Boolean.class);
                    break;
                case "START":
                    allowed = claims.get("start", Boolean.class);
                    break;
                case "STOP":
                    allowed = claims.get("stop", Boolean.class);
                    break;
                case "DISCHARGE":
                    allowed = claims.get("discharge", Boolean.class);
                    break;
                case "ADD":
                    allowed = claims.get("add", Boolean.class);
                    break;
                case "REMOVE":
                    allowed = claims.get("remove", Boolean.class);
                    break;
            }

            if (allowed) {
                return joinPoint.proceed();
            }
        }
        //Return FORBIDDEN if user has't appropriate role for specified route
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }


}
