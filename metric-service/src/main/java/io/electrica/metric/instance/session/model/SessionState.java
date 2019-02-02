package io.electrica.metric.instance.session.model;

import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Set;

public enum SessionState {
    /**
     * Instance created and there is open web socket session.
     */
    Running,
    /**
     * Instance web socket session closed improperly with different from 1000 code and there is no new session.
     */
    Expired,
    /**
     * Web socket session closed normally with 1000 code.
     */
    Stopped;

    private static final EnumMap<SessionState, Set<SessionState>> STATE_TRANSITIONS =
            new EnumMap<SessionState, Set<SessionState>>(SessionState.class) {{
        put(Running, EnumSet.of(Expired, Stopped));
        put(Expired, EnumSet.of(Running, Stopped));
        put(Stopped, EnumSet.noneOf(SessionState.class));
    }};

    public Set<SessionState> next() {
        return Collections.unmodifiableSet(STATE_TRANSITIONS.get(this));
    }

    public boolean isTransitionAllowed(SessionState sessionState) {
        return STATE_TRANSITIONS.get(this).contains(sessionState);
    }
}
