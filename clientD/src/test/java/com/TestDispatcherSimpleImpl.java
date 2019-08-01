package com;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestDispatcherSimpleImpl {

    @Test
    public void testCreateDispatcher()
    {
        DispatcherSimpleImpl dispatcher = new DispatcherSimpleImpl();

        DataTransferPacket packet = new DataTransferPacket(Arrays.asList("command", "Hi"));

        dispatcher.dispatchObject(packet);

        assertEquals("Hi", packet.attributes.get(0));
    }
}
