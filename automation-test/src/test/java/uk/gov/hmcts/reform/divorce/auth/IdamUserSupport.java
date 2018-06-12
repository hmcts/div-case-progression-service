package uk.gov.hmcts.reform.divorce.auth;

import io.restassured.RestAssured;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.UUID;

@Service
public class IdamUserSupport {

    private static final String idamCaseworkerUser = "CaseWorkerTest";

    private static final String idamCaseworkerPassword = "password";

    @Value("${env}")
    private String environment;

    @Value("${auth.idam.client.baseUrl}")
    private String idamUserBaseUrl;

    private String idamUsername;

    private String idamPassword;

    private String testUserJwtToken;

    private String testCaseworkerJwtToken;

    public synchronized String getIdamTestUser() {
        if (StringUtils.isBlank(testUserJwtToken)) {
            createUserInIdam();
            testUserJwtToken = generateUserTokenWithNoRoles(idamUsername, idamPassword);
        }

        return testUserJwtToken;
    }

    public synchronized String getIdamTestCaseWorkerUser() {
        if (StringUtils.isBlank(testCaseworkerJwtToken)) {
            createCaseworkerUserInIdam();
            testCaseworkerJwtToken = generateUserTokenWithNoRoles(idamCaseworkerUser, idamCaseworkerPassword);
        }

        return testCaseworkerJwtToken;
    }

    private void createUserInIdam() {
        idamUsername = "simulate-delivered" + UUID.randomUUID() + "@notifications.service.gov.uk";
        idamPassword = UUID.randomUUID().toString();
        RestAssured.given()
                .header("Content-Type", "application/json")
                .body("{\"email\":\"" + idamUsername + "\", \"forename\":\"Test\",\"surname\":\"User\",\"password\":\"" + idamPassword + "\"}")
                .post(idamCreateUrl());
    }

    private void createCaseworkerUserInIdam() {
        RestAssured.given()
                .header("Content-Type", "application/json")
                .body("{\"email\":\"" + idamCaseworkerUser + "\", "
                        + "\"forename\":\"CaseWorkerTest\",\"surname\":\"User\",\"password\":\"" + idamCaseworkerPassword + "\", "
                        + "\"roles\":[\"caseworker-divorce\"], \"userGroup\":{\"code\":\"caseworker\"}}")
                .post(idamCreateUrl());
    }

    private String idamCreateUrl() {
        return idamUserBaseUrl + "/testing-support/accounts";
    }

    private String generateUserTokenWithNoRoles(String username, String password) {
        final String encoded = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
        final String redirectUri = "saat".equalsIgnoreCase(environment)
            ? "https://ccd-case-management-web-saat.service.core-compute-saat.internal/oauth2redirect"
            : "https://www.preprod.ccd.reform.hmcts.net/oauth2redirect";
        final String token = RestAssured.given().baseUri(idamUserBaseUrl)
            .header("Authorization", "Basic " + encoded)
            .post("/oauth2/authorize?response_type=token&client_id=divorce&redirect_uri="
                + redirectUri)
            .body()
            .path("access-token");

        return "Bearer " + token;
    }
}
