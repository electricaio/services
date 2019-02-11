package io.electrica.common.rest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    private final ApplicationInfo applicationInfo;

    public HealthController(
            @Value("${common.service.id}") String id,
            @Value("${common.service.version}") String version,
            @Value("${common.service.build-date}") String buildDate
    ) {
        applicationInfo = new ApplicationInfo(id, version, buildDate);
    }

    @GetMapping(PathConstants.HEALTH_PATH)
    public ResponseEntity<ApplicationInfo> health() {
        return ResponseEntity.ok(applicationInfo);
    }

    @Getter
    @AllArgsConstructor
    public static class ApplicationInfo {
        private final String id;
        private final String version;
        private final String buildDate;
    }
}
