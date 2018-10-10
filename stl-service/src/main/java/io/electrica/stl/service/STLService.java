package io.electrica.stl.service;

import io.electrica.stl.rest.dto.CreateSTLDto;
import io.electrica.stl.rest.dto.ReadSTLDto;

import java.util.List;

public interface STLService {

    List<ReadSTLDto> list();

    ReadSTLDto create(CreateSTLDto request);
}
