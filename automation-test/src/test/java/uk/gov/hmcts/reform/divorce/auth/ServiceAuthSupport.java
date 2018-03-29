package uk.gov.hmcts.reform.divorce.auth;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.divorce.auth.model.ServiceAuthTokenFor;

import java.util.HashMap;
import java.util.Map;

@Service
public class ServiceAuthSupport {

    private static final Map<ServiceAuthTokenFor, String> CACHED_TOKENS = new HashMap<>();

    @Autowired
    @Qualifier("caseProgressionAuthTokenGenerator")
    private AuthTokenGenerator caseProgressionAuthTokenGenerator;

    @Autowired
    @Qualifier("documentGeneratorAuthTokenGenerator")
    private AuthTokenGenerator documentGeneratorAuthTokenGenerator;

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
                return caseProgressionAuthTokenGenerator;
            case DOCUMENT_GENERATOR:
                return documentGeneratorAuthTokenGenerator;
        }

        return null;
    }

}
