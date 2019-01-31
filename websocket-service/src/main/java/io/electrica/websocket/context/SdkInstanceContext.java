package io.electrica.websocket.context;

import io.electrica.common.context.Identity;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class SdkInstanceContext {

    private final UUID instanceId;
    private final String instanceName;
    private final Identity identity;
    private final ZonedDateTime instanceStartClientTime;

}
