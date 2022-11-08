package com.nanum.board.boardservice.board.security.jwt;

import com.nanum.board.boardservice.board.vo.UserResponse;
import com.nanum.board.boardservice.board.vo.UsersResponse;
import com.nanum.board.boardservice.client.UserServiceClient;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    private final UserServiceClient userServiceClient;

    @Value("${token.expiration_time}")
    private Long tokenValidTime;

    @Value("${token.secret}")
    private String secretKey;

    public String getUserPk(String header) {
        String token = header.substring("Bearer ".length());
        log.info(String.valueOf(Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody()));
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    public String getId(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().get("Id").toString();
    }

    public String resolveToken() {
        HttpServletRequest request = ((ServletRequestAttributes)
                RequestContextHolder.currentRequestAttributes()).getRequest();
        return request.getHeader("Authorization") != null ? request.getHeader("Authorization").replaceAll("Bearer ", "") : null;
    }

    public String getUserId(String token) {
        UsersResponse users = userServiceClient.getUsersByEmail(token);
        return String.valueOf(users.getUserId());
    }

    public String customResolveToken() {
        HttpServletRequest request = ((ServletRequestAttributes)
                RequestContextHolder.currentRequestAttributes()).getRequest();
        return request.getHeader("Authorization");
    }

    public boolean validateToken(String jwtToken) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}
