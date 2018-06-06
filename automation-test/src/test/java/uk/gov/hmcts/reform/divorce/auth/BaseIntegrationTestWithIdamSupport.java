package uk.gov.hmcts.reform.divorce.auth;

import com.nimbusds.jwt.JWTParser;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import uk.gov.hmcts.reform.divorce.IntegrationTest;
import uk.gov.hmcts.reform.divorce.auth.model.ServiceAuthTokenFor;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class BaseIntegrationTestWithIdamSupport extends IntegrationTest {

    @Value("${auth.idam.test.invalid.jwt}")
    private String invalidToken;

    @Autowired
    private IdamUserSupport idamUserSupport;

    @Autowired
    private ServiceAuthSupport serviceAuthSupport;

    private List<String> usersCreated;

    public void setUpClass() {
        usersCreated = new ArrayList<>();
    }

    public void tearDownClass() {
        idamUserSupport.deleteUsers(usersCreated);
    }

    protected String getIdamTestUser() {
        String username = "simulate-delivered" + UUID.randomUUID() + "@notifications.service.gov.uk";
        String password = "VerySecurePa$$w0rd";

        usersCreated.add(username);
        return idamUserSupport.getIdamTestUser(username, password);
    }

    protected String getIdamTestCaseWorkerUser() {
        String username = "simulate-delivered-caseworker" + UUID.randomUUID() + "@notifications.service.gov.uk";
        String password = "VerySecurePa$$w0rd";

        usersCreated.add(username);

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
