package io.electrica.common.security;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Enum of roles with specified code to compact in token.
 * <br><br>
 * <b>Note: don't change code of permission due to tokens compatibility.</b>
 */
@Getter
public enum RoleType {

    OrgUser(1),
    OrgAdmin(2),
    SuperAdmin(3);

    private static final Map<Integer, RoleType> ALL = Arrays.stream(RoleType.values())
            .collect(Collectors.toMap(RoleType::getCode, Function.identity()));

    private final int code;

    RoleType(int code) {
        this.code = code;
    }

    public static RoleType parse(String code) {
        RoleType result = ALL.get(Integer.parseInt(code));
        if (result == null) {
            throw new IllegalArgumentException("Unknown RoleType for code: " + code);
        }
        return result;
    }

    public String getStringCode() {
        return String.valueOf(code);
    }
}
