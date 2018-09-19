package uk.gov.hmcts.reform.divorce.caseprogression.e2e;

import io.restassured.response.Response;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.annotations.WithTag;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import uk.gov.hmcts.reform.divorce.support.auth.model.ServiceAuthTokenFor;
import uk.gov.hmcts.reform.divorce.support.caseprogression.BaseIntegrationTest;
import uk.gov.hmcts.reform.divorce.support.emclient.EvidenceManagementUtil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SerenityRunner.class)
public class CaseSubmissionMiniPetitionGenerationE2ETest extends BaseIntegrationTest {

    private static final String D8_MINI_PETITION_DOCUMENT_URL_PATH =
            "case_data.D8DocumentsGenerated[0].value.DocumentLink.document_url";
    private static final String D8_MINI_PETITION_DOCUMENT_BINARY_URL_PATH =
            "case_data.D8DocumentsGenerated[0].value.DocumentLink.document_binary_url";
    private static final String D8_MINI_PETITION_DOCUMENT_TYPE_PATH =
            "case_data.D8DocumentsGenerated[0].value.DocumentType";
    private static final String D8_MINI_PETITION_DOCUMENT_FILENAME_PATH =
            "case_data.D8DocumentsGenerated[0].value.DocumentLink.document_filename";
    private static final String PETITION = "petition";
    private static final String D8_MINI_PETITION_FILE_NAME_FORMAT = "d8petition%d.pdf";

    @Value("${document.management.store.baseUrl}")
    private String documentManagementURL;

    @Test
    @WithTag("test-type:e2e")
    public void submittingCaseAndIssuePetitionOnCcdShouldGeneratePDF() throws Exception {
        Response ccdResponse = submitCase("submit-complete-case.json");
        long caseId = assertAndGetCaseId(ccdResponse);

        Response ccdSubmitResponse = makePaymentAndIssuePetition(caseId);
        assertGeneratedDocumentExists(ccdSubmitResponse, caseId);
    }

    private void assertGeneratedDocumentExists(Response ccdSubmitResponse, long caseId){
        String documentUri = ccdSubmitResponse.path(D8_MINI_PETITION_DOCUMENT_BINARY_URL_PATH);

        assertNotNull(documentUri);
        assertNotNull(ccdSubmitResponse.path(D8_MINI_PETITION_DOCUMENT_URL_PATH));
        assertEquals(PETITION, ccdSubmitResponse.path(D8_MINI_PETITION_DOCUMENT_TYPE_PATH));
        assertEquals(String.format(D8_MINI_PETITION_FILE_NAME_FORMAT, caseId),
                ccdSubmitResponse.path(D8_MINI_PETITION_DOCUMENT_FILENAME_PATH));

        documentUri = EvidenceManagementUtil.getDocumentStoreURI(documentUri, documentManagementURL);

        Response documentManagementResponse =
                EvidenceManagementUtil.readDataFromEvidenceManagement(documentUri,
                    getServiceToken(ServiceAuthTokenFor.DIV_DOCUMENT_GENERATOR),
                    getIdamTestCaseWorkerUser());

        assertEquals(HttpStatus.OK.value(), documentManagementResponse.statusCode());
    }

    private long assertAndGetCaseId(Response ccdResponse){
        assertEquals(Integer.valueOf(HttpStatus.OK.toString()).intValue(), ccdResponse.getStatusCode());
        return Long.parseLong(ccdResponse.getBody().path("caseId").toString());
    }

    private Response makePaymentAndIssuePetition(long caseId) throws Exception {
        Response response = submitEvent(caseId, "paymentMade");

        assertNotNull(response.getBody().path("id"));

        response = submitEvent(caseId, "issueFromSubmitted");
        assertNotNull(response.getBody().path("id"));

        return response;
    }

}
