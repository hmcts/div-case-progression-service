package uk.gov.hmcts.reform.divorce.validationservice.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.CoreCaseData;
import uk.gov.hmcts.reform.divorce.validationservice.client.ValidationClientImpl;
import uk.gov.hmcts.reform.divorce.validationservice.domain.ValidationRequest;
import uk.gov.hmcts.reform.divorce.validationservice.domain.ValidationResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ValidationServiceImplTest {

    @Mock
    ValidationClientImpl validationClient;

    @InjectMocks
    ValidationServiceImpl validationService;

    @Test
    public void shouldValidateCoreCaseData() {
        ObjectMapper objectMapper = new ObjectMapper();

        CoreCaseData coreCaseData = new CoreCaseData();
        coreCaseData.setD8legalProcess("divorce");
        coreCaseData.setD8MarriageDate("2000-01-01");

        ValidationRequest validationRequest = new ValidationRequest();
        validationRequest.setData(objectMapper.convertValue(coreCaseData, new TypeReference<Map<String, Object>>() {}));

        ValidationResponse validationResponse = new ValidationResponse();
        validationResponse.setValidationStatus("success");

        when(validationClient.validate(eq(validationRequest))).thenReturn(validationResponse);

        ValidationResponse response = validationService.validate(validationRequest);

        assertEquals(response.getValidationStatus(), "success");

        verify(validationClient).validate(eq(validationRequest));
        verifyNoMoreInteractions(validationClient);
    }

    @Test
    public void shouldReturnWarningsForValidateCoreCaseData() {
        ObjectMapper objectMapper = new ObjectMapper();

        CoreCaseData coreCaseData = new CoreCaseData();
        coreCaseData.setD8legalProcess("divorce");
        coreCaseData.setD8MarriageDate("1901-01-01");

        ValidationRequest validationRequest = new ValidationRequest();
        validationRequest.setData(objectMapper.convertValue(coreCaseData, new TypeReference<Map<String, Object>>() {}));

        ValidationResponse validationResponse = new ValidationResponse();
        validationResponse.setValidationStatus("failed");
        List<String> warnings = new ArrayList<String>();
        warnings.add("hello world");
        warnings.add("warning");
        validationResponse.setWarnings(warnings);

        when(validationClient.validate(eq(validationRequest))).thenReturn(validationResponse);

        ValidationResponse response = validationService.validate(validationRequest);

        assertEquals(response.getValidationStatus(), "failed");
        assertEquals(response.getWarnings().get(0), "hello world");
        assertEquals(response.getWarnings().get(1), "warning");

        verify(validationClient).validate(eq(validationRequest));
        verifyNoMoreInteractions(validationClient);
    }

    @Test
    public void shouldReturnErrorsForValidateCoreCaseData() {
        ObjectMapper objectMapper = new ObjectMapper();

        CoreCaseData coreCaseData = new CoreCaseData();
        coreCaseData.setD8legalProcess("divorce");
        coreCaseData.setD8MarriageDate("1901-01-01");

        ValidationRequest validationRequest = new ValidationRequest();
        validationRequest.setData(objectMapper.convertValue(coreCaseData, new TypeReference<Map<String, Object>>() {}));

        ValidationResponse validationResponse = new ValidationResponse();
        validationResponse.setValidationStatus("failed");
        List<String> errors = new ArrayList<String>();
        errors.add("hello world");
        errors.add("error");
        validationResponse.setErrors(errors);

        when(validationClient.validate(validationRequest)).thenReturn(validationResponse);

        ValidationResponse response = validationService.validate(validationRequest);

        assertEquals(response.getValidationStatus(), "failed");
        assertEquals(response.getErrors().get(0), "hello world");
        assertEquals(response.getErrors().get(1), "error");

        verify(validationClient).validate(eq(validationRequest));
        verifyNoMoreInteractions(validationClient);
    }
}