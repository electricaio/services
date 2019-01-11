package io.electrica.common;

/**
 * Possible types of environments.
 */
public enum EnvironmentType {

    Default(true),
    Production(false),
    Test(true),
    Stage(true),
    Development(true);

    private final boolean safe;

    EnvironmentType(boolean safe) {
        this.safe = safe;
    }

    public boolean isSafe() {
        return safe;
    }
}
