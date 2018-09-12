package uk.gov.hmcts.reform.divorce.transformservice.mapping;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.reform.divorce.CaseProgressionApplication;
import uk.gov.hmcts.reform.divorce.support.util.ResourceLoader;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.CoreCaseData;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.Document;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.divorceapplicationdata.Payment;
import uk.gov.hmcts.reform.divorce.transformservice.domain.pdf.PdfFile;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(MockitoJUnitRunner.class)
public class CcdToPaymentMapperTest {

    @InjectMocks
    private CcdToPaymentMapper mapper;

    @Ignore
    @Test
    public void mapperMapFromAList() throws Exception {

        //List of payment from a file as a string
        String paymentsAsString = ResourceLoader.loadAsText("divorce-payload-json/3PaymentsCcd.json");

        //map it
      /*  = mapper.ccdToPaymentsMap(paymentsAsString);

        //check list size
        assertThat(payments.size()).isEqualTo(3);
        //check first payment is success
        assertThat(payments.get(0).getPaymentReference()).isEqualTo("RC-1536-5783-3942-9827");
        assertThat(payments.get(0).getPaymentStatus()).isEqualTo("Success");
        assertThat(payments.get(1).getPaymentStatus()).isEqualTo("Initiated");*/
    }
}
