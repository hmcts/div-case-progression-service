package uk.gov.hmcts.reform.divorce.transformservice.mapping;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.divorceapplicationdata.Payment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class CcdToPaymentMapperTest {

    @InjectMocks
    private CcdToPaymentMapper mapper;

    @Test
    public void mapperMapFromAList() throws Exception {

        Map<String, Object> ccdPayments = getPaymentsAsCaseDataMap();

        final List<Payment> paymentList = mapper.ccdToPaymentRef(ccdPayments);

        assertThat(paymentList.size()).isEqualTo(2);
        assertThat(paymentList.get(0).getPaymentReference()).isEqualTo("RC-1536-5783-3942-9827");
        assertThat(paymentList.get(0).getPaymentStatus()).isEqualTo("success");
        assertThat(paymentList.get(1).getPaymentStatus()).isEqualTo("initiated");
    }

    private Map<String, Object> getPaymentsAsCaseDataMap() throws Exception {
        Map<String, Object> paymentMap1 = new HashMap<>();
        paymentMap1.put("PaymentStatus", "success");
        paymentMap1.put("PaymentReference", "RC-1536-5783-3942-9827");

        Map<String, Object> paymentMap2 = new HashMap<>();
        paymentMap2.put("PaymentStatus", "initiated");

        List<Map<String, Object>> payments = new ArrayList<>();
        payments.add(paymentMap1);
        payments.add(paymentMap2);

        Map<String, Object> caseDataMap = new HashMap<>();
        caseDataMap.put("Payments", payments);
        return caseDataMap;
    }
}
