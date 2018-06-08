package uk.gov.hmcts.reform.divorce.caseprogression.transformapi;

import io.restassured.response.Response;
import org.springframework.http.HttpStatus;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;

public class TestUtil {

    private static final String CASE_ID = "caseId";
    private static final String ERROR = "error";
    private static final Long ZERO_VALUE = new Long(0L);

    static void assertOkResponseAndCaseIdIsNotZero(Response ccdResponse) {
        assertEquals(Integer.valueOf(HttpStatus.OK.toString()).intValue(), ccdResponse.getStatusCode());

        assertNotEquals(ZERO_VALUE, extractCaseId(ccdResponse));
    }

    public static Long extractCaseId(Response ccdResponse) {
        Object caseId = ccdResponse.getBody().path(CASE_ID);
        return caseId == null ? null : Long.parseLong(caseId.toString());
    }

    static void assertResponseErrorsAreAsExpected(Response ccdResponse, String exception, String details) {
        assertEquals(Integer.valueOf(HttpStatus.OK.toString()).intValue(), ccdResponse.getStatusCode());

        assertEquals(ZERO_VALUE, extractCaseId(ccdResponse));
        assertEquals(ERROR, ccdResponse.getBody().path("status").toString());

        assertThat(ccdResponse.getBody().path(ERROR).toString(), containsString(exception));
        assertThat(ccdResponse.getBody().path(ERROR).toString(), containsString(details));
    }
}
