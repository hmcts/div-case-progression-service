package uk.gov.hmcts.reform.divorce.caseprogression.smoke;

import net.thucydides.core.annotations.WithTag;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.hmcts.reform.divorce.support.IntegrationTest;

public class SmokeTest extends IntegrationTest {

    @Test
    @WithTag("test-type:smoke")
    public void simpleContextLoadTest(){
        //Dummy test
        Assert.assertTrue(true);
    }

}
