package io.electrica.websocket.context;

import io.electrica.common.context.Identity;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class SdkInstanceContext {

    private final UUID instanceId;
    private final Identity identity;

}
