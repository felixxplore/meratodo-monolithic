package com.felix.meratodo.filter;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final Map<String, Bucket> buckets=new ConcurrentHashMap<>();

    private Bucket createNewBucket(){
        Bandwidth limit = Bandwidth.builder().capacity(100).refillGreedy(100, Duration.ofHours(1)).build();
        return Bucket.builder().addLimit(limit).build();
    }

    private String getClientIp(HttpServletRequest request){
        String ip = request.getHeader("X-Forwarded-For");
        return ip!=null ? ip : request.getRemoteAddr();
    }



    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String path=request.getRequestURI();
        String clientIp=getClientIp(request);
        if(path.startsWith("/api/auth/login") || path.startsWith("/api/auth/password-reset-request")){
            Bucket bucket = buckets.computeIfAbsent(clientIp, k -> createNewBucket());

            if(!bucket.tryConsume(1)){
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.getWriter().write("Too Many attempts on this endpoint. Please wait");
                return;
            }
        }

        filterChain.doFilter(request,response);
    }
}
