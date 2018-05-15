package uk.gov.hmcts.reform.divorce.orchestrations.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

@Component
public class CallbackRouter extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        restConfiguration().contextPath("/test").port("4003").enableCORS(true)
        .apiContextPath("/api-doc")
        .apiProperty("api.title", "Test REST API")
        .apiProperty("api.version", "v1")
        .apiContextRouteId("doc-api")
        .component("servlet")
        .bindingMode(RestBindingMode.json);

        rest("/say")
            .get("/hello").to("direct:hello");

        from("direct:hello")
            .transform().constant("hello world");
    }
}
