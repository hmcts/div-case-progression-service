package uk.gov.hmcts.reform.divorce.transformservice.mapping;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(MockitoJUnitRunner.class)
public class CcdToPaymentMapperTest {

    @InjectMocks
    private CcdToPaymentMapper mapper;


    @Test
    public void mapperMapFromAList() throws Exception {

        //List of payment from a file as a string
        String paymentsAsString = ResourceLoader.loadAsText("divorce-payload-json/3PaymentsCcd.json");

        ObjectMapper objectMapper = new ObjectMapper();

        TypeReference<HashMap<String,JsonNode >> typeRef
            = new TypeReference<HashMap<String, JsonNode>>() {};

        Map<String, Object> caseData = objectMapper.readValue(paymentsAsString, typeRef);

        //map it
        final List<Payment> paymentList = mapper.ccdTpPaymentRef(caseData);
        //check list size
        assertThat(paymentList.size()).isEqualTo(3);
        //check first payment is success
        assertThat(paymentList.get(0).getPaymentReference()).isEqualTo("RC-1536-5783-3942-9827");
        assertThat(paymentList.get(0).getPaymentStatus()).isEqualTo("Success");
        assertThat(paymentList.get(1).getPaymentStatus()).isEqualTo("Initiated");
    }
}
