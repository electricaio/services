package io.electrica.metric.instance.session.service.dto;

import com.github.dozermapper.core.Mapper;
import io.electrica.common.context.IdentityContextHolder;
import io.electrica.metric.instance.session.dto.InstanceSessionDetailedDescriptorDto;
import io.electrica.metric.instance.session.dto.InstanceSessionFilter;
import io.electrica.metric.instance.session.model.InstanceSession;
import io.electrica.metric.instance.session.model.SessionState;
import io.electrica.metric.instance.session.service.InstanceSessionService;
import io.electrica.metric.instance.session.dto.InstanceSessionDescriptorDto;
import io.electrica.metric.instance.session.dto.InstanceSessionDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
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

    public InstanceSessionDto start(InstanceSessionDetailedDescriptorDto dto) {
        InstanceSession entity = toUpsertEntity(dto);
        InstanceSession instanceSession = service.start(entity);
        return toInstanceSessionDto(instanceSession);
    }

    public InstanceSessionDto expire(InstanceSessionDescriptorDto dto) {
        return toInstanceSessionDto(service.expire(dto.getId(), dto.getStartedClientTime()));
    }

    public InstanceSessionDto stop(InstanceSessionDescriptorDto dto) {
        return toInstanceSessionDto(service.stop(dto.getId(), dto.getStartedClientTime()));
    }

    public List<InstanceSessionDto> findByFilter(InstanceSessionFilter filter, Pageable pageable) {
        return toInstanceSessionDtoList(service.findByFilter(filter, pageable));
    }

    private List<InstanceSessionDto> toInstanceSessionDtoList(Page<InstanceSession> page) {
        return page.getContent().stream()
                .map(this::toInstanceSessionDto)
                .collect(Collectors.toList());
    }

    private InstanceSession toUpsertEntity(InstanceSessionDetailedDescriptorDto dto) {
        InstanceSession result = mapper.map(dto, InstanceSession.class);
        result.setSessionState(SessionState.Running);
        result.setAccessKeyId(dto.getAccessKeyId());
        result.setUserId(dto.getUserId());
        result.setOrganizationId(dto.getOrganizationId());
        return result;
    }

    private InstanceSessionDto toInstanceSessionDto(InstanceSession entity) {
        return mapper.map(entity, InstanceSessionDto.class);
    }
}
