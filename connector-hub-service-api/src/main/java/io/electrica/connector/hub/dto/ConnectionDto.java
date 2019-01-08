package io.electrica.connector.hub.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ConnectionDto extends CreateConnectionDto {

    @NotNull
    private Long id;

    @NotNull
    private Long revisionVersion;

    private Long authorizationId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
