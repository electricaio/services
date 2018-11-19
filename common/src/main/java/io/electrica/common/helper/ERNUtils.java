package io.electrica.common.helper;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ERNUtils {

    private static final String PROTOCOL = "ern://";

    private ERNUtils() {
    }

    /**
     * Given the Connector's namespace, resource and version it,
     * builds up an ERN of ern://connector:<namespace>:<resource>:<version> format.
     * <p>
     * If resource is not provided, the ERN format follows this format:
     * ern://<namespace>:<version>
     * <p>
     * In case any of the provided strings consists of multiple words, they are split by underscore.
     * <p>
     * Examples:
     * <p>
     * - ern://hackerrank:applicants:1.0
     * - ern://mysql:5.6
     * - ern://greenhouse:1.0
     */
    public static String createERN(String namespace, @Nullable String resource, String version) {
        final String ernUrl = Stream
                .of(namespace, resource, version)
                .filter(Objects::nonNull)
                .map(ERNUtils::escapeInvalidChars)
                .collect(Collectors.joining(StringUtils.COLON))
                .toLowerCase();

        return PROTOCOL + ernUrl;
    }

    public static String createERN(String namespace, String version) {
        return createERN(namespace, null, version);
    }

    /**
     * Given the string, it escapes multiple
     * - whitespaces,
     * - dots,
     * - colons
     * with an underscore.
     */
    protected static String escapeInvalidChars(String s) {
        return s.replaceAll("[\\s.:]+", StringUtils.UNDERSCORE);
    }
}
