package io.electrica.common.security;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Enum of permissions with specified code to compact in token.
 * <br><br>
 * <b>Note: don't change code of permission due to tokens compatibility.</b>
 */
@Getter
public enum PermissionType {

    UpdateUser(1),
    DeleteUser(2),
    CreateUser(3),
    ReadUser(4),
    CreateOrg(5),
    ReadOrg(6),
    AddUserToOrg(7),
    UpdateOrg(8),
    ActivateOrg(9),
    CreateAccessKey(10),
    ReadAccessKey(11),
    CreateConnector(12),
    DeActivateConnection(13),
    ActivateConnection(14),
    AssociateAccessKeyToConnector(15),
    ReadActiveConnection(16),
    AddPermission(17),
    RemovePermission(18),
    DeleteAccessKey(19),
    CreateWebhook(20),
    ReadWebhook(21),
    DeleteWebhook(22),
    ReadConnector(23),
    UpdateConnection(24);

    private static final Map<Integer, PermissionType> ALL = Arrays.stream(PermissionType.values())
            .collect(Collectors.toMap(PermissionType::getCode, Function.identity()));

    private final int code;

    PermissionType(int code) {
        this.code = code;
    }

    public String getStringCode() {
        return String.valueOf(code);
    }

    public static PermissionType parse(String code) {
        PermissionType result = ALL.get(Integer.parseInt(code));
        if (result == null) {
            throw new IllegalArgumentException("Unknown PermissionType for code: " + code);
        }
        return result;
    }

}
