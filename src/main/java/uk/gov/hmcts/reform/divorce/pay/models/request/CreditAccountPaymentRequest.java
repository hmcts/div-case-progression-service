package uk.gov.hmcts.reform.divorce.pay.models.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

import javax.annotation.Generated;

@Data
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreditAccountPaymentRequest{

	@JsonProperty("ccd_case_number")
	private String ccdCaseNumber;

	@JsonProperty("account_number")
	private String accountNumber;

	@JsonProperty("amount")
	private String amount;

	@JsonProperty("case_reference")
	private String caseReference;

	@JsonProperty("fees")
	private List<FeesItem> fees;

	@JsonProperty("service")
	private String service;

	@JsonProperty("customer_reference")
	private String customerReference;

	@JsonProperty("site_id")
	private String siteId;

	@JsonProperty("description")
	private String description;

	@JsonProperty("currency")
	private String currency;

	@JsonProperty("organisation_name")
	private String organisationName;

}
