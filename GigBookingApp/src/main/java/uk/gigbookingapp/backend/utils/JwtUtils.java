package uk.gigbookingapp.backend.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import uk.gigbookingapp.backend.entity.User;

import java.security.Key;
import java.util.Date;

public class JwtUtils {

    // 7 days expire
    private static final long expire = 7;

    // A secret key
    // It can be any string but long than 256 bits(64 bytes) because we use HS512 algorithm.
    // Do not disclose to others.
    private static final String secret =
                    "ItIsASecretKey1234567890" +
                    "ItIsASecretKey1234567890";

    private static final Key key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));

    private JwtUtils(){}

    public static String generateToken(User user){
        Date now = new Date();
        Date expiration = new Date(now.getTime() + 86400 * 1000 * expire);
        return Jwts.builder()
                .setHeaderParam("type", "JWT")
                .setSubject(user.getId())
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(key)
                .compact();
    }

    public static Claims getClatimsByToken(String token){
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
