package io.electrica.common.jpa.history;

import io.electrica.common.context.Identity;
import io.electrica.common.context.IdentityContextHolder;
import org.hibernate.envers.RevisionListener;

/**
 * Listener to support user id audit in history.
 */
public class CustomRevisionListener implements RevisionListener {

    @Override
    public void newRevision(Object revisionEntity) {
        CustomRevisionEntity entity = (CustomRevisionEntity) revisionEntity;
        Identity identity = IdentityContextHolder.getInstance().getIdentity();
        // case when non-rest request (context filter doesn't work)
        long userId = identity != null ? identity.getUserId() : Identity.NOT_AUTHENTICATED_USER_ID;
        entity.setUserId(userId);
    }
}
