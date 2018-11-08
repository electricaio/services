package io.electrica.connector.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class ErrorDto {

    @NotNull
    private String message;

    @NotNull
    private String code;

    private String payload;

}
