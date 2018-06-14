package uk.gov.hmcts.reform.divorce.support;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan(basePackages = {"uk.gov.hmcts.reform.divorce", "uk.gov.hmcts.auth.provider.service"})
@PropertySource("classpath:application.properties")
@PropertySource(value = "classpath:application-${env}.properties", ignoreResourceNotFound = true)
public class TestContextConfiguration {
}
