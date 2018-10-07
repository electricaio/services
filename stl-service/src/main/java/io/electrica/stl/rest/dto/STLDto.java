package io.electrica.stl.rest.dto;

import com.github.dozermapper.core.Mapping;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class STLDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    @Mapping("type.name")
    private String type;

    private String name;

    private String version;

    private String namespace;

    private String ern;
}
