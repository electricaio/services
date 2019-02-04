package io.electrica.metric.instance.session.dto;

import io.electrica.metric.instance.session.model.SessionState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InstanceSessionFilter {
    @NotNull
    private LocalDateTime startDate;
    @NotNull
    private LocalDateTime endDate;
    private String nameStartWith;
    private Set<SessionState> sessionStates;
    private Long accessKeyId;
    private Long userId;
    private Long organizationId;
}
