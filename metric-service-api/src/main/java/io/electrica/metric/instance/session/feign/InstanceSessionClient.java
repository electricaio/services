package io.electrica.metric.instance.session.feign;

import io.electrica.metric.instance.session.dto.InstanceSessionDescriptorDto;
import io.electrica.metric.instance.session.dto.InstanceSessionDetailedDescriptorDto;
import io.electrica.metric.instance.session.rest.InstanceSessionController;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.concurrent.Future;

@FeignClient(name = "instanceSessionClient", url = "${feign.metricService.url}")
public interface InstanceSessionClient extends InstanceSessionController {

    @PostMapping(PREFIX + "/started")
    Future<Void> asyncStarted(@RequestBody InstanceSessionDetailedDescriptorDto dto);

    @PostMapping(PREFIX + "/expired")
    Future<Void> asyncExpired(@RequestBody InstanceSessionDescriptorDto dto);

    @PostMapping(PREFIX + "/stopped")
    Future<Void> asyncStopped(@RequestBody InstanceSessionDescriptorDto dto);
}
