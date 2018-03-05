package uk.gov.hmcts.reform.divorce.common;

import com.nimbusds.jwt.JWTParser;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.divorce.transformservice.domain.Jwt;
import uk.gov.hmcts.reform.divorce.errorhandler.JwtParsingException;

import java.text.ParseException;
import java.util.Date;
import java.util.Map;

@Component
public class JwtFactory {
    public Jwt create(String encodedJwt) {
        String jwt = encodedJwt.replaceFirst("Bearer ", "");

        try {
            Map<String, Object> claims = JWTParser.parse(jwt).getJWTClaimsSet().getClaims();

            return Jwt.builder()
                    .defaultUrl(((String) claims.get("default-url")))
                    .subject(((String) claims.get("sub")))
                    .data(((String) claims.get("data")))
                    .type(((String) claims.get("type")))
                    .forename(((String) claims.get("forename")))
                    .surname(((String) claims.get("surname")))
                    .id(Long.parseLong((String) claims.get("id")))
                    .expiration(new java.sql.Date(((Date) claims.get("exp")).getTime()).toLocalDate())
                    .issuedAt(new java.sql.Date(((Date) claims.get("iat")).getTime()).toLocalDate())
                    .jwtId(((String) claims.get("jti")))
                    .defaultService(((String) claims.get("default-service")))
                    .levelOfAssurance(((Long) claims.get("loa")))
                    .group(((String) claims.get("group")))
                    .build();
        }
        catch (ParseException e) {
            throw new JwtParsingException("Exception while parsing JWT",e);
        }
    }
}
