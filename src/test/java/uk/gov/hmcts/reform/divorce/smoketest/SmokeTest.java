package uk.gov.hmcts.reform.divorce.smoketest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.reform.divorce.transformservice.controller.CcdSubmissionController;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SmokeTest {

    @Autowired
    private CcdSubmissionController controller;

    @Test
    public void contexLoads() throws Exception {
        assertNotNull(controller);
    }
}
