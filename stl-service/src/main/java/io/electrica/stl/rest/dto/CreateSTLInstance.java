package io.electrica.stl.rest.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class CreateSTLInstance implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    public Long stlId;

    @NotNull
    public Long accessKeyId;

    @Valid
    @NotNull
    private AuthorizationData authorizationData;
}
