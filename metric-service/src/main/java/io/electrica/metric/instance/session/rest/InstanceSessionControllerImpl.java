package io.electrica.metric.instance.session.rest;

import io.electrica.metric.instance.session.dto.InstanceSessionFilter;
import io.electrica.metric.instance.session.model.SessionState;
import io.electrica.metric.instance.session.service.dto.InstanceSessionDtoService;
import io.electrica.metric.instance.session.dto.InstanceSessionDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@RestController
public class InstanceSessionControllerImpl implements InstanceSessionController {
    private final InstanceSessionDtoService instanceSessionDtoService;

    @Inject
    public InstanceSessionControllerImpl(InstanceSessionDtoService instanceSessionDtoService) {
        this.instanceSessionDtoService = instanceSessionDtoService;
    }

    @PreAuthorize("#common.hasPermission('ReadInstanceSession') " +
            " AND ( #common.isSuperAdmin() OR #common.isUser(#userId) )")
    @Override
    public List<InstanceSessionDto> getInstanceSessions(
            @PageableDefault Pageable pageable,
            @RequestParam(value = "startDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(value = "endDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(value = "nameStartWith", required = false) String nameStartWith,
            @RequestParam(value = "state[]", required = false) Set<SessionState> sessionStates,
            @RequestParam(value = "accessKeyId", required = false) Long accessKeyId,
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "organizationId", required = false) Long organizationId
    ) {
        return instanceSessionDtoService.findByFilter(new InstanceSessionFilter(
                startDate,
                endDate,
                nameStartWith,
                sessionStates == null ? Collections.emptySet() : sessionStates,
                accessKeyId,
                userId,
                organizationId
        ), pageable);
    }
}
