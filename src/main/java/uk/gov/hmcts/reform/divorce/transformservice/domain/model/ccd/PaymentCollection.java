package uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd;

import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.divorceapplicationdata.Payment;

@Data
@Builder
public class PaymentCollection {

    private String id;

    private Payment value;
}