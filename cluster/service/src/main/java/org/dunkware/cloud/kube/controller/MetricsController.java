package org.dunkware.cloud.kube.controller;


import org.dunkware.cloud.kube.service.MetricsService;
import lombok.RequiredArgsConstructor;
import org.dunkware.kube.cluster.domain.stats.ClusterStats;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/metrics")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class MetricsController {
    private final MetricsService metricsService;

    @GetMapping
    public ClusterStats getMetrics() {
        return metricsService.getClusterMetrics();
    }
}
 // Image Credential
 // REgisstry
  // template Spring Config Template 