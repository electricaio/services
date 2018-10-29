package io.electrica.connector.hub.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthorizationDto {

    @NotBlank
    @Max(255)
    private String name;

    @Max(255)
    private String tenantRefId;

    public Optional<String> getTenantRefIdOpt() {
        return Optional.ofNullable(tenantRefId);
    }
}
