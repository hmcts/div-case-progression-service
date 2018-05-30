package uk.gov.hmcts.reform.divorce;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.health.HealthAggregator;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.reform.authorisation.healthcheck.ServiceAuthHealthIndicator;
import uk.gov.hmcts.reform.divorce.health.CustomHealthAggregator;

import java.util.ArrayList;
import java.util.List;

@EnableFeignClients(basePackages = {"uk.gov.hmcts.reform.divorce.idam.api"})
@SpringBootApplication(exclude = {ServiceAuthHealthIndicator.class})
public class CaseProgressionApplication {

    @Value("${http.request.read.timeout}")
    private int httpRequestReadTimeout;

    @Value("${http.request.connect.timeout}")
    private int httpRequestConnectTimeout;

    public static void main(String[] args) {
        SpringApplication.run(CaseProgressionApplication.class, args);
    }

    private List<HttpMessageConverter<?>> getMessageConverters() {
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        messageConverters.add(new MappingJackson2HttpMessageConverter());
        return messageConverters;
    }

    private ClientHttpRequestFactory requestFactory() {
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setConnectTimeout(httpRequestConnectTimeout);
        requestFactory.setReadTimeout(httpRequestReadTimeout);

        return requestFactory;
    }

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setMessageConverters(getMessageConverters());
        restTemplate.setRequestFactory(requestFactory());

        return restTemplate;
    }

    @Bean
    public HealthAggregator healthAggregator() {
        return new CustomHealthAggregator();
    }

}
