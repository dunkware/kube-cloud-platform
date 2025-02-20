package org.dunkware.cloud.kube.config;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.util.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class KubernetesConfig {

    @Value("${kubernetes.config.file:}")
    private String kubeConfigPath;

    @Bean
    public ApiClient kubernetesApiClient() throws IOException {
        ApiClient client;
        if (kubeConfigPath != null && !kubeConfigPath.isEmpty()) {
            // Use local kubeconfig file
            client = Config.fromConfig(kubeConfigPath);
        } else {
            // Use in-cluster config when deployed to Kubernetes
            client = Config.defaultClient();
        }
        // Set reasonable timeouts
        client.setConnectTimeout(10000);
        client.setReadTimeout(30000);
        client.setWriteTimeout(30000);

        return client;
    }
}