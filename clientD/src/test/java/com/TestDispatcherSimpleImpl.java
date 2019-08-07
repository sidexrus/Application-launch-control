package com;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestDispatcherSimpleImpl {

    @Test
    public void testCreateDispatcher()
    {
        DispatcherSimpleImpl dispatcher = new DispatcherSimpleImpl();

        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("command");
        arrayList.add("Hi");

        DataTransferPacket packet = new DataTransferPacket(arrayList);

        dispatcher.dispatchObject(packet);

        assertEquals("Hi", packet.attributes.get(0));
    }
}
