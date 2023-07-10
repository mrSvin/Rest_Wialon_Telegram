package SpringWialonApplication.service;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;

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

    public String getNameJWT(String jwt) {

        String name = Jwts.parser()
                .setSigningKey(DatatypeConverter.parseBase64Binary(SECRET_KEY))
                .parseClaimsJws(jwt).getBody().get("name").toString();
        return name;
    }

    public boolean decodeJWT(String jwt) {

        //This line will throw an exception if it is not a signed JWS (as expected)
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(DatatypeConverter.parseBase64Binary(SECRET_KEY))
                    .parseClaimsJws(jwt).getBody();
            return true;
        } catch (Exception e) {
            System.out.println("Ошибка токена " + e);
            return false;
        }

    }

}
