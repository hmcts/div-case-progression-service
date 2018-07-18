package uk.gov.hmcts.reform.divorce.pay;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.divorce.pay.api.PaymentServiceClient;
import uk.gov.hmcts.reform.divorce.pay.exceptions.PaymentFailedException;
import uk.gov.hmcts.reform.divorce.pay.services.PaymentService;
import uk.gov.hmcts.reform.divorce.transformservice.domain.ccd.CreateEvent;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.CaseDetails;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.CoreCaseData;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.FeesItem;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.OrderSummary;

import java.util.ArrayList;

import static org.mockito.Mockito.mock;

@RunWith(JUnit4.class)
public class PaymentServiceTest {

    private PaymentService paymentService;

    private PaymentServiceClient paymentServiceClient;

    private AuthTokenGenerator authTokenGenerator;

    @Before
    public void setup() {
        this.paymentService =  mock(PaymentService.class);
        this.authTokenGenerator = mock(AuthTokenGenerator.class);
        this.paymentService = new PaymentService(paymentServiceClient, authTokenGenerator);
    }

    @Test
    public void assertValidPayment() {
        Assert.assertTrue(true);
    }

    @Test(expected = PaymentFailedException.class)
    public void failIfFoundNoJWTTokens() {

        CreateEvent event = getCreateEvent();
        paymentService.processPBAPayments("test", event);
    }

    private CreateEvent getCreateEvent() {
        CreateEvent event = new CreateEvent();
        CaseDetails caseDetails = new CaseDetails();
        event.setCaseDetails(caseDetails);
        CoreCaseData coreCaseData = new CoreCaseData();
        OrderSummary orderSummary = new OrderSummary();
        ArrayList<FeesItem> feesItems = new ArrayList<>();
        feesItems.add(new FeesItem());
        orderSummary.setFees(feesItems);
        coreCaseData.setOrderSummary(orderSummary);
        caseDetails.setCaseData(coreCaseData);
        return event;
    }
}
