package uk.gov.hmcts.reform.divorce.health;

import com.google.common.collect.ImmutableList;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthAggregator;
import org.springframework.boot.actuate.health.Status;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CustomHealthAggregator implements HealthAggregator {

    private List<String> statusOrder;

    public CustomHealthAggregator() {
        this.statusOrder = ImmutableList.of(
            Status.DOWN.getCode(),
            Status.OUT_OF_SERVICE.getCode(),
            Status.UP.getCode(),
            Status.UNKNOWN.getCode());
    }

    @Override
    public final Health aggregate(Map<String, Health> healths) {

        List<Status> statusCandidates = healths
                                        .values()
                                        .stream()
                                        .map(Health::getStatus)
                                        .collect(Collectors.toList());

        Status status = aggregateStatus(statusCandidates);
        Map<String, Object> details = aggregateDetails(healths);
        return new Health.Builder(status, details).build();
    }

    private Status aggregateStatus(List<Status> candidates) {
        // Only sort those status instances that we know about
        List<Status> filteredCandidates = new ArrayList<Status>();
        for (Status candidate : candidates) {
            if (this.statusOrder.contains(candidate.getCode())) {
                filteredCandidates.add(candidate);
            }
        }
        // If no status is given return UNKNOWN
        if (filteredCandidates.isEmpty()) {
            return Status.UNKNOWN;
        }
        // Sort given Status instances by configured order
        filteredCandidates.sort(new StatusComparator(this.statusOrder));
        return filteredCandidates.get(0);
    }

    private Map<String, Object> aggregateDetails(Map<String, Health> healths) {
        return new LinkedHashMap<>(healths);
    }

    private class StatusComparator implements Comparator<Status> {

        private final List<String> statusOrder;

        StatusComparator(List<String> statusOrder) {
            this.statusOrder = statusOrder;
        }

        @Override
        public int compare(Status s1, Status s2) {
            int i1 = this.statusOrder.indexOf(s1.getCode());
            int i2 = this.statusOrder.indexOf(s2.getCode());
            return (i1 < i2 ? -1 : (i1 == i2 ? s1.getCode().compareTo(s2.getCode()) : 1));
        }

    }

}
