package uk.gov.hmcts.reform.divorce.fees.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.divorce.fees.api.FeesAnyPaymentApiClient;
import uk.gov.hmcts.reform.divorce.fees.models.Fee;

@Component
public class FeesAndPaymentService {

    private FeesAnyPaymentApiClient feesAnyPaymentApiClient;

    @Autowired
    public FeesAndPaymentService(FeesAnyPaymentApiClient feesAnyPaymentApiClient) {
       this.feesAnyPaymentApiClient = feesAnyPaymentApiClient;
    }

    public Fee getPetitionIssueFee() {
        Fee petitionIssueFee = feesAnyPaymentApiClient.getPetitionIssueFee().getBody();
        return petitionIssueFee;
    }

}
