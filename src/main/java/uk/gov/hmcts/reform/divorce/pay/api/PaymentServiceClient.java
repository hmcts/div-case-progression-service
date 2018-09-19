package uk.gov.hmcts.reform.divorce.pay.api;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.gov.hmcts.reform.divorce.pay.models.request.CreditAccountPaymentRequest;
import uk.gov.hmcts.reform.divorce.pay.models.response.CreditAccountPaymentResponse;

@FeignClient(name = "payment-api", url = "${payment.api.url}")
public interface PaymentServiceClient {

    String SERVICE_AUTHORISATION_HEADER = "ServiceAuthorization";

    @RequestMapping(method = RequestMethod.POST, value = "/credit-account-payments")
    ResponseEntity<CreditAccountPaymentResponse> creditAccountPayment(
        @RequestHeader(HttpHeaders.AUTHORIZATION) String authorisation,
        @RequestHeader(SERVICE_AUTHORISATION_HEADER) String serviceAuthorisation,
        CreditAccountPaymentRequest creditAccountPaymentRequest);

}
