package uk.gov.hmcts.reform.divorce.divdocumentgenerator;

import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Value;
import uk.gov.hmcts.reform.divorce.auth.BaseIntegrationTestWithIdamSupport;
import uk.gov.hmcts.reform.divorce.emclient.EvidenceManagementUtil;
import uk.gov.hmcts.reform.divorce.util.ResourceLoader;

import static net.serenitybdd.rest.SerenityRest.given;

public abstract class BaseIntegrationTest extends BaseIntegrationTestWithIdamSupport {

    protected static final String DOCUMENT_URL_KEY = "url";
    protected static final String MIME_TYPE_KEY = "mimeType";
    protected static final String APPLICATION_PDF_MIME_TYPE = "application/pdf";
    static final String X_PATH_TO_URL = "_links.self.href";

    @Value("${document.generator.generate.pdf.uri}")
    private String divDocumentGeneratorURI;

    @Value("${document.management.store.baseUrl}")
    private String documentManagementURL;

    /**
     * This will call the Div Document Generator
     *
     * @param requestBody Json Request
     * @return response from the Div Document Generator
     */
    protected Response callDivDocumentGenerator(String requestBody) {
        return given()
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post(divDocumentGeneratorURI)
                .andReturn();
    }

    /**
     * Load JSON.
     *
     * @param fileName the file name
     * @return the string
     * @throws Exception then the resource cannot be loaded
     */
    String loadJSON(final String fileName) throws Exception {
        return ResourceLoader.loadJSON("documentgenerator/" + fileName);
    }

    /**
     * Given the uri it will update the url to corresponding localhost url for testing with docker
     *
     * @param uri the link to be updated
     * @return updated url
     */
    //this is a hack to make this work with the docker container
    protected String getDocumentStoreURI(String uri) {
        if (uri.contains("document-management-store:8080")) {
            return uri.replace("http://document-management-store:8080", documentManagementURL);
        }

        return uri;
    }

    protected Response readDataFromEvidenceManagement(String uri) {
        return EvidenceManagementUtil.readDataFromEvidenceManagement(uri, getIdamTestCaseWorkerUser());
    }
}