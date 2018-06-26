package uk.gov.hmcts.reform.divorce.draftservice.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.hmcts.reform.divorce.transformservice.client.RetrieveCcdClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyZeroInteractions;

@RunWith(MockitoJUnitRunner.class)
public class CaseRetrieverTest {

    private static final String USER_ID = "userId";
    private static final String JWT = "bearer jwt";
    private CaseRetriever underTest;

    @Mock
    private RetrieveCcdClient mockRetrieveCcdClient;

    @Before
    public void setUp() {
        String checkCcdEnabled = "true";
        underTest = new CaseRetriever(mockRetrieveCcdClient, checkCcdEnabled);
    }

    @Test
    public void getCases_should_not_call_ccd_if_feature_flag_is_disabled() {

        String checkCcdEnabled = "false";
        underTest = new CaseRetriever(mockRetrieveCcdClient, checkCcdEnabled);

        List<Map<String, Object>> casesRetrieved = underTest.getCases(USER_ID, JWT);

        assertEquals(0, casesRetrieved.size());
        verifyZeroInteractions(mockRetrieveCcdClient);
    }

    @Test
    public void getCases_should_return_empty_list_if_no_cases_found() {

        List<Map<String, Object>> cases = Collections.emptyList();
        given(mockRetrieveCcdClient
                .getCases(USER_ID, JWT))
                .willReturn(cases);

        List<Map<String, Object>> casesRetrieved = underTest.getCases(USER_ID, JWT);

        assertEquals(0, casesRetrieved.size());
    }

    @Test
    public void getCases_should_return_cases_found() {

        Map<String, Object> caseData = new HashMap();
        Map<String, Object> caseData2 = new HashMap();

        List<Map<String, Object>> cases = new ArrayList<>();
        cases.add(caseData);
        cases.add(caseData2);

        given(mockRetrieveCcdClient
                .getCases(USER_ID, JWT))
                .willReturn(cases);

        List<Map<String, Object>> casesRetrieved = underTest.getCases(USER_ID, JWT);

        assertEquals(2, casesRetrieved.size());
    }
}
