package uk.gov.hmcts.reform.divorce.auth;

import io.restassured.RestAssured;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.UUID;

@Service
public class IdamUserSupport {
    /**
     * The idam user base url.
     */
    @Value("${auth.idam.client.baseUrl}")
    private String idamUserBaseUrl;

    /**
     * The username for a valid idam user
     */
    private String idamUsername;

    /**
     * The password for a valid idam user
     */
    private String idamPassword;

    private String idamCaseworkerUser;

    private String idamCaseworkerPassword;

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

    /**
     * Create a new user in IDAM
     */
    private void createUserInIdam() {
        idamUsername = "simulate-delivered" + UUID.randomUUID() + "@notifications.service.gov.uk";
        idamPassword = UUID.randomUUID().toString();
        RestAssured.given()
                .header("Content-Type", "application/json")
                .body("{\"email\":\"" + idamUsername + "\", \"forename\":\"Test\",\"surname\":\"User\",\"password\":\"" + idamPassword + "\"}")
                .post(idamCreateUrl());
    }


    /**
     * Create a new user in IDAM
     */
    private void createCaseworkerUserInIdam() {
        idamCaseworkerUser = "simulate-delivered" + UUID.randomUUID() + "@notifications.service.gov.uk";
        idamCaseworkerPassword = UUID.randomUUID().toString();
        RestAssured.given()
                .header("Content-Type", "application/json")
                .body("{\"email\":\"" + idamCaseworkerUser + "\", "
                        + "\"forename\":\"CaseWorkerTest\",\"surname\":\"User\",\"password\":\"" + idamCaseworkerPassword + "\", "
                        + "\"roles\":[\"caseworker-divorce\"], \"userGroup\":{\"code\":\"caseworker\"}}")
                .post(idamCreateUrl());
    }

    /**
     * Testing support url for Idam create user
     *
     * @return the string
     */
    private String idamCreateUrl() {
        return idamUserBaseUrl + "/testing-support/accounts";
    }

    /**
     * Login url.
     *
     * @return the string
     */
    private String loginUrl() {
        return idamUserBaseUrl + "/oauth2/authorize";
    }

    /**
     * Generate user token with no roles.
     *
     * @return the string
     */
    private String generateUserTokenWithNoRoles(String username, String password) {
        System.out.println(loginUrl());

        String userLoginDetails = String.join(":", username, password);
        final String authHeader = "Basic " + new String(Base64.getEncoder().encode((userLoginDetails).getBytes()));

        final String token = RestAssured.given()
                .header("Authorization", authHeader)
                .post(loginUrl())
                .body()
                .path("access-token");

        return "Bearer " + token;
    }
}
