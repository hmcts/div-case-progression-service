package uk.gov.hmcts.reform.divorce.emclient;

import io.restassured.response.Response;
import net.serenitybdd.junit.spring.integration.SpringIntegrationMethodRule;
import net.serenitybdd.rest.SerenityRest;
import org.junit.Assert;
import org.junit.Rule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import uk.gov.hmcts.reform.divorce.auth.BaseIntegrationTestWithIdamSupport;
import uk.gov.hmcts.reform.divorce.auth.model.ServiceAuthTokenFor;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.notNullValue;

public abstract class BaseIntegrationTest extends BaseIntegrationTestWithIdamSupport {

    @Rule
    public SpringIntegrationMethodRule springMethodIntegration = new SpringIntegrationMethodRule();
    @Value("${evidence.management.client.api.baseUrl}")
    private String evidenceManagementClientApiBaseUrl;
    @Value("${evidence.management.client.api.endpoint.uploadwiths2stoken}")
    private String evidenceManagementClientApiUploadWithS2STokenEndpoint;
    @Value("${evidence.management.client.api.endpoint.upload}")
    private String evidenceManagementClientApiUploadEndpoint;
    @Value("${document.management.store.baseUrl}")
    private String documentManagementURL;

    /**
     * Make REST call to an emclient API with user token and validate the document is stored in EM Store
     *
     * @param fileName        the request body to be sent as a file
     * @param fileContentType the fileContentType represents the contentType of the file
     */
    void uploadFileAndVerifyStoredInEvidenceManagement(String fileName, String fileContentType) {
        Response response = SerenityRest.given()
                .headers(headers())
                .multiPart("file", new File("src/test/resources/FileTypes/" + fileName), fileContentType)
                .post(evidenceManagementClientApiBaseUrl.concat(evidenceManagementClientApiUploadEndpoint)).andReturn();

        String fileUrl = EvidenceManagementUtil.getDocumentStoreURI(
                ((List<String>) response.getBody().path("fileUrl")).get(0), documentManagementURL);

        Assert.assertEquals(HttpStatus.OK.value(), response.statusCode());

        Response responseFromEvidenceManagement =
                EvidenceManagementUtil.readDataFromEvidenceManagement(fileUrl, getIdamTestCaseWorkerUser());

        Assert.assertEquals(HttpStatus.OK.value(), responseFromEvidenceManagement.getStatusCode());
        Assert.assertEquals(fileName, responseFromEvidenceManagement.getBody().path("originalDocumentName"));
        Assert.assertEquals(fileContentType, responseFromEvidenceManagement.getBody().path("mimeType"));
    }

    /**
     * emclient API with user token
     *
     * @param fileName        the request body to be sent as a file
     * @param fileContentType the fileContentType represents the contentType of the file
     * @return the rest assured response
     */
    String invalidFileUpload(String fileName, String fileContentType) {
        return SerenityRest.given()
                .headers(headers())
                .multiPart("file", new File("src/test/resources/FileTypes/" + fileName), fileContentType)
                .post(evidenceManagementClientApiBaseUrl.concat(evidenceManagementClientApiUploadEndpoint)).then()
                .extract().asString();
    }


    /**
     * Make REST call to an emclient API with S2S token and validate the docuement is stored in EM Store
     *
     * @param fileName        the request body to be sent as a file
     * @param fileContentType the fileContentType represents the contentType of the file
     */
    void uploadFileWithS2STokenAndVerifyStoredInEmStore(String fileName, String fileContentType) {
        Response response = SerenityRest.given()
                .headers(S2STokenHeader())
                .multiPart("file", new File("src/test/resources/FileTypes/" + fileName), fileContentType)
                .post(evidenceManagementClientApiBaseUrl.concat(evidenceManagementClientApiUploadWithS2STokenEndpoint))
                .andReturn();

        String fileUrl = EvidenceManagementUtil.getDocumentStoreURI(
                ((List<String>) response.getBody().path("fileUrl")).get(0), documentManagementURL);

        Assert.assertEquals(HttpStatus.OK.value(), response.statusCode());

        Response responseFromEvidenceManagement =
                EvidenceManagementUtil.readDataFromEvidenceManagement(fileUrl, getIdamTestCaseWorkerUser());

        Assert.assertEquals(HttpStatus.OK.value(), responseFromEvidenceManagement.getStatusCode());
        Assert.assertEquals(fileName, responseFromEvidenceManagement.getBody().path("originalDocumentName"));
        Assert.assertEquals(fileContentType, responseFromEvidenceManagement.getBody().path("mimeType"));
    }

    /**
     * emclient API with s2s token
     *
     * @param fileName        the request body to be sent as a file
     * @param fileContentType the fileContentType represents the contentType of the file
     * @return the rest assured response
     */
    String invalidFileUploadWithS2SToken(String fileName, String fileContentType) {
        return SerenityRest.given()
                .headers(headers())
                .multiPart("file", new File("src/test/resources/FileTypes/" + fileName), fileContentType)
                .post(evidenceManagementClientApiBaseUrl.concat(evidenceManagementClientApiUploadWithS2STokenEndpoint)).then()
                .extract().asString();
    }

    Response extractUploadedFileUrl(String fileName, String fileContentType) {

        String extractedFileUrlFromResponse = SerenityRest.given()
                .headers(headers())
                .multiPart("file", new File("src/test/resources/FileTypes/" + fileName), fileContentType)
                .post(evidenceManagementClientApiBaseUrl.concat(evidenceManagementClientApiUploadEndpoint))
                .andReturn()
                .getBody()
                .path("fileUrl")
                .toString();

        String fileUrl = EvidenceManagementUtil.getDocumentStoreURI(
                extractedFileUrlFromResponse.substring(1, extractedFileUrlFromResponse.length() - 1),
                documentManagementURL);

        return EvidenceManagementUtil.readDataFromEvidenceManagement(fileUrl, getIdamTestCaseWorkerUser());
    }

    String bulkFileUpload(String[] fileName, String[] fileContentType) {
        return SerenityRest.given()
                .headers(headers())
                .multiPart("file", new File("src/test/resources/FileTypes/" + fileName[0]), fileContentType[0])
                .multiPart("file", new File("src/test/resources/FileTypes/" + fileName[1]), fileContentType[1])
                .multiPart("file", new File("src/test/resources/FileTypes/" + fileName[2]), fileContentType[2])
                .multiPart("file", new File("src/test/resources/FileTypes/" + fileName[3]), fileContentType[3])
                .multiPart("file", new File("src/test/resources/FileTypes/" + fileName[4]), fileContentType[4])
                .multiPart("file", new File("src/test/resources/FileTypes/" + fileName[5]), fileContentType[5])
                .multiPart("file", new File("src/test/resources/FileTypes/" + fileName[6]), fileContentType[6])
                .multiPart("file", new File("src/test/resources/FileTypes/" + fileName[7]), fileContentType[7])
                .multiPart("file", new File("src/test/resources/FileTypes/" + fileName[8]), fileContentType[8])
                .multiPart("file", new File("src/test/resources/FileTypes/" + fileName[9]), fileContentType[9])
                .post(evidenceManagementClientApiBaseUrl.concat(evidenceManagementClientApiUploadEndpoint)).then()
                .body("fileUrl", notNullValue())
                .statusCode(200).extract().asString();
    }

    /**
     * Setup headers required for POST to EMClient API
     *
     * @return map of objects
     */
    private Map<String, Object> headers() {
        Map<String, Object> headers = new HashMap<>();
        headers.put("authorizationToken", getIdamTestUser());
        headers.put("Content-Type", "multipart/form-data");
        return headers;
    }

    /**
     * Setup headers required for POST to EMClient api using S2SToken
     *
     * @return map of objects
     */
    private Map<String, Object> S2STokenHeader() {
        Map<String, Object> headers = new HashMap<>();
        headers.put("authorizationToken", getServiceToken());
        headers.put("Content-Type", "multipart/form-data");
        return headers;
    }

    @Override
    protected ServiceAuthTokenFor getServiceAuthTokenFor() {
        return ServiceAuthTokenFor.DOCUMENT_GENERATOR;
    }

}