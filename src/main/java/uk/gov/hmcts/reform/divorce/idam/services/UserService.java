package uk.gov.hmcts.reform.divorce.idam.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.divorce.idam.api.IdamApiClient;
import uk.gov.hmcts.reform.divorce.idam.models.UserDetails;
import uk.gov.hmcts.reform.divorce.insights.AppInsights;

@Component
public class UserService {

    private final IdamApiClient idamApiClient;
    private final AppInsights appInsights;

    @Autowired
    public UserService(IdamApiClient idamApiClient, AppInsights appInsights) {
        this.idamApiClient = idamApiClient;
        this.appInsights = appInsights;
    }

    public UserDetails getUserDetails(String authorisation) {
        long detailsStart = System.currentTimeMillis();
        UserDetails userDetails = idamApiClient.retrieveUserDetails(authorisation);
        long detailsEnd = System.currentTimeMillis();

        appInsights.trackMetric("IdAM User Details", detailsEnd - detailsStart);

        return userDetails;
    }

}
