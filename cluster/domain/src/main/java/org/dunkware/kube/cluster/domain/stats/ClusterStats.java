package org.dunkware.kube.cluster.domain.stats;

import lombok.Data;

import java.util.List;

@Data
public class ClusterStats {
    private double cpuCores;
    private double memoryGb;
    private int podCount;
    private int nodeCount;
    private int namespaceCount;
    private double cpuUsagePercentage;
    private double memoryUsagePercentage;
    private List<NodeStats> nodeStats;

    // PVC Stats
    private int pvcCount;
    private double totalStorageRequestedGb;
    private double totalStorageCapacityGb;
    private double storageUsagePercentage;
}