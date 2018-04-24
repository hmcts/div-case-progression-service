package uk.gov.hmcts.reform.divorce.caseprogression.transformapi;

import io.restassured.response.Response;
import org.springframework.http.HttpStatus;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;

class TestUtil {

    static void assertOkResponseAndCaseIdIsNotZero(Response ccdResponse) {
        assertEquals(Integer.valueOf(HttpStatus.OK.toString()).intValue(), ccdResponse.getStatusCode());

        assertNotEquals(0L, Long.parseLong(ccdResponse.getBody().path("caseId").toString()));
    }

    static void assertResponseErrorsAreAsExpected(Response ccdResponse, String exception, String details) {
        assertEquals(Integer.valueOf(HttpStatus.OK.toString()).intValue(), ccdResponse.getStatusCode());

        assertEquals(0L, Long.parseLong(ccdResponse.getBody().path("caseId").toString()));
        assertEquals("error", ccdResponse.getBody().path("status").toString());

        assertThat(ccdResponse.getBody().path("error").toString(), containsString(exception));
        assertThat(ccdResponse.getBody().path("error").toString(), containsString(details));
    }
}
