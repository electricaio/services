package io.electrica.metric.instance.session.dto;

import io.electrica.metric.instance.session.model.SessionState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.EnumSet;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InstanceSessionFilter implements Serializable {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String nameStartWith;
    private EnumSet<SessionState> sessionStates;
    private Long accessKeyId;
    private Long userId;
    private Long organizationId;
}
