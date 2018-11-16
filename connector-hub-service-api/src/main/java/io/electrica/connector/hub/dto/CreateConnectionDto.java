package io.electrica.connector.hub.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.annotation.Nullable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Map;

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

    @Nullable
    private Map<String, String> properties;
}
