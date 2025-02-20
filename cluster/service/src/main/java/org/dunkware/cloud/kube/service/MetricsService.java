
        package org.dunkware.cloud.kube.service;


import io.kubernetes.client.custom.NodeMetrics;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.apis.CustomObjectsApi;
import io.kubernetes.client.openapi.models.*;
import io.kubernetes.client.custom.Quantity;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.dunkware.kube.cluster.domain.stats.ClusterStats;
import org.dunkware.kube.cluster.domain.stats.NodeStats;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
        @Slf4j
        @Service
        public class MetricsService {
            private final CoreV1Api coreV1Api;
            private final CustomObjectsApi customObjectsApi;

            public MetricsService(ApiClient apiClient) {
                this.coreV1Api = new CoreV1Api(apiClient);
                this.customObjectsApi = new CustomObjectsApi(apiClient);
            }

            public ClusterStats getClusterMetrics() {
                try {
                    ClusterStats stats = new ClusterStats();
                    List<NodeStats> nodeStatsList = new ArrayList<>();

                    // Get nodes
                    V1NodeList nodes = coreV1Api.listNode(null, null, null, null, null, null, null, null, null, null);
                    stats.setNodeCount(nodes.getItems().size());

                    // Get pods
                    V1PodList pods = coreV1Api.listPodForAllNamespaces(null, null, null, null, null, null, null, null, null, null);
                    stats.setPodCount(pods.getItems().size());

                    // Get namespaces
                    V1NamespaceList namespaces = coreV1Api.listNamespace(null, null, null, null, null, null, null, null, null, null);
                    stats.setNamespaceCount(namespaces.getItems().size());

                    // Get PVCs
                    V1PersistentVolumeClaimList pvcs = coreV1Api.listPersistentVolumeClaimForAllNamespaces(null, null, null, null, null, null, null, null, null, null);
                    stats.setPvcCount(pvcs.getItems().size());

                    // Calculate total storage metrics
                    double totalStorageRequested = pvcs.getItems().stream()
                            .mapToDouble(pvc -> {
                                Quantity storage = pvc.getSpec().getResources().getRequests().get("storage");
                                return storage != null ? storage.getNumber().doubleValue() : 0;
                            })
                            .sum();
                    stats.setTotalStorageRequestedGb(totalStorageRequested / (1024 * 1024 * 1024));

                    // Get metrics from metrics.k8s.io API
                    Map<String, Object> nodeMetrics = getNodeMetrics();

                    // Process each node
                    for (V1Node node : nodes.getItems()) {
                        NodeStats nodeStats = new NodeStats();
                        String nodeName = node.getMetadata().getName();
                        nodeStats.setName(nodeName);
                        nodeStats.setLabels(node.getMetadata().getLabels());

                        // Check node readiness
                        boolean ready = node.getStatus().getConditions().stream()
                                .filter(condition -> "Ready".equals(condition.getType()))
                                .findFirst()
                                .map(condition -> "True".equals(condition.getStatus()))
                                .orElse(false);
                        nodeStats.setReady(ready);

                        // Get node capacity
                        V1NodeStatus status = node.getStatus();
                        Map<String, Quantity> capacity = status.getCapacity();

                        // CPU metrics
                        double cpuCapacity = capacity.get("cpu").getNumber().doubleValue();
                        nodeStats.setCpuCapacityCores(cpuCapacity);

                        // Memory metrics
                        double memoryCapacity = capacity.get("memory").getNumber().doubleValue();
                        nodeStats.setMemoryCapacityBytes(memoryCapacity);

                        // Calculate resource requests and limits for this node
                        List<V1Pod> nodePods = pods.getItems().stream()
                                .filter(pod -> nodeName.equals(pod.getSpec().getNodeName()))
                                .collect(Collectors.toList());

                        nodeStats.setPodCount(nodePods.size());

                        // Calculate CPU and Memory requests/limits for the node
                        ResourceCalculator calculator = new ResourceCalculator(nodePods);
                        nodeStats.setCpuRequestsCores(calculator.getCpuRequests());
                        nodeStats.setCpuLimitsCores(calculator.getCpuLimits());
                        nodeStats.setMemoryRequestsBytes(calculator.getMemoryRequests());
                        nodeStats.setMemoryLimitsBytes(calculator.getMemoryLimits());

                        // Get usage metrics for the node
                        if (nodeMetrics != null) {
                            @SuppressWarnings("unchecked")
                            List<Map<String, Object>> items = (List<Map<String, Object>>) nodeMetrics.get("items");
                            if (items != null) {
                                Optional<Map<String, Object>> nodeMetric = items.stream()
                                        .filter(item -> nodeName.equals(((Map<String, String>) item.get("metadata")).get("name")))
                                        .findFirst();

                                nodeMetric.ifPresent(metric -> {
                                    @SuppressWarnings("unchecked")
                                    Map<String, Object> usage = (Map<String, Object>) metric.get("usage");
                                    if (usage != null) {
                                        String cpuUsage = (String) usage.get("cpu");
                                        String memoryUsage = (String) usage.get("memory");

                                        // Parse CPU usage (usually in format like "250m" for millicores)
                                        double cpuUsageValue = parseCpuValue(cpuUsage);
                                        nodeStats.setCpuUsagePercentage((cpuUsageValue / cpuCapacity) * 100);

                                        // Parse memory usage (usually in format like "1Gi" or "1024Mi")
                                        double memoryUsageValue = parseMemoryValue(memoryUsage);
                                        nodeStats.setMemoryUsagePercentage((memoryUsageValue / memoryCapacity) * 100);
                                    }
                                });
                            }
                        }

                        nodeStatsList.add(nodeStats);
                    }

                    // Set cluster-wide CPU and memory totals
                    double totalCpuCapacity = nodeStatsList.stream()
                            .mapToDouble(NodeStats::getCpuCapacityCores)
                            .sum();
                    double totalCpuRequests = nodeStatsList.stream()
                            .mapToDouble(NodeStats::getCpuRequestsCores)
                            .sum();
                    double totalMemoryCapacity = nodeStatsList.stream()
                            .mapToDouble(NodeStats::getMemoryCapacityBytes)
                            .sum();
                    double totalMemoryRequests = nodeStatsList.stream()
                            .mapToDouble(NodeStats::getMemoryRequestsBytes)
                            .sum();

                    stats.setCpuCores(totalCpuCapacity);
                    stats.setMemoryGb(totalMemoryCapacity / (1024 * 1024 * 1024));
                    stats.setCpuUsagePercentage((totalCpuRequests / totalCpuCapacity) * 100);
                    stats.setMemoryUsagePercentage((totalMemoryRequests / totalMemoryCapacity) * 100);
                    stats.setNodeStats(nodeStatsList);

                    return stats;
                } catch (ApiException e) {
                    log.error("Error fetching cluster metrics", e);
                    throw new RuntimeException("Failed to fetch cluster metrics", e);
                }
            }

            private Map<String, Object> getNodeMetrics() {
                try {
                    return (Map<String, Object>) customObjectsApi.listClusterCustomObject(
                            "metrics.k8s.io",
                            "v1beta1",
                            "nodes",
                            null, null, null, null, null, null, null, null, null, null
                    );
                } catch (ApiException e) {
                    log.warn("Could not fetch metrics from metrics.k8s.io API", e);
                    return null;
                }
            }

            private double parseCpuValue(String cpuStr) {
                if (cpuStr == null || cpuStr.isEmpty()) {
                    return 0.0;
                }

                // Remove any non-digit characters except decimal point and 'm'
                if (cpuStr.endsWith("m")) {
                    // Convert millicores to cores
                    return Double.parseDouble(cpuStr.substring(0, cpuStr.length() - 1)) / 1000.0;
                }
                return Double.parseDouble(cpuStr);
            }

            private double parseMemoryValue(String memoryStr) {
                if (memoryStr == null || memoryStr.isEmpty()) {
                    return 0.0;
                }

                // Parse values like "1Gi", "1024Mi", "1024Ki", etc.
                Quantity quantity = Quantity.fromString(memoryStr);
                return quantity.getNumber().doubleValue();
            }

            @Data
            private static class ResourceCalculator {
                private double cpuRequests;
                private double cpuLimits;
                private double memoryRequests;
                private double memoryLimits;

                public ResourceCalculator(List<V1Pod> pods) {
                    for (V1Pod pod : pods) {
                        for (V1Container container : pod.getSpec().getContainers()) {
                            V1ResourceRequirements resources = container.getResources();
                            if (resources == null) continue;

                            // Process requests
                            Map<String, Quantity> requests = resources.getRequests();
                            if (requests != null) {
                                if (requests.containsKey("cpu")) {
                                    cpuRequests += requests.get("cpu").getNumber().doubleValue();
                                }
                                if (requests.containsKey("memory")) {
                                    memoryRequests += requests.get("memory").getNumber().doubleValue();
                                }
                            }

                            // Process limits
                            Map<String, Quantity> limits = resources.getLimits();
                            if (limits != null) {
                                if (limits.containsKey("cpu")) {
                                    cpuLimits += limits.get("cpu").getNumber().doubleValue();
                                }
                                if (limits.containsKey("memory")) {
                                    memoryLimits += limits.get("memory").getNumber().doubleValue();
                                }
                            }
                        }
                    }
                }
            }
        }