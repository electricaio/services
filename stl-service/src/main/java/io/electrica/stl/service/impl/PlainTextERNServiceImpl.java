package io.electrica.stl.service.impl;

import io.electrica.stl.service.ERNService;
import org.springframework.stereotype.Service;

@Service
public class PlainTextERNServiceImpl implements ERNService {

    @Override
    public String assignERN(String name) {
        return name;
    }
}
