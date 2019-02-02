package io.electrica.metric.instance.session.service.dto;

import com.github.dozermapper.core.Mapper;
import io.electrica.common.context.Identity;
import io.electrica.common.context.IdentityContextHolder;
import io.electrica.metric.instance.session.model.InstanceSession;
import io.electrica.metric.instance.session.model.SessionState;
import io.electrica.metric.instance.session.service.InstanceSessionService;
import io.electrica.metric.instance.session.dto.UpsertInstanceSessionDto;
import io.electrica.metric.instance.session.dto.InstanceSessionDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class InstanceSessionDtoService {
    private final InstanceSessionService service;
    private final IdentityContextHolder identityContextHolder;
    private final Mapper mapper;

    public InstanceSessionDtoService(InstanceSessionService service, IdentityContextHolder identityContextHolder,
                                     Mapper mapper) {
        this.service = service;
        this.identityContextHolder = identityContextHolder;
        this.mapper = mapper;
    }

    public InstanceSessionDto start(UpsertInstanceSessionDto dto) {
        InstanceSession entity = toUpsertEntity(dto);
        InstanceSession instanceSession = service.start(entity);
        return toInstanceSessionDto(instanceSession);
    }

    public InstanceSessionDto expire(UUID uuid, ZonedDateTime lastSessionStarted) {
        return toInstanceSessionDto(service.expire(uuid, lastSessionStarted));
    }

    public InstanceSessionDto stop(UUID uuid, ZonedDateTime lastSessionStarted) {
        return toInstanceSessionDto(service.stop(uuid, lastSessionStarted));
    }

    public List<InstanceSessionDto> findByCurrentUserId(Pageable pageable) {
        long userId = identityContextHolder.getIdentity().getUserId();
        return toInstanceSessionDtoList(service.findByUserId(userId, pageable));
    }

    public List<InstanceSessionDto> findByAccessKeyId(long accessKeyId, Pageable pageable) {
        return toInstanceSessionDtoList(service.findByAccessKeyId(accessKeyId, pageable));
    }

    public List<InstanceSessionDto> findByUserId(long userId, Pageable pageable) {
        return toInstanceSessionDtoList(service.findByUserId(userId, pageable));
    }

    public List<InstanceSessionDto> findByOrganizationId(long organizationId, Pageable pageable) {
        return toInstanceSessionDtoList(service.findByOrganizationId(organizationId, pageable));
    }

    private List<InstanceSessionDto> toInstanceSessionDtoList(Page<InstanceSession> page) {
        return page.getContent().stream()
                .map(this::toInstanceSessionDto)
                .collect(Collectors.toList());
    }

    private InstanceSession toUpsertEntity(UpsertInstanceSessionDto dto) {
        InstanceSession result = mapper.map(dto, InstanceSession.class);
        Identity identity = identityContextHolder.getIdentity();
        result.setSessionState(SessionState.Running);
        result.setAccessKeyId(identity.getAccessKeyId());
        result.setUserId(identity.getUserId());
        result.setOrganizationId(identity.getOrganizationId());
        return result;
    }

    private InstanceSessionDto toInstanceSessionDto(InstanceSession entity) {
        return mapper.map(entity, InstanceSessionDto.class);
    }
}
