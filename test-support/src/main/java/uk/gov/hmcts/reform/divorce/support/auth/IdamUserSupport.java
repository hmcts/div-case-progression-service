package uk.gov.hmcts.reform.divorce.support.auth;

import io.restassured.RestAssured;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.divorce.support.auth.impl.IDAMUtilsImpl;

import java.util.UUID;

@Service
public class IdamUserSupport {

    IDAMUtils idamUtils;

    public IdamUserSupport() {
         this.idamUtils = new IDAMUtilsImpl();
    }

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
        return idamUtils.generateUserTokenWithNoRoles(username, password);
    }

    public synchronized String getIdamTestUser() {
        if (StringUtils.isBlank(testUserJwtToken)) {
            createUserAndToken();
        }
        return testUserJwtToken;
    }

    protected void createUserAndToken() {
        createUserInIdam();
        testUserJwtToken = idamUtils.generateUserTokenWithNoRoles(idamUsername, idamPassword);
    }

    public synchronized String getIdamTestCaseWorkerUser() {
        if (StringUtils.isBlank(testCaseworkerJwtToken)) {
            createCaseworkerUserInIdam();
            testCaseworkerJwtToken = idamUtils.generateUserTokenWithNoRoles(idamCaseworkerUser, idamCaseworkerPw);
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

        idamUtils.createUserInIdam(idamUsername, idamPassword);
    }

    private void createCaseworkerUserInIdam() {
        RestAssured.given()
                .header("Content-Type", "application/json")
                .body("{\"email\":\"" + idamCaseworkerUser + "\", "
                        + "\"forename\":\"CaseWorkerTest\",\"surname\":\"User\",\"password\":\""
                    + idamCaseworkerPw + "\", " + "\"roles\":[\"caseworker-divorce-courtadmin\"],"
                    + " \"userGroup\":{\"code\":\"caseworker\"}}")
                .post(idamCreateUrl());
    }

    private String idamCreateUrl() {
        return idamUserBaseUrl + "/testing-support/accounts";
    }


}
