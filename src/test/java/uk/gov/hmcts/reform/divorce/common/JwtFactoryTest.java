package uk.gov.hmcts.reform.divorce.common;

import org.junit.Test;
import uk.gov.hmcts.reform.divorce.transformservice.domain.Jwt;
import uk.gov.hmcts.reform.divorce.errorhandler.JwtParsingException;

import java.text.ParseException;
import java.time.LocalDate;

import static org.junit.Assert.*;

public class JwtFactoryTest {

    private JwtFactory jwtFactory = new JwtFactory();

    @Test
    public void shouldReturnJwtDomainObjectFromEncodedJwt() throws Exception {
        String encodedJwt = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI4cjF0M3Z1M2c5ZGR0OG9saGpyMmoyc2o3ZyIsInN1YiI6IjYwIiwiaWF0Ijo" +
                            "xNTA1ODk3Mjk3LCJleHAiOjE1MDU5MjYwOTcsImRhdGEiOiJjaXRpemVuLGRpdm9yY2UtcHJpdmF0ZS1iZXRhLGNpdGl" +
                            "6ZW4tbG9hMSxkaXZvcmNlLXByaXZhdGUtYmV0YS1sb2ExIiwidHlwZSI6IkFDQ0VTUyIsImlkIjoiNjAiLCJmb3JlbmF" +
                            "tZSI6ImpvaG4iLCJzdXJuYW1lIjoic21pdGgiLCJkZWZhdWx0LXNlcnZpY2UiOiJEaXZvcmNlIiwibG9hIjoxLCJkZWZ" +
                            "hdWx0LXVybCI6Imh0dHBzOi8vd3d3LWxvY2FsLnJlZ2lzdHJhdGlvbi50ZXN0VXJsLmNvbTo5MDAwL3BvYy9kaXZvcmN" +
                            "lIiwiZ3JvdXAiOiJkaXZvcmNlLXByaXZhdGUtYmV0YSJ9.E4TxUfBKZg6bUvlsDUonWyIMEEoRuzyGUneFiW8iEo0";

        Jwt jwt = jwtFactory.create(encodedJwt);

    assertEquals("https://www-local.registration.testUrl.com:9000/poc/divorce", jwt.getDefaultUrl());
    assertEquals("60", jwt.getSubject());
    assertEquals("citizen,divorce-private-beta,citizen-loa1,divorce-private-beta-loa1", jwt.getData());
    assertEquals("ACCESS", jwt.getType());
    assertEquals("john", jwt.getForename());
    assertEquals("smith", jwt.getSurname());
    assertEquals(60, jwt.getId());
    assertEquals(LocalDate.of(2017, 9, 20), jwt.getExpiration());
    assertEquals(LocalDate.of(2017, 9, 20), jwt.getIssuedAt());
    assertEquals("8r1t3vu3g9ddt8olhjr2j2sj7g", jwt.getJwtId());
    assertEquals("Divorce", jwt.getDefaultService());
    assertEquals(1, jwt.getLevelOfAssurance());
    assertEquals("divorce-private-beta", jwt.getGroup());
}

    @Test
    public void shouldReturnJwtFromAuthorizationHeader() throws Exception {
        String encodedJwt = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI4cjF0M3Z1M2c5ZGR0OG9saGpyMmoyc2o3ZyIsInN1YiI6IjYwIiwia" +
                            "WF0IjoxNTA1ODk3Mjk3LCJleHAiOjE1MDU5MjYwOTcsImRhdGEiOiJjaXRpemVuLGRpdm9yY2UtcHJpdmF0ZS1iZXRhLG" +
                            "NpdGl6ZW4tbG9hMSxkaXZvcmNlLXByaXZhdGUtYmV0YS1sb2ExIiwidHlwZSI6IkFDQ0VTUyIsImlkIjoiNjAiLCJmb3J" +
                            "lbmFtZSI6ImpvaG4iLCJzdXJuYW1lIjoic21pdGgiLCJkZWZhdWx0LXNlcnZpY2UiOiJEaXZvcmNlIiwibG9hIjoxLCJk" +
                            "ZWZhdWx0LXVybCI6Imh0dHBzOi8vd3d3LWxvY2FsLnJlZ2lzdHJhdGlvbi50ZXN0VXJsLmNvbTo5MDAwL3BvYy9kaXZvc" +
                            "mNlIiwiZ3JvdXAiOiJkaXZvcmNlLXByaXZhdGUtYmV0YSJ9.E4TxUfBKZg6bUvlsDUonWyIMEEoRuzyGUneFiW8iEo0";

        Jwt jwt = jwtFactory.create(encodedJwt);

        assertEquals("https://www-local.registration.testUrl.com:9000/poc/divorce", jwt.getDefaultUrl());
        assertEquals("60", jwt.getSubject());
        assertEquals("citizen,divorce-private-beta,citizen-loa1,divorce-private-beta-loa1", jwt.getData());
        assertEquals("ACCESS", jwt.getType());
        assertEquals("john", jwt.getForename());
        assertEquals("smith", jwt.getSurname());
        assertEquals(60, jwt.getId());
        assertEquals(LocalDate.of(2017, 9, 20), jwt.getExpiration());
        assertEquals(LocalDate.of(2017, 9, 20), jwt.getIssuedAt());
        assertEquals("8r1t3vu3g9ddt8olhjr2j2sj7g", jwt.getJwtId());
        assertEquals("Divorce", jwt.getDefaultService());
        assertEquals(1, jwt.getLevelOfAssurance());
        assertEquals("divorce-private-beta", jwt.getGroup());
    }

    @Test
    public void createThrowsJwtParsingException() throws Exception {
        String encodedJwt = "abc";

        try {
            jwtFactory.create(encodedJwt);
            fail("Expected JwtParsingException");
        }
        catch (JwtParsingException e) {
            assertTrue(e.getCause() instanceof ParseException);
        }
    }
}