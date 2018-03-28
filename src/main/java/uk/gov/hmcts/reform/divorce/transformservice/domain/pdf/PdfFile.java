package uk.gov.hmcts.reform.divorce.transformservice.domain.pdf;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
@Data
public class PdfFile {
    private String url;
    private String fileName;
}
