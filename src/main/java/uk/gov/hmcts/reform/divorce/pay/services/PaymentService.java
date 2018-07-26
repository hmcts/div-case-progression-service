package uk.gov.hmcts.reform.divorce.pay.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.divorce.pay.api.PaymentServiceClient;
import uk.gov.hmcts.reform.divorce.pay.exceptions.PaymentFailedException;
import uk.gov.hmcts.reform.divorce.pay.models.request.CreditAccountPaymentRequest;
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
    public PaymentService(PaymentServiceClient paymentServiceClient, @Qualifier("divorce_frontend") AuthTokenGenerator
        authTokenGenerator) {
        this.paymentServiceClient = paymentServiceClient;
        this.authTokenGenerator = authTokenGenerator;
    }

    public void processPBAPayments(String userJWT, CreateEvent caseEvent) {

        CreditAccountPaymentRequest request = new CreditAccountPaymentRequest();
        CaseDetails caseDetails = caseEvent.getCaseDetails();
        CoreCaseData caseData = caseDetails.getCaseData();
        OrderSummary orderSummary = caseData.getOrderSummary();

        try {
            //we always interesting in the first fee, future might require a change.
            FeesItem feesItem = orderSummary.getFees().get(0);
            final Value value = feesItem.getValue();
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
            uk.gov.hmcts.reform.divorce.pay.models.request.FeesItem feesItemRequest;
            feesItemRequest = new uk.gov.hmcts.reform.divorce.pay.models.request.FeesItem();
            addToRequest(feesItemRequest::setCcdCaseNumber, caseDetails::getCaseId);
            addToRequest(feesItemRequest::setCalculatedAmount, orderSummary::getPaymentTotal);
            addToRequest(feesItemRequest::setCode, value::getFeeCode);
            addToRequest(feesItemRequest::setReference, caseData::getD8SolicitorReference);
            addToRequest(feesItemRequest::setVersion, value::getFeeVersion);
            List<uk.gov.hmcts.reform.divorce.pay.models.request.FeesItem> feesItems = new ArrayList<>();
            feesItems.add(feesItemRequest);
            request.setFees(feesItems);
            paymentServiceClient.creditAccountPayment(userJWT, authTokenGenerator.generate(), request);
            log.info("successfully payment processed on case Id - " + caseDetails.getCaseId());

        } catch (Exception e) {

            log.error("failed payment on case Id - " + caseDetails.getCaseId() + "\n" + e.getLocalizedMessage());
            throw new PaymentFailedException(e.getMessage());
        }
    }

    private void addToRequest(Consumer<String> setter, Supplier<String> value) {
        Optional.ofNullable(value.get()).ifPresent(setter);
    }


}
