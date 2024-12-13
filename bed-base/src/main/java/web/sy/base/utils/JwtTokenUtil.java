package web.sy.base.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import web.sy.base.config.GlobalConfig;
import web.sy.base.exception.TokenAuthException;
import web.sy.base.pojo.entity.User;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class JwtTokenUtil {
    
    private static String getSecret() {
        String secret = GlobalConfig.getConfig().getJwtSecret();
        if (secret == null) {
            throw new IllegalStateException("jwt.secret not configured");
        }
        return secret;
    }
    
    private static long getExpiration() {
        Long expiration = GlobalConfig.getConfig().getJwtExpiration();
        if (expiration == null) {
            throw new IllegalStateException("jwt.expiration not configured");
        }
        return expiration*1000;
    }
    
    private static long getRefreshExpiration() {
        Long expiration = GlobalConfig.getConfig().getJwtExpiration();
        if (expiration == null) {
            throw new IllegalStateException("jwt.expiration not configured");
        }
        return expiration*1000;
    }
    
    private static Key getSigningKey() {
        byte[] keyBytes = getSecret().getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public static String generateToken(User userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "access");
        return createToken(claims, userDetails.getUsername(), getExpiration());
    }
    
    public static String generateRefreshToken(User userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refresh");
        return createToken(claims, userDetails.getUsername(), getRefreshExpiration());
    }

    private static String createToken(Map<String, Object> claims, String subject, long expiration) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public static String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public static Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }
    
    public static String getTokenType(String token) {
        return getClaimFromToken(token, claims -> claims.get("type", String.class));
    }

    public static <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private static Claims getAllClaimsFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new TokenAuthException("Token已过期");
        } catch (JwtException e) {
            throw new TokenAuthException("Token解析失败");
        }
    }

    public static Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public static Boolean validateToken(String token, User userDetails) {
        final String username = getUsernameFromToken(token);
        final String tokenType = getTokenType(token);
        return (username.equals(userDetails.getUsername()) 
                && !isTokenExpired(token)
                && "access".equals(tokenType));
    }
    
    public static Boolean validateRefreshToken(String token, User userDetails) {
        final String username = getUsernameFromToken(token);
        final String tokenType = getTokenType(token);
        return (username.equals(userDetails.getUsername()) 
                && !isTokenExpired(token)
                && "refresh".equals(tokenType));
    }
}