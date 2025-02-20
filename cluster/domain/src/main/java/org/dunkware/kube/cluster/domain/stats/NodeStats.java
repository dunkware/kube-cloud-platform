package org.dunkware.kube.cluster.domain.stats;

import lombok.Data;

import java.util.Map;

@Data
public class NodeStats {
    private String name;
    private Map<String, String> labels;
    private boolean ready;
    private double cpuRequestsCores;
    private double cpuLimitsCores;
    private double cpuCapacityCores;
    private double cpuUsagePercentage;
    private double memoryRequestsBytes;
    private double memoryLimitsBytes;
    private double memoryCapacityBytes;
    private double memoryUsagePercentage;
    private int podCount;
}