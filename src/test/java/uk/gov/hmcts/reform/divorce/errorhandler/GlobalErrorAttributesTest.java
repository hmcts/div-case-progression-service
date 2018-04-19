package uk.gov.hmcts.reform.divorce.errorhandler;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.boot.autoconfigure.web.DefaultErrorAttributes;
import org.springframework.web.context.request.RequestAttributes;

import javax.xml.bind.ValidationException;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class GlobalErrorAttributesTest {

    @Mock
    private RequestAttributes mockRequestAttributes;

    private GlobalErrorAttributes underTest;

    @Before
    public void setUp() {
        underTest = new GlobalErrorAttributes();
    }

    @Test
    public void getErrorAttributesShouldIncludeAllAttributesFromDefaultErrorAttributes() {
        given(mockRequestAttributes.getAttribute("javax.servlet.error.status_code", 0))
            .willReturn(400);
        given(mockRequestAttributes.getAttribute(DefaultErrorAttributes.class.getName() + ".ERROR", 0))
            .willReturn(new ValidationException("Value is invalid"));

        Map<String, Object> errorAttributes =
            underTest.getErrorAttributes(mockRequestAttributes, false);

        assertNotNull(errorAttributes.get("timestamp"));
        assertEquals(400, errorAttributes.get("status"));
        assertEquals("Bad Request", errorAttributes.get("error"));
        assertEquals("javax.xml.bind.ValidationException", errorAttributes.get("exception"));
        assertEquals("Value is invalid", errorAttributes.get("message"));
    }

    @Test
    public void getErrorAttributesShouldReturnErrorCodeWhenCorrectRequestAttributeIsSet() {
        given(mockRequestAttributes.getAttribute("javax.servlet.error.status_code", 0))
            .willReturn(400);
        given(mockRequestAttributes.getAttribute("javax.servlet.error.error_code", 0))
            .willReturn("validationFailure");
        given(mockRequestAttributes.getAttribute(DefaultErrorAttributes.class.getName() + ".ERROR", 0))
            .willReturn(new ValidationException("Value is invalid"));

        Map<String, Object> errorAttributes =
            underTest.getErrorAttributes(mockRequestAttributes, false);

        assertEquals("validationFailure", errorAttributes.get("errorCode"));

    }

    @Test
    public void getErrorAttributesShouldNotReturnErrorCodeWhenTheErrorCodeRequestAttributeIsMissing() {
        given(mockRequestAttributes.getAttribute("javax.servlet.error.status_code", 0))
            .willReturn(400);
        given(mockRequestAttributes.getAttribute(DefaultErrorAttributes.class.getName() + ".ERROR", 0))
            .willReturn(new ValidationException("Value is invalid"));

        Map<String, Object> errorAttributes =
            underTest.getErrorAttributes(mockRequestAttributes, false);

        assertNull(errorAttributes.get("errorCode"));

    }
}
