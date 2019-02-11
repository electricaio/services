package io.electrica.metric.instance.session.rest;

import io.electrica.metric.instance.session.dto.InstanceSessionDto;
import io.electrica.metric.instance.session.model.SessionState;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static io.electrica.common.rest.PathConstants.V1;


public interface InstanceSessionController {
    String PREFIX = V1 + "/metrics/instance-sessions";

    @GetMapping(path = PREFIX + "/instance-sessions/{id}")
    InstanceSessionDto getInstanceSession(@PathVariable("id") UUID id);

    @GetMapping(path = PREFIX + "/instance-sessions")
    List<InstanceSessionDto> getInstanceSessions(
            @PageableDefault Pageable pageable,
            @RequestParam(value = "startTime", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(value = "endTime", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(value = "nameStartWith", required = false) String nameStartWith,
            @RequestParam(value = "state[]", required = false) Set<SessionState> sessionStates,
            @RequestParam(value = "accessKeyId", required = false) Long accessKeyId,
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "organizationId", required = false) Long organizationId
    );

}
