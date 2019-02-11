package io.electrica.metric.instance.session.rest;

import io.electrica.common.exception.BadRequestServiceException;
import io.electrica.metric.instance.session.dto.InstanceSessionFilter;
import io.electrica.metric.instance.session.model.SessionState;
import io.electrica.metric.instance.session.service.dto.InstanceSessionDtoService;
import io.electrica.metric.instance.session.dto.InstanceSessionDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
public class InstanceSessionControllerImpl implements InstanceSessionController {
    private final InstanceSessionDtoService instanceSessionDtoService;

    @Inject
    public InstanceSessionControllerImpl(InstanceSessionDtoService instanceSessionDtoService) {
        this.instanceSessionDtoService = instanceSessionDtoService;
    }

    @PreAuthorize("#common.hasPermission('ReadInstanceSession')")
    @PostAuthorize("#common.isUser(returnObject.getBody().getUserId()) OR #common.isSuperAdmin()")
    public InstanceSessionDto getInstanceSession(@PathVariable("id") UUID id) {
        return instanceSessionDtoService.findById(id)
                .orElseThrow(() -> new BadRequestServiceException("Instance not found"));
    }

    @PreAuthorize("#common.hasPermission('ReadInstanceSession') " +
            " AND ( #common.isSuperAdmin() OR #common.isUser(#userId) )")
    @Override
    public List<InstanceSessionDto> getInstanceSessions(
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
    ) {
        EnumSet<SessionState> sessionStatesEnumSet = EnumSet.noneOf(SessionState.class);
        if (sessionStates != null) {
            sessionStatesEnumSet.addAll(sessionStates);
        }
        return instanceSessionDtoService.findByFilter(new InstanceSessionFilter(
                startTime,
                endTime,
                nameStartWith,
                sessionStatesEnumSet,
                accessKeyId,
                userId,
                organizationId
        ), pageable);
    }
}
