package uk.gov.hmcts.reform.divorce.transformservice.mapping;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.reform.divorce.CaseProgressionApplication;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.ccd.CoreCaseData;
import uk.gov.hmcts.reform.divorce.transformservice.domain.model.divorceapplicationdata.DivorceSession;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;

import static java.time.format.DateTimeFormatter.ofPattern;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CaseProgressionApplication.class)
public class AddressesCaseToCCDMapperTest {

    @Autowired
    private DivorceCaseToCCDSubmissionMapper mapper;

    @Test
    public void shouldMapAllAndTransformAllFieldsForAdulteryDifferentAddressMappingScenario()
        throws URISyntaxException, IOException {

        CoreCaseData expectedCoreCaseData = (CoreCaseData) DivorceCaseToCCDMapperTestUtil
            .jsonToObject("fixtures/ccdmapping/addresscase.json", AddressesCaseToCCDMapperTest.class,
                CoreCaseData.class);
        expectedCoreCaseData.setCreatedDate(LocalDate.now().format(ofPattern("yyyy-MM-dd")));
        DivorceSession divorceSession = (DivorceSession) DivorceCaseToCCDMapperTestUtil
            .jsonToObject("divorce-payload-json/addresses.json", AddressesCaseToCCDMapperTest.class,
                DivorceSession.class);

        CoreCaseData actualCoreCaseData = mapper.divorceCaseDataToCourtCaseData(divorceSession);

        assertThat(actualCoreCaseData, samePropertyValuesAs(expectedCoreCaseData));
    }

    @Test
    public void shouldMapAllAndTransformAllFieldsForAdulteryWithEmptyRespondentAddressMappingScenario()
        throws Exception {

        CoreCaseData expectedCoreCaseData = (CoreCaseData) DivorceCaseToCCDMapperTestUtil
            .jsonToObject("fixtures/ccdmapping/addresscase-with-empty-respondent-homeaddress.json",
                AddressesCaseToCCDMapperTest.class,
                CoreCaseData.class);
        expectedCoreCaseData.setCreatedDate(LocalDate.now().format(ofPattern("yyyy-MM-dd")));
        DivorceSession divorceSession = (DivorceSession) DivorceCaseToCCDMapperTestUtil
            .jsonToObject("divorce-payload-json/addresses-unknow-respondent.json",
                AddressesCaseToCCDMapperTest.class,
                DivorceSession.class);

        CoreCaseData actualCoreCaseData = mapper.divorceCaseDataToCourtCaseData(divorceSession);
        assertThat(actualCoreCaseData, samePropertyValuesAs(expectedCoreCaseData));
    }
}
