package uk.gov.hmcts.reform.divorce;

import net.serenitybdd.junit.spring.integration.SpringIntegrationMethodRule;
import net.thucydides.core.annotations.WithTag;
import net.thucydides.core.annotations.WithTags;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.reform.divorce.auth.config.ServiceContextConfiguration;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {TestContextConfiguration.class, ServiceContextConfiguration.class})
@WithTags({
        @WithTag("test-type:all")
})
public abstract class IntegrationTest {

    @Rule
    public SpringIntegrationMethodRule springMethodIntegration;

    public IntegrationTest() {
        this.springMethodIntegration = new SpringIntegrationMethodRule();
    }
}
