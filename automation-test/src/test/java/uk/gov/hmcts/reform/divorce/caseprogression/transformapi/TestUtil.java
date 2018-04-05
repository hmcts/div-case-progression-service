package uk.gov.hmcts.reform.divorce.caseprogression.transformapi;

import io.restassured.response.Response;
import org.springframework.http.HttpStatus;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;

public class TestUtil {

    /**
     * Assert caseId is not 0L
     *
     * @param ccdResponse the response from ccd
     */
    public static void assertOkResponseAndCaseIdIsNotZero(Response ccdResponse) {
        assertEquals(Integer.valueOf(HttpStatus.OK.toString()).intValue(), ccdResponse.getStatusCode());

        assertNotEquals(0L, Long.parseLong(ccdResponse.getBody().path("caseId").toString()));
    }

    /**
     * Assertions for errors
     *
     * @param ccdResponse the response from making the post to ccd
     * @param exception   the returned exception
     * @param details     the details of the returned exception, usually as part of the error message
     */
    public static void assertResponseErrorsAreAsExpected(Response ccdResponse, String exception, String details) {
        assertEquals(Integer.valueOf(HttpStatus.OK.toString()).intValue(), ccdResponse.getStatusCode());

        assertEquals(0L, Long.parseLong(ccdResponse.getBody().path("caseId").toString()));
        assertEquals("error", ccdResponse.getBody().path("status").toString());

        assertThat(ccdResponse.getBody().path("error").toString(), containsString(exception));
        assertThat(ccdResponse.getBody().path("error").toString(), containsString(details));
    }
}