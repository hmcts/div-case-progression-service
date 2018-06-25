package uk.gov.hmcts.reform.divorce.insights;

import com.microsoft.applicationinsights.TelemetryClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.logging.appinsights.AbstractAppInsights;

@Component
public class AppInsights extends AbstractAppInsights {
    @Autowired
    public AppInsights(TelemetryClient client) {
        super(client);
    }

    public void trackMetric(String name, double value) {
        super.telemetry.trackMetric(name, value);
    }
}
