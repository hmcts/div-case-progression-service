package uk.gov.hmcts.reform.divorce.auth;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.authorisation.ServiceAuthorisationApi;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGeneratorFactory;
import uk.gov.hmcts.reform.divorce.auth.model.ServiceAuthTokenFor;

import java.util.HashMap;
import java.util.Map;

@Service
public class ServiceAuthSupport {

    private static final Map<ServiceAuthTokenFor, String> CACHED_TOKENS = new HashMap<>();

    @Value("${document.generator.service.auth.secret}")
    private String documentGeneratorSecret;

    @Value("${document.generator.auth.microservice}")
    private String documentGeneratorMicroserviceName;

    @Value("${case.progression.service.auth.secret}")
    private String caseProgressionSecret;

    @Value("${case.progression.auth.microservice}")
    private String caseProgressionMicroserviceName;

    @Autowired
    private ServiceAuthorisationApi serviceAuthorisationApi;

    public synchronized String getServiceAuthTokenFor(ServiceAuthTokenFor serviceAuthTokenFor) {
        if (serviceAuthTokenFor == null) {
            throw new IllegalArgumentException("ServiceAuthTokenFor is null. Cannot generate service token");
        }

        String serviceToken = CACHED_TOKENS.get(serviceAuthTokenFor);

        if (StringUtils.isBlank(serviceToken)) {
            serviceToken = getCaseProgressionAuthTokenGenerator(serviceAuthTokenFor).generate();

            CACHED_TOKENS.put(serviceAuthTokenFor, serviceToken);
        }

        return serviceToken;
    }

    private AuthTokenGenerator getCaseProgressionAuthTokenGenerator(ServiceAuthTokenFor serviceAuthTokenFor) {
        switch (serviceAuthTokenFor) {
            case CASE_PROGRESSION:
                System.out.println("ATTENTION PLEASE: " + caseProgressionSecret);
                return AuthTokenGeneratorFactory.createDefaultGenerator(caseProgressionSecret, caseProgressionMicroserviceName, serviceAuthorisationApi);
            case DIV_DOCUMENT_GENERATOR:
                return AuthTokenGeneratorFactory.createDefaultGenerator(documentGeneratorSecret, documentGeneratorMicroserviceName, serviceAuthorisationApi);
        }

        return null;
    }

}
