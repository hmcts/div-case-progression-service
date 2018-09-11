package uk.gov.hmcts.reform.divorce.support.auth.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserCode {
    private String code;
}
