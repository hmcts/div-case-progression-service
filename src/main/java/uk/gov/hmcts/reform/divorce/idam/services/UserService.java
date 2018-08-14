package uk.gov.hmcts.reform.divorce.idam.services;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.divorce.idam.api.IdamApiClient;
import uk.gov.hmcts.reform.divorce.idam.models.UserDetails;

@Component
public class UserService {

    private static String BEARER = "Bearer";

    private final IdamApiClient idamApiClient;

    @Autowired
    public UserService(IdamApiClient idamApiClient) {
        this.idamApiClient = idamApiClient;
    }

    public UserDetails getUserDetails(String authorisation) {
        String authToken = StringUtils.containsIgnoreCase(authorisation, BEARER)
                ? authorisation
                : String.format("%s %s", BEARER, authorisation);
        return idamApiClient.retrieveUserDetails(authToken);
    }

}
