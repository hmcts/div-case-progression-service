package uk.gov.hmcts.reform.divorce.support.auth;

import io.restassured.RestAssured;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.UUID;

@Service
public class IdamUserSupport {

    private static final String idamCaseworkerUser = "CaseWorkerTest";

    private static final String idamCaseworkerPw = "password";

    @Value("${auth.idam.client.baseUrl}")
    private String idamUserBaseUrl;

    private String idamUsername;

    private String idamPassword;

    private String testUserJwtToken;

    private String testCaseworkerJwtToken;

    public String generateNewUserAndReturnToken() {
        String username = "simulate-delivered" + UUID.randomUUID() + "@notifications.service.gov.uk";
        String password = UUID.randomUUID().toString();
        createUserInIdam(username, password);
        return generateUserTokenWithNoRoles(username, password);
    }

    public synchronized String getIdamTestUser() {
        if (StringUtils.isBlank(testUserJwtToken)) {
            createUserAndToken();
        }
        return testUserJwtToken;
    }

    protected void createUserAndToken() {
        createUserInIdam();
        testUserJwtToken = generateUserTokenWithNoRoles(idamUsername, idamPassword);
    }

    public synchronized String getIdamTestCaseWorkerUser() {
        if (StringUtils.isBlank(testCaseworkerJwtToken)) {
            createCaseworkerUserInIdam();
            testCaseworkerJwtToken = generateUserTokenWithNoRoles(idamCaseworkerUser, idamCaseworkerPw);
        }

        return testCaseworkerJwtToken;
    }

    private void createUserInIdam(String username, String password) {
        RestAssured.given()
            .header("Content-Type", "application/json")
            .body("{\"email\":\"" + username + "\", \"forename\":\"Test\",\"surname\":\"User\",\"password\":\""
                + password + "\"}")
            .post(idamCreateUrl());
    }

    private void createUserInIdam() {
        idamUsername = "simulate-delivered" + UUID.randomUUID() + "@notifications.service.gov.uk";
        idamPassword = UUID.randomUUID().toString();

        createUserInIdam(idamUsername, idamPassword);
    }

    private void createCaseworkerUserInIdam() {
        RestAssured.given()
                .header("Content-Type", "application/json")
                .body("{\"email\":\"" + idamCaseworkerUser + "\", "
                        + "\"forename\":\"CaseWorkerTest\",\"surname\":\"User\",\"password\":\""
                    + idamCaseworkerPw + "\", " + "\"roles\":[\"caseworker-divorce.support\"],"
                    + " \"userGroup\":{\"code\":\"caseworker\"}}")
                .post(idamCreateUrl());
    }

    private String idamCreateUrl() {
        return idamUserBaseUrl + "/testing-support/accounts";
    }

    private String generateUserTokenWithNoRoles(String username, String password) {
        final String encoded = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
        final String token = RestAssured.given().baseUri(idamUserBaseUrl)
            .header("Authorization", "Basic " + encoded)
            .post("/oauth2/authorize?response_type=token&client_id=divorce.support&redirect_uri="
                + "https://www.preprod.ccd.reform.hmcts.net/oauth2redirect")
            .body()
            .path("access-token");

        return "Bearer " + token;
    }
}
