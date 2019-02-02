package io.electrica.metric.instance.session.rest;

import io.electrica.metric.instance.session.service.dto.InstanceSessionDtoService;
import io.electrica.metric.instance.session.dto.UpsertInstanceSessionDto;
import io.electrica.metric.instance.session.dto.InstanceSessionDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@RestController
public class InstanceSessionControllerImpl implements InstanceSessionController {
    private final InstanceSessionDtoService instanceSessionDtoService;

    @Inject
    public InstanceSessionControllerImpl(InstanceSessionDtoService instanceSessionDtoService) {
        this.instanceSessionDtoService = instanceSessionDtoService;
    }

    @PreAuthorize("#oauth2.hasScope('sdk')")
    @Override
    public ResponseEntity<Void> started(@RequestBody UpsertInstanceSessionDto dto) {
        instanceSessionDtoService.start(dto);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("#oauth2.hasScope('sdk')")
    @Override
    public ResponseEntity<Void> expired(@PathVariable("instanceSessionId") UUID id,
                                        @RequestParam("sessionStartedClientTime")
                                        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                ZonedDateTime sessionStartedClientTime) {
        instanceSessionDtoService.expire(id, sessionStartedClientTime);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("#oauth2.hasScope('sdk')")
    @Override
    public ResponseEntity<Void> stopped(@PathVariable("instanceSessionId") UUID id,
                                        @RequestParam("sessionStartedClientTime")
                                        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                ZonedDateTime sessionStartedClientTime) {
        instanceSessionDtoService.stop(id, sessionStartedClientTime);
        return ResponseEntity.ok().build();
    }

    @Override
    public List<InstanceSessionDto> getInstanceSessions(
            @PageableDefault Pageable pageable,
            @RequestParam(value = "accessKeyId", required = false) Long accessKeyId,
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "organizationId", required = false) Long organizationId
    ) {
        if (accessKeyId != null) {
            return instanceSessionDtoService.findByAccessKeyId(accessKeyId, pageable);
        } else if (userId != null) {
            return instanceSessionDtoService.findByUserId(userId, pageable);
        } else if (organizationId != null) {
            return instanceSessionDtoService.findByOrganizationId(organizationId, pageable);
        } else {
            return instanceSessionDtoService.findByCurrentUserId(pageable);
        }
    }
}
