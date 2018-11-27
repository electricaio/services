package io.electrica.connector.hub.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

    @NotNull
    @NotBlank
    @Size(max = 255)
    private String sourceUrl;

    @NotNull
    @NotBlank
    @Size(max = 255)
    private String connectorUrl;

    @NotNull
    @NotBlank
    @Size(max = 255)
    private String sdkUrl;

    @NotNull
    @NotBlank
    @Size(max = 255)
    private String imageUrl;


    @Size(max = 255)
    private String description;

    private Map<String, String> properties;
}
