package uk.gov.hmcts.reform.divorce.errorhandler;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.web.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;

import java.util.Map;

@Component
public class GlobalErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(RequestAttributes requestAttributes, boolean includeStackTrace) {
        Map<String, Object> errorAttributes =
                super.getErrorAttributes(requestAttributes, includeStackTrace);
        String errorCode =
                (String) requestAttributes.getAttribute("javax.servlet.error.error_code", RequestAttributes.SCOPE_REQUEST);
        if (StringUtils.isNotBlank(errorCode)) {
            errorAttributes.put("errorCode", errorCode);
        }

        return errorAttributes;
    }
}
