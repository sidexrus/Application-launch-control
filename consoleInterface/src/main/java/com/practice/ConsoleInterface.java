package com.practice;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


public class ConsoleInterface extends Thread{
    private AsynchronousSocketChannel client;
    private List<String> message;
    private String address;
    private String sequenceName;

    public ConsoleInterface(InetSocketAddress address, String sequenceName, List<String> message)
    {
        try {
            client = AsynchronousSocketChannel.open();

            Future<Void> result = client.connect(address);
            result.get();

            this.address = address.toString();
            this.sequenceName = sequenceName;

            this.message = message;
        } catch (IOException | ExecutionException | InterruptedException ignored) {
        }
    }

    @Override
    public void run()
    {
        send(message);

        message.clear();
        message.add(address + " " + sequenceName);

        message.addAll(receive());
    }

    public void send(List<String> message)
    {
        try {
            Future<Integer> writeValue = client.write(ByteBuffer.wrap(message.toString().getBytes()));
            System.out.println("Send to server: " + message.toString());
            writeValue.get();
        } catch (InterruptedException | ExecutionException ignored) {
        }
    }

    public List<String> receive()
    {
        ArrayList<String> allMsg = new ArrayList<>();
        String receivedMessage = "";
        try {
            ByteBuffer buffer = ByteBuffer.allocate(4096);
            Future<Integer> readValue =  client.read(buffer);
            readValue.get();

            receivedMessage = new String(buffer.array()).trim();
            receivedMessage = receivedMessage.substring(1, receivedMessage.length() - 1);
            allMsg.addAll(Arrays.asList(receivedMessage.split(", ")));

        } catch (InterruptedException | ExecutionException ignored) {
        }

        return allMsg;
    }

    public List<String> getMessage()
    {
        return message;
    }
}
