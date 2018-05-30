package uk.gov.hmcts.reform.divorce;

import io.restassured.RestAssured;
import io.restassured.specification.ProxySpecification;
import net.serenitybdd.junit.spring.integration.SpringIntegrationMethodRule;
import net.serenitybdd.rest.SerenityRest;
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
        SerenityRest.useRelaxedHTTPSValidation("SSL");
        RestAssured.useRelaxedHTTPSValidation();

        System.setProperty("http.proxyHost", "proxyout.reform.hmcts.net");
        System.setProperty("http.proxyPort", "8080");
        //System.setProperty("http.nonProxyHosts", "http://localhost");
        System.setProperty("https.proxyHost", "proxyout.reform.hmcts.net");
        System.setProperty("https.proxyPort", "8080");

        /*RestAssured.proxy = ProxySpecification.host("proxyout.reform.hmcts.net").withPort(8080);
        SerenityRest.proxy("proxyout.reform.hmcts.net", 8080);*/
    }
}
