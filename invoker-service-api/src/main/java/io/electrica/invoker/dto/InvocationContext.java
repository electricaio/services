package io.electrica.invoker.dto;

import com.fasterxml.jackson.annotation.JsonRawValue;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Setter
public class InvocationContext {

    @NotNull
    private UUID instanceId;

    @NotNull
    private Long connectionId;

    @NotNull
    @JsonRawValue
    private String parameters;

    @NotNull
    @JsonRawValue
    private String data;

}
