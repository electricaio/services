package io.electrica.metric.instance.session.feign;

import io.electrica.metric.instance.session.dto.InstanceSessionDto;
import io.electrica.metric.instance.session.model.SessionState;
import io.electrica.metric.instance.session.rest.InstanceSessionController;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@FeignClient(name = "instanceSessionClient", url = "${feign.metricService.url}")
public interface InstanceSessionClient extends InstanceSessionController {

    @GetMapping(path = PREFIX + "/instance-sessions")
    List<InstanceSessionDto> feignGetInstanceSessions(
            @RequestParam(value = "pageNumber", required = false) int pageNumber,
            @RequestParam(value = "pageSize", required = false) int pageSize,
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
