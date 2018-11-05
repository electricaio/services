package io.electrica.connector.hub.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateConnectionDto {

    @NotBlank
    @Size(max = 255)
    public String name;

    @Size(max = 255)
    private String tenantRefId;

    @NotNull
    public Long connectorId;

    @NotNull
    public Long accessKeyId;
}
