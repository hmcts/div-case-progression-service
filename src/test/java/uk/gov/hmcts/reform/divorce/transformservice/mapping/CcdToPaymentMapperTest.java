package uk.gov.hmcts.reform.divorce.transformservice.mapping;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.hmcts.reform.divorce.support.util.ResourceLoader;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.divorceapplicationdata.Payment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class CcdToPaymentMapperTest {

    @InjectMocks
    private CcdToPaymentMapper mapper;

    @Ignore
    @Test
    public void mapperMapFromAList() throws Exception {

        Map<String, Object> ccdPayments = getPaymentsAsCaseDataMap();

        final List<Payment> paymentList = mapper.ccdToPaymentRef(ccdPayments);

        assertThat(paymentList.size()).isEqualTo(3);
        assertThat(paymentList.get(0).getPaymentReference()).isEqualTo("RC-1536-5783-3942-9827");
        assertThat(paymentList.get(0).getPaymentStatus()).isEqualTo("Success");
        assertThat(paymentList.get(1).getPaymentStatus()).isEqualTo("Initiated");
    }

    private Map<String, Object> getPaymentsAsCaseDataMap() throws Exception {
        String paymentsAsString = ResourceLoader.loadAsText("divorce-payload-json/3PaymentsCcd.json");
        ObjectMapper objectMapper = new ObjectMapper();
        TypeReference<HashMap<String, JsonNode>> typeRef = new TypeReference<HashMap<String, JsonNode>>() {
        };
        return objectMapper.readValue(paymentsAsString, typeRef);
    }
}
