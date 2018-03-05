package uk.gov.hmcts.reform.divorce.transformservice.functional;

import com.jayway.jsonpath.JsonPath;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.reform.divorce.CaseProgressionApplication;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = CaseProgressionApplication.class)
public class HealthCheckServerNotFoundFunctionalTest {
    /*
        No Wiremock here to test the health page is still available when there is no response from an upstream service
     */

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void shouldReturnStatusDownWhenDependenciesAreUnavailable() throws Exception {
        String body = this.restTemplate.getForObject("/status/health", String.class);

        assertThat(JsonPath.read(body,"$.status").toString()).isEqualTo("DOWN");
        assertThat(JsonPath.read(body,"$.caseDataStoreApi.status").toString()).isEqualTo("DOWN");
        assertThat(JsonPath.read(body,"$.serviceAuthProviderApi.status").toString()).isEqualTo("DOWN");
        assertThat(JsonPath.read(body,"$.draftStoreApi.status").toString()).isEqualTo("DOWN");
        assertThat(JsonPath.read(body,"$.diskSpace.status").toString()).isEqualTo("UP");
    }
}
