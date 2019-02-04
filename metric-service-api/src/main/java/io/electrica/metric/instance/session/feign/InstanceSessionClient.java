package io.electrica.metric.instance.session.feign;

import io.electrica.metric.instance.session.rest.InstanceSessionController;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "instanceSessionClient", url = "${feign.metricService.url}")
public interface InstanceSessionClient extends InstanceSessionController {
}
