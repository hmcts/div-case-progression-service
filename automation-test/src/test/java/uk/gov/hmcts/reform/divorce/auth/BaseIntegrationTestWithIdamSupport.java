package uk.gov.hmcts.reform.divorce.auth;

import com.nimbusds.jwt.JWTParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import uk.gov.hmcts.reform.divorce.IntegrationTest;
import uk.gov.hmcts.reform.divorce.auth.model.ServiceAuthTokenFor;

import java.text.ParseException;
import java.util.Map;

public abstract class BaseIntegrationTestWithIdamSupport extends IntegrationTest {

    @Value("${auth.idam.test.invalid.jwt}")
    private String invalidToken;

    @Autowired
    private IdamUserSupport idamUserSupport;

    @Autowired
    private ServiceAuthSupport serviceAuthSupport;

    protected String getIdamTestUser() {
        return idamUserSupport.getIdamTestUser();
    }

    protected String getIdamTestCaseWorkerUser() {
        return idamUserSupport.getIdamTestCaseWorkerUser();
    }

    protected String getServiceToken() {
        return getServiceToken(getServiceAuthTokenFor());
    }

    protected String getUserId(String encodedJwt) {
        String jwt = encodedJwt.replaceFirst("Bearer ", "");
        Map<String, Object> claims;
        try {
            claims = JWTParser.parse(jwt).getJWTClaimsSet().getClaims();

        } catch (ParseException e) {
            throw new IllegalStateException("Cannot find user from authorization token ", e);
        }
        return (String) claims.get("id");
    }

    private String getServiceToken(ServiceAuthTokenFor serviceAuthTokenFor) {
        return serviceAuthSupport.getServiceAuthTokenFor(serviceAuthTokenFor);
    }

    protected ServiceAuthTokenFor getServiceAuthTokenFor() {
        return null;
    }

    protected String getInvalidToken() {
        return this.invalidToken;
    }
}
