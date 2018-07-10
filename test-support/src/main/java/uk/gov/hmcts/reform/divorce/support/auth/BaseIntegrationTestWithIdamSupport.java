package uk.gov.hmcts.reform.divorce.support.auth;

import com.nimbusds.jwt.JWTParser;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import uk.gov.hmcts.reform.divorce.support.IntegrationTest;
import uk.gov.hmcts.reform.divorce.support.auth.model.ServiceAuthTokenFor;

import java.text.ParseException;
import java.util.Map;
import java.util.UUID;

public abstract class BaseIntegrationTestWithIdamSupport extends IntegrationTest {

    @Value("${auth.idam.test.invalid.jwt}")
    private String invalidToken;

    @Autowired
    private IdamUserSupport idamUserSupport;

    @Autowired
    private ServiceAuthSupport serviceAuthSupport;

    protected String getIdamTestUser() {
        String username = "simulate-delivered" + UUID.randomUUID() + "@notifications.service.gov.uk";
        String password = RandomStringUtils.random(32, 0, 20, true, true, "qw32rfHIJk9iQ8Ud7h0X".toCharArray());
        return idamUserSupport.getIdamTestUser(username, password);
    }


    protected String getIdamTestCaseWorkerUser() {
        String username = "simulate-delivered-caseworker" + UUID.randomUUID() + "@notifications.service.gov.uk";
        String password = RandomStringUtils.random(32, 0, 20, true, true, "qw32rfHIJk9iQ8Ud7h0X".toCharArray());

        return idamUserSupport.getIdamTestCaseWorkerUser(username, password);
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

    protected String getServiceToken(ServiceAuthTokenFor serviceAuthTokenFor) {
        return serviceAuthSupport.getServiceAuthTokenFor(serviceAuthTokenFor);
    }

    protected ServiceAuthTokenFor getServiceAuthTokenFor() {
        return null;
    }

    protected String getInvalidToken() {
        return this.invalidToken;
    }
}
