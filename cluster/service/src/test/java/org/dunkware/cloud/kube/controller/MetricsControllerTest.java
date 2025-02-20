package org.dunkware.cloud.kube.controller;

import org.dunkware.cloud.kube.service.MetricsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MetricsController.class)
public class MetricsControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MetricsService metricsService;

    @Test
    public void shouldReturnMetrics() {
        // Add your test implementation
    }
}