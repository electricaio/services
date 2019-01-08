package io.electrica.connector.hub.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateConnectorDto {

    @NotNull
    private Long typeId;

    @NotNull
    private AuthorizationType authorizationType;

    @NotBlank
    @Size(max = 255)
    private String name;

    @Size(max = 255)
    private String resource;

    @NotBlank
    @Size(max = 255)
    private String version;

    @NotBlank
    @Size(max = 255)
    private String namespace;

    @NotNull
    @NotBlank
    @Size(max = 255)
    private String sourceUrl;

    @NotBlank
    @Size(max = 255)
    private String connectorUrl;

    @NotBlank
    @Size(max = 255)
    private String sdkUrl;

    @NotBlank
    @Size(max = 255)
    private String imageUrl;

    @Size(max = 255)
    private String description;

    private Map<String, String> properties;
}
