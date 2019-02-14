package io.electrica.metric.common.mq;

import javax.validation.constraints.NotNull;

public interface MetricEvent {
    @NotNull
    Long getOrganizationId();

    @NotNull
    Long getUserId();

    @NotNull
    Long getAccessKeyId();
}
