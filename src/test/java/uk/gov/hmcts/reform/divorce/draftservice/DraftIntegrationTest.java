package uk.gov.hmcts.reform.divorce.draftservice;

import io.restassured.response.Response;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.reform.divorce.caseprogression.draftsapi.DraftBaseIntegrationTest;
import uk.gov.hmcts.reform.divorce.caseprogression.transformapi.TestUtil;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(
        properties = {
                "ccd.caseDataStore.baseUrl=http://ccd-data-store-api-aat.service.core-compute-aat.internal",
                "auth.provider.service.client.baseUrl=http://rpe-service-auth-provider-aat.service.core-compute-aat.internal",
                "idam.s2s-auth.url=http://rpe-service-auth-provider-aat.service.core-compute-aat.internal",
                "draft.store.api.baseUrl=http://draft-store-service-aat.service.core-compute-aat.internal",
                "idam.api.url=https://preprod-idamapi.reform.hmcts.net:3511",
                "draft.api.ccd.check.enabled=true"
        },
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(properties = "auth.idam.client.baseUrl=https://preprod-idamapi.reform.hmcts.net:3511")
public class DraftIntegrationTest extends DraftBaseIntegrationTest {

    @Test
    public void shouldReturnCaseDataIfDraftDoesNotExistButCaseExistsInCcd() throws Exception {

        // given
        Response caseSubmissionResponse = submitCase("addresses2.json");

        // when
        Response draftResponse = getDivorceDraft();

        // then
        Long caseId = TestUtil.extractCaseId(caseSubmissionResponse);
        Long draftResponseCaseId = new Long(draftResponse.getBody().path("caseId").toString());
        Boolean submissionStarted = new Boolean(draftResponse.getBody().path("submissionStarted").toString());
        String courts = draftResponse.getBody().path("courts").toString();

        assertEquals(caseId, draftResponseCaseId);
        assertEquals("eastMidlands", courts);
        assertEquals(true, submissionStarted);
    }
}
