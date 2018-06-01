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

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertSame;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class RetrieveCcdClientTest {

    private static final String USER_ID = "userId";
    private static final String JWT = "jwt";
    @Mock
    private CcdClientConfiguration mockClientConfiguration;
    @Mock
    private RestTemplate mockRestTemplate;
    @Mock
    private TransformationHttpEntityFactory mockHttpEntityFactory;

    private RetrieveCcdClient underTest;

    @Before
    public void setUp() throws Exception {
        underTest = new RetrieveCcdClient(mockClientConfiguration, mockRestTemplate, mockHttpEntityFactory);
    }

    @Test
    public void getCases_should_return_list_of_cases() {

        // given
        String expectedUrl = "http://expectedUrl";
        given(mockClientConfiguration.getRetrieveCaseUrl(USER_ID))
                .willReturn(expectedUrl);

        HttpEntity<String> mockHttpEntity = mock(HttpEntity.class);
        given(mockHttpEntityFactory.createRequestEntityForCcdGet(JWT))
                .willReturn(mockHttpEntity);

        ResponseEntity<List> mockResponseEntity = mock(ResponseEntity.class);

        List expectedListOfCases = mock(List.class);
        given(mockResponseEntity.getBody()).willReturn(expectedListOfCases);
        given(mockRestTemplate.exchange(expectedUrl, HttpMethod.GET, mockHttpEntity, List.class))
                .willReturn(mockResponseEntity);
        // when
        List<Map> listOfCases = underTest.getCases(USER_ID, JWT);

        // then
        assertSame(listOfCases, expectedListOfCases);
    }
}
