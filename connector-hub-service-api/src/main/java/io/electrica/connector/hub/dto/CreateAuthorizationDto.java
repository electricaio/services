package io.electrica.connector.hub.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Size;

@Getter
@Setter
public abstract class CreateAuthorizationDto {

    @Size(max = 255)
    private String tenantRefId;
}
