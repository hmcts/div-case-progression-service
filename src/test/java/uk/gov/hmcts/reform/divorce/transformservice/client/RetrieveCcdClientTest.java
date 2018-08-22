package uk.gov.hmcts.reform.divorce.transformservice.client;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;

@RunWith(MockitoJUnitRunner.class)
public class RetrieveCcdClientTest {

    private static final String USER_ID = "userId";
    private static final String JWT = "jwt";
    private static final String EXPECTED_URL = "http://expectedUrl";
    private static final String CASE_STATE_KEY = "state";
    private static final String REJECTED_STATE = "Rejected";
    private static final String SUBMITTED_STATE = "Submitted";

    @Mock
    private CcdClientConfiguration mockClientConfiguration;
    @Mock
    private RestTemplate mockRestTemplate;
    @Mock
    private TransformationHttpEntityFactory mockHttpEntityFactory;

    private HttpEntity<String> mockHttpEntity;

    private ResponseEntity<List> mockResponseEntity;

    private RetrieveCcdClient underTest;

    private List<Map<String, Object>> listOfCases;

    @Before
    public void setUp() {

        mockResponseEntity = mock(ResponseEntity.class);
        mockHttpEntity = mock(HttpEntity.class);

        given(mockClientConfiguration.getRetrieveCaseUrl(USER_ID))
            .willReturn(EXPECTED_URL);

        given(mockHttpEntityFactory.createRequestEntityForCcdGet(JWT))
            .willReturn(mockHttpEntity);

        given(mockRestTemplate.exchange(EXPECTED_URL,
            HttpMethod.GET,
            mockHttpEntity,
            List.class))
            .willReturn(mockResponseEntity);

        String checkCcdEnabled = "true";
        underTest = new RetrieveCcdClient(mockClientConfiguration,
            mockRestTemplate,
            mockHttpEntityFactory,
            checkCcdEnabled);
    }

    @Test
    public void getCases_should_return_list_of_cases() {

        //given
        List expectedListOfCases = mock(List.class);
        given(mockResponseEntity.getBody()).willReturn(expectedListOfCases);
        given(mockRestTemplate.exchange(EXPECTED_URL,
            HttpMethod.GET,
            mockHttpEntity,
            List.class))
                .willReturn(mockResponseEntity);

        // when
        listOfCases = underTest.getCases(USER_ID, JWT);

        // then
        assertSame(listOfCases, expectedListOfCases);
    }

    @Test
    public void getCases_should_not_call_ccd_if_feature_flag_is_disabled() {

        //given
        List expectedListOfCases = mock(List.class);
        given(mockResponseEntity.getBody()).willReturn(expectedListOfCases);
        String checkCcdEnabled = "false";
        underTest = new RetrieveCcdClient(mockClientConfiguration,
            mockRestTemplate,
            mockHttpEntityFactory,
            checkCcdEnabled);

        // when
        listOfCases = underTest.getCases(USER_ID, JWT);

        // then
        assertEquals(0, listOfCases.size());
        verifyZeroInteractions(mockRestTemplate,
            mockClientConfiguration,
            mockHttpEntityFactory);
    }

    @Test
    public void getCases_should_return_empty_list_if_no_cases_found() {

        //given
        listOfCases = Collections.emptyList();
        given(mockResponseEntity.getBody()).willReturn(listOfCases);
        given(underTest
            .getCases(USER_ID, JWT))
            .willReturn(listOfCases);

        //when
        List<Map<String, Object>> casesRetrieved = underTest.getCases(USER_ID, JWT);

        //then
        assertEquals(0, casesRetrieved.size());
    }

    @Test
    public void getNonRejectedCases_should_return_list_of_non_rejected_cases() {

        //given
        Map<String, Object> submittedCase = new HashMap<>();
        submittedCase.put(CASE_STATE_KEY, SUBMITTED_STATE);

        Map<String, Object> rejectedCase = new HashMap<>();
        rejectedCase.put(CASE_STATE_KEY, REJECTED_STATE);

        List<Map<String, Object>> expectedListOfCases = new ArrayList<>();
        expectedListOfCases.add(submittedCase);

        List<Map<String, Object>> responseListOfCases = new ArrayList<>();
        responseListOfCases.add(submittedCase);
        responseListOfCases.add(rejectedCase);

        given(mockResponseEntity.getBody()).willReturn(responseListOfCases);
        given(mockRestTemplate.exchange(EXPECTED_URL,
            HttpMethod.GET,
            mockHttpEntity,
            List.class))
            .willReturn(mockResponseEntity);

        // when
        listOfCases = underTest.getNonRejectedCases(USER_ID, JWT);

        // then
        assertEquals(expectedListOfCases, listOfCases);
    }

    @Test
    public void getNonRejectedCases_should_return_empty_list_if_only_rejected_cases_found() {
        //given
        Map<String, Object> rejectedCase = new HashMap<>();
        rejectedCase.put(CASE_STATE_KEY, REJECTED_STATE);

        List<Map<String, Object>> listOfCases = new ArrayList<>();
        listOfCases.add(rejectedCase);

        given(mockResponseEntity.getBody()).willReturn(listOfCases);
        given(underTest
            .getNonRejectedCases(USER_ID, JWT))
            .willReturn(listOfCases);

        //when
        List<Map<String, Object>> casesRetrieved = underTest.getNonRejectedCases(USER_ID, JWT);

        //then
        assertEquals(0, casesRetrieved.size());
    }


    @Test
    public void getNonRejectedCases_should_return_empty_list_if_no_cases_found() {
        //given
        listOfCases = Collections.emptyList();
        given(mockResponseEntity.getBody()).willReturn(listOfCases);
        given(underTest
            .getNonRejectedCases(USER_ID, JWT))
            .willReturn(listOfCases);

        //when
        List<Map<String, Object>> casesRetrieved = underTest.getCases(USER_ID, JWT);

        //then
        assertEquals(0, casesRetrieved.size());
    }
}
