package uk.gov.hmcts.reform.divorce.transformservice;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import uk.gov.hmcts.reform.divorce.CaseProgressionApplication;

@Configuration
@EnableSwagger2
public class SwaggerConfiguration {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
            .select()
            .apis(RequestHandlerSelectors.basePackage(CaseProgressionApplication.class.getPackage().getName()))
            .build()
            .useDefaultResponseMessages(false)
            .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
            .title("Transformation Service API")
            .description("Persist data from the frontend divorce session into Core Case data")
            .license("")
            .licenseUrl("")
            .termsOfServiceUrl("")
            .build();
    }

}
