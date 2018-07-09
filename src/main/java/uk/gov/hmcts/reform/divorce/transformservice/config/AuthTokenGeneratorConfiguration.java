package uk.gov.hmcts.reform.divorce.transformservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import uk.gov.hmcts.reform.authorisation.ServiceAuthorisationApi;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGeneratorFactory;

@Configuration
@Lazy
@EnableFeignClients(basePackageClasses = ServiceAuthorisationApi.class)
public class AuthTokenGeneratorConfiguration {

    @Bean(name = "ccd_submission")
    public AuthTokenGenerator ccdSubmissionTokenGenerator(
        @Value("${idam.s2s-auth.totp_divorce_ccd_submission_secret}") final String secret,
        @Value("${idam.s2s-auth.microservice_divorce_ccd_submission}") final String microService,
        final ServiceAuthorisationApi serviceAuthorisationApi) {

        return AuthTokenGeneratorFactory.createDefaultGenerator(secret, microService, serviceAuthorisationApi);
    }

    @Bean(name = "divorce_frontend")
    public AuthTokenGenerator ccdDivorceFrontEndTokenGenerator(
        @Value("${idam.s2s-auth.totp_divorce_frontend_secret}") final String secret,
        @Value("${idam.s2s-auth.microservice_divorce_frontend}") final String microService,
        final ServiceAuthorisationApi serviceAuthorisationApi) {

        return AuthTokenGeneratorFactory.createDefaultGenerator(secret, microService, serviceAuthorisationApi);
    }
}
