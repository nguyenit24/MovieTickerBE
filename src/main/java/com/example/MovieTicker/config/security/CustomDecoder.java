package com.example.MovieTicker.config.security;

import com.nimbusds.jose.JOSEException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;
import com.example.MovieTicker.service.AuthenticateService;
import com.example.MovieTicker.request.IntrospectRequest;

import javax.crypto.spec.SecretKeySpec;
import java.text.ParseException;
import java.util.Objects;

@Component
public class CustomDecoder implements JwtDecoder
{
    @Value("${jwt.Key}")
    private String jwtSecret;

    @Autowired
    private AuthenticateService authenticateService;

    private NimbusJwtDecoder jwtDecoder = null;

    @Override
    public Jwt decode(String token) throws JwtException {
        try{
            var res = authenticateService.introspect(
                    IntrospectRequest.builder().
                            token(token).
                            build()
            );
            if(!res.isValid()){
                throw new JwtException("Invalid token");
            }
        } catch (JOSEException | ParseException e) {
            throw new JwtException(e.getMessage());
        }
        if(Objects.isNull(jwtDecoder)) {
            SecretKeySpec secretKeySpec = new SecretKeySpec(jwtSecret.getBytes(), "HS512");
            jwtDecoder=NimbusJwtDecoder.withSecretKey(secretKeySpec).macAlgorithm(MacAlgorithm.HS512).build();
        }
        return jwtDecoder.decode(token);
    }
}
