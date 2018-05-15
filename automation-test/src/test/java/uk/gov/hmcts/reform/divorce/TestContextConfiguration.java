package uk.gov.hmcts.reform.divorce;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan(basePackages = {"uk.gov.hmcts.reform.divorce", "uk.gov.hmcts.auth.provider.service"})
@PropertySource("classpath:application.properties")
@PropertySource("classpath:application-aat.properties")
public class TestContextConfiguration {
}
