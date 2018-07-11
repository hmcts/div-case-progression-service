package uk.gov.hmcts.reform.divorce.validationservice.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import uk.gov.hmcts.reform.divorce.CaseProgressionApplication;
import uk.gov.hmcts.reform.divorce.testutils.ObjectMapperTestUtil;
import uk.gov.hmcts.reform.divorce.validationservice.domain.ValidationRequest;
import uk.gov.hmcts.reform.divorce.validationservice.domain.ValidationResponse;
import uk.gov.hmcts.reform.divorce.validationservice.service.ValidationService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@RunWith(SpringRunner.class)
@WebMvcTest(ValidationController.class)
@ContextConfiguration(classes = CaseProgressionApplication.class)
public class ValidationControllerTest {

    private static final String VALIDATE_URL = "/validate";

    @Autowired
    private WebApplicationContext applicationContext;

    @MockBean
    private ValidationService validationService;

    private MockMvc mvc;

    @Before
    public void setUp() throws Exception {
        mvc = MockMvcBuilders.webAppContextSetup(applicationContext).build();
    }

    @Test
    public void givenValidationRequest_thenValidateTheRequest_expectNoErrors() throws Exception {
        ValidationRequest validationRequest = new ValidationRequest();

        ValidationResponse validationResponse = new ValidationResponse();

        when(validationService.validate(validationRequest)).thenReturn(validationResponse);

        mvc.perform(post(VALIDATE_URL)
            .content(ObjectMapperTestUtil.convertObjectToJsonString(validationRequest))
            .contentType(MediaType.APPLICATION_JSON_UTF8));

        verify(validationService).validate(validationRequest);
        verifyNoMoreInteractions(validationService);
    }
}
