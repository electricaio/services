package io.electrica.metric.instance.session.feign;

import io.electrica.metric.instance.session.dto.UpsertInstanceSessionDto;
import io.electrica.metric.instance.session.rest.InstanceSessionController;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.concurrent.Future;

@FeignClient(name = "instanceSessionClient", url = "${feign.metricService.url}")
public interface InstanceSessionClient extends InstanceSessionController {

    @PostMapping(PREFIX + "/started")
    Future<Void> asyncStarted(@RequestBody UpsertInstanceSessionDto dto);

    @PostMapping(PREFIX + "/{instanceSessionId}/expired")
    Future<Void> asyncExpired(@PathVariable("instanceSessionId") UUID id,
                              @RequestParam("sessionStartedClientTime")
                              @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                      ZonedDateTime sessionStartedClientTime);

    @PostMapping(PREFIX + "/{instanceSessionId}/stopped")
    Future<Void> asyncStopped(@PathVariable("instanceSessionId") UUID id,
                              @RequestParam("sessionStartedClientTime")
                              @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                      ZonedDateTime sessionStartedClientTime);
}
