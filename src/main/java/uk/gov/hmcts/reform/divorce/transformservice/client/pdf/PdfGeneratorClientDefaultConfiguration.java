package uk.gov.hmcts.reform.divorce.transformservice.client.pdf;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PdfGeneratorClientDefaultConfiguration implements PdfGeneratorClientConfiguration {

    private static final String PDF_GENERATOR_URL_FORMAT = "%s%s";

    @Value("${pdf.generator.baseUrl}")
    private String baseUrl;

    @Value("${pdf.generator.generate.path}")
    private String generatePath;

    @Override
    public String getPdfGeneratorUrl() {
        return String.format(PDF_GENERATOR_URL_FORMAT, baseUrl, generatePath);
    }
}