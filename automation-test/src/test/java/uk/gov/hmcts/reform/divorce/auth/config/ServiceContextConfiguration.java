package uk.gov.hmcts.reform.divorce.auth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.web.HttpMessageConvertersAutoConfiguration;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.feign.FeignAutoConfiguration;
import org.springframework.cloud.netflix.feign.ribbon.FeignRibbonClientAutoConfiguration;
import org.springframework.cloud.netflix.ribbon.RibbonAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import uk.gov.hmcts.reform.authorisation.ServiceAuthorisationApi;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGeneratorFactory;

@Configuration
@Lazy
@ImportAutoConfiguration({RibbonAutoConfiguration.class, HttpMessageConvertersAutoConfiguration.class, FeignRibbonClientAutoConfiguration.class, FeignAutoConfiguration.class})
@EnableFeignClients(basePackageClasses = ServiceAuthorisationApi.class)
public class ServiceContextConfiguration {

    @Bean("caseProgressionAuthTokenGenerator")
    public AuthTokenGenerator serviceAuthTokenGenerator(
            @Value("${case.progression.service.auth.secret}") final String secret,
            @Value("${case.progression.auth.microservice}") final String microService,
            final ServiceAuthorisationApi serviceAuthorisationApi
    ) {

        System.out.println("Microsoft key : " + secret);
        return AuthTokenGeneratorFactory.createDefaultGenerator(secret, microService, serviceAuthorisationApi);
    }
}
