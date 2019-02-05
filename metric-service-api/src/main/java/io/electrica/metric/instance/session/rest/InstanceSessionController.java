package io.electrica.metric.instance.session.rest;

import io.electrica.metric.instance.session.dto.InstanceSessionDto;
import io.electrica.metric.instance.session.model.SessionState;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

import static io.electrica.common.rest.PathConstants.V1;


public interface InstanceSessionController {
    String PREFIX = V1 + "/metrics/instance-sessions";

    @GetMapping(path = PREFIX + "/instance-sessions")
    List<InstanceSessionDto> getInstanceSessions(
            @PageableDefault Pageable pageable,
            @RequestParam(value = "startDate")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(value = "endDate")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(value = "nameStartWith", required = false) String nameStartWith,
            @RequestParam(value = "sessionStates[]", required = false) SessionState[] sessionStates,
            @RequestParam(value = "accessKeyId", required = false) Long accessKeyId,
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "organizationId", required = false) Long organizationId
    );

}
