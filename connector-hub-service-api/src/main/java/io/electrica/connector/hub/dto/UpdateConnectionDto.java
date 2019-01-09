package io.electrica.connector.hub.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Map;

@Getter
@Setter
public class UpdateConnectionDto {

    @NotNull
    private Long id;

    @NotNull
    private Long revisionVersion;

    @NotBlank
    @Size(max = 255)
    public String name;

    private Map<String, String> properties;

}
