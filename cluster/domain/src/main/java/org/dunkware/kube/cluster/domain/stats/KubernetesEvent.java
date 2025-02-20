package org.dunkware.kube.cluster.domain.stats;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class KubernetesEvent {
    private String type;  // Normal or Warning
    private String reason;
    private String message;
    private String namespace;
    private String involvedObject;
    private String involvedObjectKind;
    private LocalDateTime lastTimestamp;
    private int count;
}
