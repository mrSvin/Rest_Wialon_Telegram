package SpringWialonApplication.service;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    @Value("${jwt-secret}")
    private String SECRET_KEY;

    public String checkJWT(String jwt, String roles) {
        try {
            Object roleJwt = Jwts.parser()
                    .setSigningKey(DatatypeConverter.parseBase64Binary(SECRET_KEY))
                    .parseClaimsJws(jwt).getBody().get("role");
            if (roles.equals("admin")) {
                if (!roleJwt.equals("ROLE_ADMIN")) {
                    return "no rights";
                }
            }
            return "ok";
        } catch (Exception e) {
            System.out.println("Ошибка токена " + e);
            return "error";
        }

    }

}
