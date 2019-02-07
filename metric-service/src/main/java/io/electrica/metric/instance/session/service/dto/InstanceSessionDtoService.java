package io.electrica.metric.instance.session.service.dto;

import com.github.dozermapper.core.Mapper;
import io.electrica.metric.instance.session.dto.InstanceSessionDescriptorDto;
import io.electrica.metric.instance.session.dto.InstanceSessionDto;
import io.electrica.metric.instance.session.dto.InstanceSessionFilter;
import io.electrica.metric.instance.session.model.InstanceSession;
import io.electrica.metric.instance.session.model.SessionState;
import io.electrica.metric.instance.session.service.InstanceSessionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class InstanceSessionDtoService {
    private final InstanceSessionService service;
    private final Mapper mapper;

    public InstanceSessionDtoService(InstanceSessionService service, Mapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    public InstanceSessionDto start(InstanceSessionDescriptorDto dto) {
        InstanceSession entity = toEntity(dto, SessionState.Running);
        InstanceSession instanceSession = service.changeState(entity);
        return toInstanceSessionDto(instanceSession);
    }

    public InstanceSessionDto expire(InstanceSessionDescriptorDto dto) {
        InstanceSession entity = toEntity(dto, SessionState.Expired);
        return toInstanceSessionDto(service.changeState(entity));
    }

    public InstanceSessionDto stop(InstanceSessionDescriptorDto dto) {
        InstanceSession entity = toEntity(dto, SessionState.Stopped);
        return toInstanceSessionDto(service.changeState(entity));
    }

    public Optional<InstanceSessionDto> findById(UUID id) {
        return service.findById(id)
                .map(this::toInstanceSessionDto);
    }

    public List<InstanceSessionDto> findByFilter(InstanceSessionFilter filter, Pageable pageable) {
        return toInstanceSessionDtoList(service.findByFilter(filter, pageable));
    }

    private List<InstanceSessionDto> toInstanceSessionDtoList(Page<InstanceSession> page) {
        return page.getContent().stream()
                .map(this::toInstanceSessionDto)
                .collect(Collectors.toList());
    }

    private InstanceSession toEntity(InstanceSessionDescriptorDto dto, SessionState sessionState) {
        InstanceSession result = mapper.map(dto, InstanceSession.class);
        result.setSessionState(sessionState);
        return result;
    }

    private InstanceSessionDto toInstanceSessionDto(InstanceSession entity) {
        return mapper.map(entity, InstanceSessionDto.class);
    }
}
