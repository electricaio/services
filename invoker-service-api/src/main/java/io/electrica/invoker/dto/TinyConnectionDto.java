package io.electrica.invoker.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * Tiny Connection Dto model for Connection Dto.
 */
@Getter
@Setter
public class TinyConnectionDto {

    @NotNull
    public String name;

    @NotNull
    private Long id;
}
