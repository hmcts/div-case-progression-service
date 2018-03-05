package uk.gov.hmcts.reform.divorce.testutils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectMapperTestUtil {

    public static <T> T convertJsonToObject(final byte[] data, Class<T> type) {
        try {
            return new ObjectMapper().readValue(data, type);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String convertObjectToJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}