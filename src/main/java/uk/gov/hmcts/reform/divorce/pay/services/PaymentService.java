package uk.gov.hmcts.reform.divorce.pay.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.divorce.pay.api.PaymentServiceClient;
import uk.gov.hmcts.reform.divorce.pay.exceptions.PaymentFailedException;
import uk.gov.hmcts.reform.divorce.pay.models.request.CreditAccountPaymentRequest;
import uk.gov.hmcts.reform.divorce.pay.models.response.CreditAccountPaymentResponse;
import uk.gov.hmcts.reform.divorce.transformservice.domain.ccd.CreateEvent;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.CaseDetails;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.CoreCaseData;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.FeesItem;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.OrderSummary;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Component
@Slf4j
public class PaymentService {

    private static final String SERVICE = "DIVORCE";

    private static final String CURRENCY = "GBP";

    private final PaymentServiceClient paymentServiceClient;

    private final AuthTokenGenerator authTokenGenerator;


    @Autowired
    public PaymentService(PaymentServiceClient paymentServiceClient, AuthTokenGenerator authTokenGenerator) {
        this.paymentServiceClient = paymentServiceClient;
        this.authTokenGenerator = authTokenGenerator;
    }

    public void processPBAPayments(String userJWT, CreateEvent caseEvent) {

        //TODO: remove me
//        userJWT = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJ0ZmVka3JsaDRldWVldjgxdWxqOG1lM2xpbiIsInN1YiI6IjExOSIsImlhdCI6MTUzMDgyODQyMCwiZXhwIjoxNTMwODU3MjIwLCJkYXRhIjoiY2FzZXdvcmtlci1kaXZvcmNlLGNhc2V3b3JrZXIsY2FzZXdvcmtlci1kaXZvcmNlLWxvYTEsY2FzZXdvcmtlci1sb2ExIiwidHlwZSI6IkFDQ0VTUyIsImlkIjoiMTE5IiwiZm9yZW5hbWUiOiJSaHlzIiwic3VybmFtZSI6IldpbGxpYW1zIiwiZGVmYXVsdC1zZXJ2aWNlIjoiQ0NEIiwibG9hIjoxLCJkZWZhdWx0LXVybCI6Imh0dHBzOi8vd3d3LmNjZC5kZW1vLnBsYXRmb3JtLmhtY3RzLm5ldCIsImdyb3VwIjoiY2FzZXdvcmtlciJ9.Uz-vI2oZ5oxvB3OxGzavmxa5a3C-dCjPImnz-FFyUgA";
        /**
         *
         *{
         *  “amount”: 0,  )
         *  “description”: “string”, )
         *  “ccd_case_number”: “string”, )
         *  “case_reference”: “string”, ???
         *  “service”: “CMC”, )
         *  “currency”: “GBP”, )
         *  “customer_reference”: “string”, )
         *  “organisation_name”: “string”, )
         *  “account_number”: “stri/ng”, )
         *  “site_id”: “string”, _
         *  “fees”: [
         *    {
         *      “calculated_amount”: 0,
         *      “ccd_case_number”: “string”,
         *      “code”: “string”,
         *      “memo_line”: “string”,
         *      “natural_account_code”: “string”,
         *      “reference”: “string”,
         *      “version”: “string”,
         *      “volume”: 0
         *    }
         *  ]
         * }
         *
         */
        CreditAccountPaymentRequest request = new CreditAccountPaymentRequest();
        CaseDetails caseDetails = caseEvent.getCaseDetails();
        CoreCaseData caseData = caseDetails.getCaseData();
        OrderSummary orderSummary = caseData.getOrderSummary();

        try {
            //we always interesting in the first fee, future might require a change.
            FeesItem feesItem = orderSummary.getFees().get(0);
            Value value = feesItem.getValue();
            request.setService(SERVICE);
            request.setCurrency(CURRENCY);
            addToRequest(request::setAmount, orderSummary::getPaymentTotal);
            addToRequest(request::setCustomerReference, caseData::getD8caseReference);
            addToRequest(request::setCcdCaseNumber, caseDetails::getCaseId);
            addToRequest(request::setAccountNumber, caseData::getSolicitorFeeAccountNumber);
            addToRequest(request::setOrganisationName, caseData::getPetitionerSolicitorFirm);
            addToRequest(request::setSiteId, caseData::getD8SelectedDivorceCentreSiteId);
            addToRequest(request::setCustomerReference, caseData::getD8SolicitorReference);
            addToRequest(request::setCustomerReference, caseData::getD8SolicitorReference);
            addToRequest(request::setDescription, value::getFeeDescription);
            //populate feesItem
            List<uk.gov.hmcts.reform.divorce.pay.models.request.FeesItem> feesItems = new ArrayList<>();
            uk.gov.hmcts.reform.divorce.pay.models.request.FeesItem feesItemRequest;
            feesItemRequest = new uk.gov.hmcts.reform.divorce.pay.models.request.FeesItem();
            addToRequest(feesItemRequest::setCcdCaseNumber, caseDetails::getCaseId);
            addToRequest(feesItemRequest::setCalculatedAmount, orderSummary::getPaymentTotal);
            addToRequest(feesItemRequest::setCode, value::getFeeCode);
            addToRequest(feesItemRequest::setReference, caseData::getD8SolicitorReference);
            addToRequest(feesItemRequest::setVersion, value::getFeeVersion);
            feesItems.add(feesItemRequest);
            request.setFees(feesItems);
            paymentServiceClient.creditAccountPayment(userJWT, authTokenGenerator.generate(), request);
            log.info("successfully payment processed on case Id - " + caseDetails.getCaseId());

        } catch (Exception e) {

            log.error("failed payment on case Id - " + caseDetails.getCaseId() + "\n" + e.getLocalizedMessage());
            throw new PaymentFailedException(e.getMessage());
        }
    }

    private void addToRequest(Consumer<String> setter, Supplier<String> value)  {
        Optional.ofNullable(value.get()).ifPresent(setter);
    }


}
