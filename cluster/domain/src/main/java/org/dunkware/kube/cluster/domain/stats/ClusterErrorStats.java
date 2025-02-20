package org.dunkware.kube.cluster.domain.stats;

import lombok.Data;

import java.util.List;
import java.util.Map;
@Data
public class ClusterErrorStats {
    private int totalErrorCount;
    private int totalWarningCount;
    private List<KubernetesEvent> recentEvents;
    private Map<String, Integer> errorsByType;  // e.g., "Failed", "BackOff", etc.
    private Map<String, Integer> errorsByNamespace;
    private Map<String, Integer> errorsByResource;  // e.g., "Pod", "Deployment", etc.
}
