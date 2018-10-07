package io.electrica.stl.service;

import io.electrica.stl.rest.dto.STLDto;

import java.util.List;

public interface STLService {

    List<STLDto> findAll();
}
