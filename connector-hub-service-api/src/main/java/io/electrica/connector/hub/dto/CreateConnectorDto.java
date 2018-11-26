package io.electrica.connector.hub.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Map;

@Getter
@Setter
public class CreateConnectorDto {

    @NotNull
    private Long typeId;

    @NotNull
    private AuthorizationType authorizationType;

    @NotNull
    @NotBlank
    @Size(max = 255)
    private String name;

    @Size(max = 255)
    private String resource;

    @NotNull
    @NotBlank
    @Size(max = 255)
    private String version;

    @NotNull
    @NotBlank
    @Size(max = 255)
    private String namespace;

    @Size(max = 255)
    private String sourceURL;

    @Size(max = 255)
    private String connectorURL;

    @Size(max = 255)
    private String sdkURL;

    @Size(max = 255)
    private String imageURL;

    @Size(max = 255)
    private String description;

    private Map<String, String> properties;
}
