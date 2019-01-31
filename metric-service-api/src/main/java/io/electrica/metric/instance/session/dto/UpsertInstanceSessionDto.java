package io.electrica.metric.instance.session.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpsertInstanceSessionDto {
    private UUID id;
    private String name;
    private ZonedDateTime startedClientTime;
}
