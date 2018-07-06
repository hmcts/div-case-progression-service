package uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import uk.gov.hmcts.reform.divorce.fees.models.Fee;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderSummary {

    @JsonProperty("PaymentReference")
    private String paymentReference;

    @JsonProperty("PaymentTotal")
    private String paymentTotal;

    @JsonProperty("Fees")
    private List<FeesItem> fees;

    public void add(Fee... fees) {
        NumberFormat formatter = new DecimalFormat("#0");
        List<FeesItem> feesItems = new ArrayList<>();
        for (Fee fee : fees) {
            if (fee != null) {
                Value value = new Value();
                value.setFeeAmount(String.valueOf(formatter.format(fee.getAmount() * 100)));
                value.setFeeCode(fee.getFeeCode());
                value.setFeeDescription(fee.getDescription());
                value.setFeeVersion(String.valueOf(fee.getVersion()));
                FeesItem feesItem = new FeesItem();
                feesItem.setValue(value);
                feesItems.add(feesItem);
            }
        }
        this.setFees(feesItems);
        double sum = Arrays.asList(fees).stream().mapToDouble(Fee::getAmount).sum() * 100;
        this.setPaymentTotal(String.valueOf(formatter.format(sum)));
    }

}
