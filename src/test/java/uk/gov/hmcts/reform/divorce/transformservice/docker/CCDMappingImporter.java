package uk.gov.hmcts.reform.divorce.transformservice.docker;

import com.fasterxml.jackson.databind.ObjectMapper;

public class CCDMappingImporter {

    public static void main(String[] args) {
        String mappingFilePath = args[0];

        HttpEntityFactory httpEntityFactory = new HttpEntityFactory();
        ObjectMapper objectMapper = new ObjectMapper();

        HttpRequestFactory httpRequestFactory = new HttpRequestFactory(httpEntityFactory, objectMapper);

        DataLoader dataLoader = new DataLoader(httpRequestFactory);

        dataLoader.createRole("citizen-loa1", "PUBLIC");

        dataLoader.importMappings(CCDMappingImporter.class.getResource(mappingFilePath).getFile());
    }
}
