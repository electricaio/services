package io.electrica.stl.rest.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class CreateSTLInstanceDto {

    @NotNull
    public Long stlId;

    @NotNull
    public Long accessKeyId;

    @Valid
    @NotNull
    private AuthorizationDto authorization;

    public CreateSTLInstanceDto(Long stlId, Long accessKeyId, AuthorizationDto authorization) {
        this.stlId = stlId;
        this.accessKeyId = accessKeyId;
        this.authorization = authorization;
    }
}
