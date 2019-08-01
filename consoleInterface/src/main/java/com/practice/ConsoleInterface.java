package com.practice;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


public class ConsoleInterface {
    private String ipAddress = "";
    private Integer port = -1;
    private AsynchronousSocketChannel client;

    public ConsoleInterface()
    {
        readConfigFile("config");

        if(!isInitialized()){
            throw new ExceptionInInitializerError("File not found or parameters initialized incorrect");
        }
    }


    public ConsoleInterface(String configFilePath)
    {
        readConfigFile(configFilePath);

        if(!isInitialized()){
            throw new ExceptionInInitializerError("File not found or parameters initialized incorrect");
        }
    }

    public void run()
    {
        try {
            client = AsynchronousSocketChannel.open();

            Future<Void> result = client.connect(
                    new InetSocketAddress(ipAddress, port));
            result.get();
        } catch (IOException | ExecutionException | InterruptedException ignored) {
        }
    }

    public String getIpAddress()
    {
        return  ipAddress;
    }

    public Integer getPort()
    {
        return port;
    }

    public boolean isInitialized()
    {
        if(ipAddress.equals("") || (port < 1 || port > (1 << 16))){
            return false;
        }

        return true;
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

    public String receive()
    {
        String receivedMessage = "";
        try {
            ByteBuffer buffer = ByteBuffer.allocate(4096);
            Future<Integer> readValue =  client.read(buffer);
            readValue.get();

            receivedMessage = new String(buffer.array()).trim();
        } catch (InterruptedException | ExecutionException ignored) {
        }

        return receivedMessage;
    }


    private void readConfigFile(String filePath)
    {
        try {
            File configFile = new File(filePath);
            FileInputStream in = new FileInputStream(configFile);
            byte[] data = new byte[(int)configFile.length()];

            in.read(data);
            in.close();

            String configContent = new String(data, StandardCharsets.UTF_8);

            JSONObject rootObject = new JSONObject(configContent);

            String ipAddress = rootObject.getString("IP address");
            Integer port = rootObject.getInt("port");

            this.ipAddress = ipAddress;
            this.port = port;

        } catch (IOException ignored){

        }
    }

}
