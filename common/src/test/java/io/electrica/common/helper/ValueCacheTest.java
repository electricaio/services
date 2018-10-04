package io.electrica.common.helper;

import org.junit.Test;

import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class ValueCacheTest {

    @Test
    public void get() {
        String string = "test";

        Supplier<String> loader = mock(Supplier.class);
        when(loader.get()).thenReturn(string);

        ValueCache<String> cache = new ValueCache<>(loader);

        verify(loader, times(0)).get();

        assertEquals(cache.get(), string);
        verify(loader, times(1)).get();

        cache.get();
        verify(loader, times(1)).get();

    }
}
