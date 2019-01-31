package io.electrica.metric.instance.session.rest;

import io.electrica.metric.instance.session.dto.UpsertInstanceSessionDto;
import io.electrica.metric.instance.session.dto.InstanceSessionDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import static io.electrica.common.rest.PathConstants.V1;


public interface InstanceSessionController {
    String PREFIX = V1 + "/metrics/instance-sessions";

    @PostMapping(PREFIX + "/started")
    ResponseEntity<Void> started(@RequestBody UpsertInstanceSessionDto dto);

    @PostMapping(PREFIX + "/{instanceSessionId}/expired")
    ResponseEntity<Void> expired(@PathVariable("instanceSessionId") UUID id,
                                 @RequestParam("sessionStartedClientTime")
                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                        ZonedDateTime sessionStartedClientTime);

    @PostMapping(PREFIX + "/{instanceSessionId}/stopped")
    ResponseEntity<Void> stopped(@PathVariable("instanceSessionId") UUID id,
                                 @RequestParam("sessionStartedClientTime")
                              @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                      ZonedDateTime sessionStartedClientTime);

    @GetMapping(path = PREFIX + "/user-instance-sessions")
    List<InstanceSessionDto> getInstanceSessions(
            @PageableDefault Pageable pageable,
            @RequestParam(value = "accessKeyId", required = false) Long accessKeyId,
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "organizationId", required = false) Long organizationId
    );

}
