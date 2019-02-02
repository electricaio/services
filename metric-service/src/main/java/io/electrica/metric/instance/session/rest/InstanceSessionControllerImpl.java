package io.electrica.metric.instance.session.rest;

import io.electrica.metric.instance.session.dto.InstanceSessionDetailedDescriptorDto;
import io.electrica.metric.instance.session.service.dto.InstanceSessionDtoService;
import io.electrica.metric.instance.session.dto.InstanceSessionDescriptorDto;
import io.electrica.metric.instance.session.dto.InstanceSessionDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;

@RestController
public class InstanceSessionControllerImpl implements InstanceSessionController {
    private final InstanceSessionDtoService instanceSessionDtoService;

    @Inject
    public InstanceSessionControllerImpl(InstanceSessionDtoService instanceSessionDtoService) {
        this.instanceSessionDtoService = instanceSessionDtoService;
    }

    @PreAuthorize("#oauth2.hasScope('sdk')")
    @Override
    public ResponseEntity<Void> started(@RequestBody InstanceSessionDetailedDescriptorDto dto) {
        instanceSessionDtoService.start(dto);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("#oauth2.hasScope('sdk')")
    @Override
    public ResponseEntity<Void> expired(@RequestBody InstanceSessionDescriptorDto dto) {
        instanceSessionDtoService.expire(dto);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("#oauth2.hasScope('sdk')")
    @Override
    public ResponseEntity<Void> stopped(@RequestBody InstanceSessionDescriptorDto dto) {
        instanceSessionDtoService.stop(dto);
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
