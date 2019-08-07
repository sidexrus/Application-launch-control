package com;


import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestNIOSocketServer {

    @Test
    public void testRun()
    {
        Dispatcher dispatcher = new Dispatcher() {
            @Override
            public void dispatchObject(DataTransferPacket packet) {
                packet.attributes.add(packet.typePacket);
            }
        };

        String messageForSend = "[Hi]";

        NIOSocketServer server = new NIOSocketServer(5000, dispatcher);
        server.run();

        try {
            AsynchronousSocketChannel client = AsynchronousSocketChannel.open();

            Future<Void> res = client.connect(new InetSocketAddress("127.0.0.1", 5000));
            res.get();

            client.write(ByteBuffer.wrap(messageForSend.getBytes()));

            ByteBuffer buffer = ByteBuffer.allocate(4096);
            Future<Integer> readVal = client.read(buffer);
            readVal.get();

            assertEquals(messageForSend, new String(buffer.array()).trim());

            client.close();
        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
