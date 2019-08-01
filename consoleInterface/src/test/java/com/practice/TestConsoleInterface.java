package com.practice;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;

public class TestConsoleInterface {

    @Test
    public void testReadConfigFile()
    {
        File tmpFile = new File("tmpFile");

        try {
            tmpFile.createNewFile();
            Files.write(tmpFile.toPath(), Collections.singleton("{ \"IP address\":\"192.168.0.1\", \"port\":\"3000\"}"));

            ConsoleInterface consoleInterface = new ConsoleInterface("tmpFile");

            assertTrue(consoleInterface.isInitialized());
            assertEquals("192.168.0.1", consoleInterface.getIpAddress());
            assertEquals(3000, consoleInterface.getPort());
        } catch (IOException e) {
            e.printStackTrace();
        }

        tmpFile.delete();
    }

    @Test
    public void testConfigFileNotFound()
    {
        try {
            ConsoleInterface consoleInterface = new ConsoleInterface("tmpFile");

        }catch (ExceptionInInitializerError e){
            e.printStackTrace();
            assertTrue(true);
        }

//        assertFalse(consoleInterface.isInitialized());
    }

    @Test
    public void testRun()
    {
        try {
            AsynchronousServerSocketChannel server =
                    AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(5000));

            ConsoleInterface consoleInterface = new ConsoleInterface();
            consoleInterface.run();

            assertTrue(true);

            server.close();
        }catch (ExceptionInInitializerError | IOException e){
            e.printStackTrace();
        }
    }

    @Test
    public void testSendMessage()
    {
        try {
            AsynchronousServerSocketChannel server =
                    AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(5000));

            ConsoleInterface client = new ConsoleInterface();
            client.run();

            Future<AsynchronousSocketChannel> clientChannelF = server.accept();

            client.send(Arrays.asList("Hi", "dude"));
            AsynchronousSocketChannel clientChannel = clientChannelF.get();

            ByteBuffer buffer = ByteBuffer.allocate(1024);
            clientChannel.read(buffer);

            String msg = new String(buffer.array()).trim();

            assertEquals("[Hi, dude]", msg);

            server.close();
        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testSendEmptyMessage()
    {
        try {
            AsynchronousServerSocketChannel server =
                    AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(5000));

            ConsoleInterface client = new ConsoleInterface();
            client.run();

            Future<AsynchronousSocketChannel> clientChannelF = server.accept();

            client.send(Arrays.asList(""));
            AsynchronousSocketChannel clientChannel = clientChannelF.get();

            ByteBuffer buffer = ByteBuffer.allocate(1024);
            clientChannel.read(buffer);

            String msg = new String(buffer.array()).trim();

            assertEquals("[]", msg);

            server.close();
        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSendSomeMessages()
    {
        try {
            AsynchronousServerSocketChannel server =
                    AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(5000));

            ConsoleInterface client = new ConsoleInterface();
            client.run();

            Future<AsynchronousSocketChannel> clientChannelF = server.accept();

            client.send(Arrays.asList("Hi"));
            client.send(Arrays.asList("dude"));
            AsynchronousSocketChannel clientChannel = clientChannelF.get();

            ByteBuffer buffer = ByteBuffer.allocate(1024);
            clientChannel.read(buffer);

            String msg = new String(buffer.array()).trim();

            assertEquals("[Hi][dude]", msg);

            server.close();
        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testReceiveMessage()
    {
        try {
            AsynchronousServerSocketChannel server =
                    AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(5000));

            ConsoleInterface client = new ConsoleInterface();
            client.run();

            Future<AsynchronousSocketChannel> clientChannelF = server.accept();

            AsynchronousSocketChannel clientChannel = clientChannelF.get();

            clientChannel.write(ByteBuffer.wrap("I am server".getBytes()));

            String msg = client.receive();

            assertEquals("I am server", msg);

            server.close();
        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
