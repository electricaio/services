package io.electrica.metric.instance.session.rest;

import io.electrica.metric.instance.session.dto.InstanceSessionDescriptorDto;
import io.electrica.metric.instance.session.dto.InstanceSessionDetailedDescriptorDto;
import io.electrica.metric.instance.session.dto.InstanceSessionDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static io.electrica.common.rest.PathConstants.V1;


public interface InstanceSessionController {
    String PREFIX = V1 + "/metrics/instance-sessions";

    @PostMapping(PREFIX + "/started")
    ResponseEntity<Void> started(@RequestBody InstanceSessionDetailedDescriptorDto dto);

    @PostMapping(PREFIX + "/expired")
    ResponseEntity<Void> expired(@RequestBody InstanceSessionDescriptorDto dto);

    @PostMapping(PREFIX + "/stopped")
    ResponseEntity<Void> stopped(@RequestBody InstanceSessionDescriptorDto dto);

    @GetMapping(path = PREFIX + "/user-instance-sessions")
    List<InstanceSessionDto> getInstanceSessions(
            @PageableDefault Pageable pageable,
            @RequestParam(value = "accessKeyId", required = false) Long accessKeyId,
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "organizationId", required = false) Long organizationId
    );

}
