package io.electrica.common.email;

import io.electrica.common.helper.ValueCache;

import java.io.ByteArrayInputStream;

public class ByteArrayInlineObject extends InlineObject<byte[]> {

    public ByteArrayInlineObject(String contentId, String contentType, ValueCache<byte[]> valueCache) {
        super(contentId, contentType, valueCache, ByteArrayInputStream::new);
    }
}
