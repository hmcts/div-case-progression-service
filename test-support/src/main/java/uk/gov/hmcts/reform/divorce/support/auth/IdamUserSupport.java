package uk.gov.hmcts.reform.divorce.support.auth;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;

@Service
public class IdamUserSupport {

    @Value("${auth.idam.client.baseUrl}")
    private String idamUserBaseUrl;

    @Value("${auth.idam.secret}")
    private String idamSecret;

    @Value("${auth.idam.redirect.url}")
    private String idamRedirectUrl;

    public String getIdamTestUser(String username, String password) {
        createCitizen(username, password);
        return generateClientToken(username, password);
    }

    public String getIdamTestCaseWorkerUser(String username, String password) {
        createCaseworkerUserInIdam(username, password);
        return generateClientToken(username, password);
    }

    private void createCitizen(String username, String password) {
        Response response = RestAssured.given()
            .header("Content-Type", "application/json")
            .body("{\"email\":\"" + username + "\", \"forename\":\"Test\",\"surname\":\"User\",\"password\":\"" + password + "\"}")
            .post(idamCreateUrl());
        System.out.println(String.format("Created citizen with response code %s and body %s", response.statusCode(), response.body().print()));
    }

    private void createCaseworkerUserInIdam(String username, String password) {
        Response response = RestAssured.given()
            .header("Content-Type", "application/json")
            .body("{\"email\":\"" + username + "\", "
                + "\"forename\":\"CaseWorkerTest\",\"surname\":\"User\",\"password\":\"" + password + "\", "
                + "\"roles\":[\"caseworker-divorce\"], \"userGroup\":{\"code\":\"caseworker\"}}")
            .post(idamCreateUrl());
        System.out.println(String.format("Created case worker with response code %s and body %s", response.statusCode(), response.body().print()));
    }

    private String idamCreateUrl() {
        return idamUserBaseUrl + "/testing-support/accounts";
    }

    private String generateClientToken(String username, String password) {
        String code = generateClientCode(username, password);

        ResponseBody res = RestAssured.given().post(idamUserBaseUrl + "/oauth2/token?code=" + code +
            "&client_secret=" + idamSecret +
            "&client_id=divorce" +
            "&redirect_uri=" + idamRedirectUrl +
            "&grant_type=authorization_code")
            .body();
        System.out.println("Generate client token response body");
        System.out.println(res.print());
        String token = res.path("access_token");

        return "Bearer " + token;
    }

    private String generateClientCode(String username, String password) {
        String encoded = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());

        ResponseBody res = RestAssured.given().baseUri(idamUserBaseUrl)
            .header("Authorization", "Basic " + encoded)
            .post("/oauth2/authorize?response_type=code&client_id=divorce&redirect_uri=" + idamRedirectUrl)
            .body();
        System.out.println("Generate client code response body");
        System.out.println(res.print());

        return res.path("code");
    }

}
