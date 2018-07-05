package uk.gov.hmcts.reform.divorce.fees.api;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.gov.hmcts.reform.divorce.fees.models.Fee;

@FeignClient(name = "fees-and-payment-api", url = "${fees.and.payments.baseUrl}")
public interface FeesAnyPaymentApiClient {

    @RequestMapping(method = RequestMethod.GET, consumes = "application/json", value = "/fees-and-payments/version/1/petition-issue-fee")
    ResponseEntity<Fee> getPetitionIssueFee();
}
