package io.electrica.stl.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateSTLInstanceDto {

    @NotNull
    public Long stlId;

    @NotNull
    public Long accessKeyId;
}
