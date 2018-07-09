package uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Value{

	@JsonProperty("FeeDescription")
	private String feeDescription;

	@JsonProperty("FeeVersion")
	private String feeVersion;

	@JsonProperty("FeeCode")
	private String feeCode;

	@JsonProperty("FeeAmount")
	private String feeAmount;

	public void setFeeDescription(String feeDescription){
		this.feeDescription = feeDescription;
	}

	public String getFeeDescription() {
		return feeDescription;
	}

	public void setFeeVersion(String feeVersion){
		this.feeVersion = feeVersion;
	}

	public String getFeeVersion(){
		return feeVersion;
	}

	public void setFeeCode(String feeCode){
		this.feeCode = feeCode;
	}

	public String getFeeCode(){
		return feeCode;
	}

	public void setFeeAmount(String feeAmount){
		this.feeAmount = feeAmount;
	}

	public String getFeeAmount(){
		return feeAmount;
	}

	@Override
 	public String toString(){
		return
			"Value{" +
			"feeDescription = '" + feeDescription + '\'' +
			",feeVersion = '" + feeVersion + '\'' +
			",feeCode = '" + feeCode + '\'' +
			",feeAmount = '" + feeAmount + '\'' +
			"}";
		}
}
