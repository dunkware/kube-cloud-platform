package org.dunkware.cloud.kube.service;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.dunkware.kube.cluster.domain.stats.ClusterErrorStats;
import org.dunkware.kube.cluster.domain.stats.KubernetesEvent;
import org.springframework.stereotype.Service;


// After
import java.time.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.CoreV1Event;
import io.kubernetes.client.openapi.models.CoreV1EventList;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;



@Slf4j
@Service
public class EventTrackerService {
    private final CoreV1Api coreV1Api;
    private static final int DEFAULT_RECENT_EVENTS_LIMIT = 50;

    public EventTrackerService(ApiClient apiClient) {
        this.coreV1Api = new CoreV1Api(apiClient);
    }

    public ClusterErrorStats getClusterErrorStats() {
        try {
            ClusterErrorStats stats = new ClusterErrorStats();
            stats.setErrorsByType(new HashMap<>());
            stats.setErrorsByNamespace(new HashMap<>());
            stats.setErrorsByResource(new HashMap<>());

            CoreV1EventList events = coreV1Api.listEventForAllNamespaces(
                    null, null, null, null, null,
                    null, null, null, null, null
            );

            List<KubernetesEvent> allEvents = new ArrayList<>();
            int errorCount = 0;
            int warningCount = 0;

            for (CoreV1Event event : events.getItems()) {
                String type = event.getType();
                String reason = event.getReason();

                // Skip normal events unless they're related to errors
                if ("Normal".equals(type) && !isErrorRelated(reason)) {
                    continue;
                }

                if ("Warning".equals(type)) {
                    warningCount++;
                }
                if (isError(reason)) {
                    errorCount++;
                }

                KubernetesEvent k8sEvent = KubernetesEvent.builder()
                        .type(type)
                        .reason(reason)
                        .message(event.getMessage())
                        .namespace(event.getMetadata().getNamespace())
                        .involvedObject(event.getInvolvedObject().getName())
                        .involvedObjectKind(event.getInvolvedObject().getKind())
                        .lastTimestamp(parseTimestamp(event.getLastTimestamp()))
                        .count(event.getCount() != null ? event.getCount() : 1)
                        .build();

                allEvents.add(k8sEvent);

                // Update error type counts
                stats.getErrorsByType().merge(reason, 1, Integer::sum);

                // Update namespace counts
                stats.getErrorsByNamespace().merge(k8sEvent.getNamespace(), 1, Integer::sum);

                // Update resource type counts
                stats.getErrorsByResource().merge(k8sEvent.getInvolvedObjectKind(), 1, Integer::sum);
            }

            stats.setTotalErrorCount(errorCount);
            stats.setTotalWarningCount(warningCount);

            // Sort events by timestamp (most recent first) and limit the number
            stats.setRecentEvents(allEvents.stream()
                    .sorted(Comparator.comparing(KubernetesEvent::getLastTimestamp).reversed())
                    .limit(DEFAULT_RECENT_EVENTS_LIMIT)
                    .collect(Collectors.toList()));

            return stats;
        } catch (ApiException e) {
            log.error("Error fetching Kubernetes events", e);
            throw new RuntimeException("Failed to fetch Kubernetes events", e);
        }
    }

    private boolean isErrorRelated(String reason) {
        // List of reasons that indicate errors, even if marked as "Normal" type
        Set<String> errorReasons = Set.of(
                "Failed", "FailedCreate", "FailedScheduling", "FailedMount",
                "BackOff", "Error", "NodeNotReady", "Unhealthy",
                "FailedValidation", "FailedPostStartHook", "FailedPreStopHook",
                "OutOfDisk", "MemoryPressure", "DiskPressure"
        );
        return errorReasons.contains(reason);
    }

    private boolean isError(String reason) {
        // Additional check for specific error conditions
        return reason != null && (
                reason.contains("Fail") ||
                        reason.contains("Error") ||
                        reason.contains("BackOff") ||
                        reason.contains("Unhealthy")
        );
    }

    // After
    private LocalDateTime parseTimestamp(OffsetDateTime timestamp) {
        if (timestamp == null) {
            return LocalDateTime.now();
        }
        return timestamp.toLocalDateTime();
    }

    @Data
    @Builder
    public static class EventSummary {
        private String namespace;
        private String resourceType;
        private String resourceName;
        private String message;
        private int count;
        private LocalDateTime lastSeen;
    }

    public List<EventSummary> getFailedPodEvents() {
        try {
            return coreV1Api.listEventForAllNamespaces(
                            null, null, null, "type=Warning",
                            null, null, null, null, null, null
                    )
                    .getItems()
                    .stream()
                    .filter(event -> "Pod".equals(event.getInvolvedObject().getKind()))
                    .filter(event -> isErrorRelated(event.getReason()))
                    .map(event -> EventSummary.builder()
                            .namespace(event.getMetadata().getNamespace())
                            .resourceType("Pod")
                            .resourceName(event.getInvolvedObject().getName())
                            .message(event.getMessage())
                            .count(event.getCount() != null ? event.getCount() : 1)
                            .lastSeen(parseTimestamp(event.getLastTimestamp()))
                            .build())
                    .sorted(Comparator.comparing(EventSummary::getLastSeen).reversed())
                    .collect(Collectors.toList());
        } catch (ApiException e) {
            log.error("Error fetching pod failure events", e);
            throw new RuntimeException("Failed to fetch pod failure events", e);
        }
    }
}