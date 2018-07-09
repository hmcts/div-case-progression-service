package uk.gov.hmcts.reform.divorce.pay.models.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class FeesItem{

	@JsonProperty("reference")
	private String reference;

	@JsonProperty("volume")
	private int volume;

	@JsonProperty("ccd_case_number")
	private String ccdCaseNumber;

	@JsonProperty("memo_line")
	private String memoLine;

	@JsonProperty("natural_account_code")
	private String naturalAccountCode;

	@JsonProperty("code")
	private String code;

	@JsonProperty("calculated_amount")
	private int calculatedAmount;

	@JsonProperty("version")
	private String version;

}
