package uk.gov.hmcts.reform.divorce.fees.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.divorce.fees.api.FeesAnyPaymentApiClient;
import uk.gov.hmcts.reform.divorce.fees.models.Fee;
import uk.gov.hmcts.reform.divorce.pay.exceptions.FeesNotFoundException;

@Component
public class FeesAndPaymentService {

    private final FeesAnyPaymentApiClient feesAnyPaymentApiClient;

    @Autowired
    public FeesAndPaymentService(FeesAnyPaymentApiClient feesAnyPaymentApiClient) {
        this.feesAnyPaymentApiClient = feesAnyPaymentApiClient;
    }

    public Fee getPetitionIssueFee() {
        Fee fee = feesAnyPaymentApiClient.getPetitionIssueFee().getBody();
        if (fee.getAmount() < 1) {
            throw new FeesNotFoundException("Fees not found !");
        }
        return fee;
    }

}
