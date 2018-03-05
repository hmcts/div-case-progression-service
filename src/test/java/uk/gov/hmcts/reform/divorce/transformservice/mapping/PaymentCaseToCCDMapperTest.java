package uk.gov.hmcts.reform.divorce.transformservice.mapping;

import static java.time.format.DateTimeFormatter.ofPattern;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;

import org.junit.Test;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.reform.divorce.CaseProgressionApplication;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.CoreCaseData;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.divorceapplicationdata.DivorceSession;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CaseProgressionApplication.class)
public class PaymentCaseToCCDMapperTest {

    @Autowired
    private DivorceCaseToCCDMapper mapper;

    @Test
    public void shouldMapAllAndTransformAllFieldsForPaymentsMappingScenario() throws URISyntaxException, IOException {

        CoreCaseData expectedCoreCaseData = (CoreCaseData)DivorceCaseToCCDMapperTestUtil.jsonToPOJO("fixtures/ccdmapping/paymentcase.json", PaymentCaseToCCDMapperTest.class, CoreCaseData.class);
        expectedCoreCaseData.setCreatedDate(LocalDate.now().format(ofPattern("yyyy-MM-dd")));
        
        DivorceSession divorceSession = (DivorceSession)DivorceCaseToCCDMapperTestUtil
                .jsonToPOJO("divorce-payload-json/payment.json", PaymentCaseToCCDMapperTest.class, DivorceSession.class);

        CoreCaseData actualCoreCaseData = mapper.divorceCaseDataToCourtCaseData(divorceSession);

        assertThat(actualCoreCaseData, samePropertyValuesAs(expectedCoreCaseData));
    }

    @Test
    public void shouldAddANewPaymentToExistingPaymentsMappingScenario() throws URISyntaxException, IOException {

        CoreCaseData expectedCoreCaseData = (CoreCaseData) DivorceCaseToCCDMapperTestUtil.jsonToPOJO("fixtures/ccdmapping/additionalpayment.json", PaymentCaseToCCDMapperTest.class, CoreCaseData.class);
        expectedCoreCaseData.setCreatedDate(LocalDate.now().format(ofPattern("yyyy-MM-dd")));

        DivorceSession divorceSession = (DivorceSession) DivorceCaseToCCDMapperTestUtil
                .jsonToPOJO("divorce-payload-json/additional-payment.json", PaymentCaseToCCDMapperTest.class, DivorceSession.class);

        CoreCaseData actualCoreCaseData = mapper.divorceCaseDataToCourtCaseData(divorceSession);

        assertThat(actualCoreCaseData, samePropertyValuesAs(expectedCoreCaseData));
    }

    @Test
    public void shouldReplaceExistingPaymentWithNewPaymentWhenTransactionIdIsSameMappingScenario() throws URISyntaxException, IOException {

        CoreCaseData expectedCoreCaseData = (CoreCaseData) DivorceCaseToCCDMapperTestUtil.jsonToPOJO("fixtures/ccdmapping/overwritepayment.json", PaymentCaseToCCDMapperTest.class, CoreCaseData.class);
        expectedCoreCaseData.setCreatedDate(LocalDate.now().format(ofPattern("yyyy-MM-dd")));

        DivorceSession divorceSession = (DivorceSession) DivorceCaseToCCDMapperTestUtil
                .jsonToPOJO("divorce-payload-json/overwrite-payment.json", PaymentCaseToCCDMapperTest.class, DivorceSession.class);

        CoreCaseData actualCoreCaseData = mapper.divorceCaseDataToCourtCaseData(divorceSession);

        assertThat(actualCoreCaseData, samePropertyValuesAs(expectedCoreCaseData));
    }

    @Test
    public void shouldAddPaymentWhenOnlyTransactionIdIsPopulated() throws URISyntaxException, IOException {
        
        CoreCaseData expectedCoreCaseData = (CoreCaseData) DivorceCaseToCCDMapperTestUtil.jsonToPOJO("fixtures/ccdmapping/paymentidonly.json", PaymentCaseToCCDMapperTest.class, CoreCaseData.class);
        expectedCoreCaseData.setCreatedDate(LocalDate.now().format(ofPattern("yyyy-MM-dd")));

        DivorceSession divorceSession = (DivorceSession) DivorceCaseToCCDMapperTestUtil
                .jsonToPOJO("divorce-payload-json/payment-id-only.json", PaymentCaseToCCDMapperTest.class, DivorceSession.class);

        CoreCaseData actualCoreCaseData = mapper.divorceCaseDataToCourtCaseData(divorceSession);

        assertThat(actualCoreCaseData, samePropertyValuesAs(expectedCoreCaseData));
    }

}
