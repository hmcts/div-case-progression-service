package uk.gov.hmcts.reform.divorce.health;

import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CustomHealthAggregatorTest {

    private CustomHealthAggregator underTest;

    @Before
    public void setUp() {
        underTest = new CustomHealthAggregator();
    }

    @Test
    public void aggregateShouldReturnUnknownWhenThereAreNoHealthChecks() {
        Health health = underTest.aggregate(Collections.emptyMap());
        assertEquals(Status.UNKNOWN, health.getStatus());
        assertTrue(health.getDetails().isEmpty());
    }

    @Test
    public void aggregateShouldIgnoreServicesWithUnknownStatuses() {
        Map<String, Health> healts = ImmutableMap.of("service1", Health.up().build(),
            "service2", Health.status(new Status("Happy")).build());

        Health health = underTest.aggregate(healts);
        assertEquals(Status.UP, health.getStatus());
    }

    @Test
    public void aggregateShouldSetTheStatusToTheLowestStatus() {
        Map<String, Health> healts = ImmutableMap.of("service1", Health.up().build(),
            "service2", Health.down().build());

        Health health = underTest.aggregate(healts);
        assertEquals(Status.DOWN, health.getStatus());
    }

    @Test
    public void aggregateShouldNotIgnoreTheStatusOfTheDraftStoreAPI() {
        Map<String, Health> healts = ImmutableMap.of("service1", Health.up().build(),
            "draftStoreApi", Health.down().build());

        Health health = underTest.aggregate(healts);
        assertEquals(Status.DOWN, health.getStatus());
        assertEquals(2, health.getDetails().entrySet().size());
        assertEquals(Status.UP, ((Health) health.getDetails().get("service1")).getStatus());
        assertEquals(Status.DOWN, ((Health) health.getDetails().get("draftStoreApi")).getStatus());
    }

    @Test
    public void aggregateShouldBeUpIfAllServicesAreUp() {
        Map<String, Health> healts = ImmutableMap.of("service1", Health.up().build(),
            "draftStoreApi", Health.up().build());

        Health health = underTest.aggregate(healts);
        assertEquals(Status.UP, health.getStatus());
        assertEquals(2, health.getDetails().entrySet().size());
        assertEquals(Status.UP, ((Health) health.getDetails().get("service1")).getStatus());
        assertEquals(Status.UP, ((Health) health.getDetails().get("draftStoreApi")).getStatus());
    }


}
