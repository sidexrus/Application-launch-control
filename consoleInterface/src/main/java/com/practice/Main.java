package com.practice;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Main {

    public static void main(String[] args) {
        if(args.length == 0){
            System.out.println("Incorrect arguments");
            return;
        }

        System.out.println(Arrays.toString(args));

        List<String> msg = new ArrayList<>();
        msg.add("command");
        msg.addAll(Arrays.asList(args));

        try (AsynchronousSocketChannel client =
                     AsynchronousSocketChannel.open()) {
            Future<Void> result = client.connect(
                    new InetSocketAddress("127.0.0.1", 5000));
            result.get();

            Future<Integer> writeval = client.write(ByteBuffer.wrap(msg.toString().getBytes()));
            System.out.println("Writing to server: " + msg.toString());
            writeval.get();

        }
        catch (ExecutionException | IOException e) {
            e.printStackTrace();
        }
        catch (InterruptedException e) {
            System.out.println("Disconnected from the server.");
        }



    }
}
