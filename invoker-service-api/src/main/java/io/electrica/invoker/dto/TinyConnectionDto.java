package io.electrica.invoker.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * Tiny Connection Dto model for Connection Dto.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TinyConnectionDto {

    @NotNull
    private Long id;

    @NotNull
    private String name;

    private Map<String, String> properties;

}
