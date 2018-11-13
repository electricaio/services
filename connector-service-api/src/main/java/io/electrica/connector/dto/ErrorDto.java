package io.electrica.connector.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorDto {

    @NotNull
    private String code;

    @NotNull
    private String message;

    @NotNull
    private String stackTrace;

    private List<String> payload;

}
