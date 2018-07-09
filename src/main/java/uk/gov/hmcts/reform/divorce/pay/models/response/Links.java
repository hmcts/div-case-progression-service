package uk.gov.hmcts.reform.divorce.pay.models.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

import javax.annotation.Generated;

@Data
@ToString
public class Links{

	@JsonProperty("cancel")
	private Cancel cancel;

	@JsonProperty("next_url")
	private NextUrl nextUrl;

	@JsonProperty("self")
	private Self self;

}
