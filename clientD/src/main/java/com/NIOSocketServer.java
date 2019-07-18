package com;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class NIOSocketServer
{
    private Dispatcher dispatcher;
    public NIOSocketServer(final Dispatcher dispatcher)
    {
        this.dispatcher = dispatcher;
        try{
            final AsynchronousServerSocketChannel listener =
                    AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(5000));

            listener.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {

                @Override
                public void completed(AsynchronousSocketChannel channel, Void attributes)
                {
                    listener.accept(null, this);

                    ByteBuffer byteBuffer = ByteBuffer.allocate(4096);
                    List<String> message = new ArrayList<>();

                    try{
                        int bytesRead = channel.read(byteBuffer).get(20, TimeUnit.SECONDS);

                        boolean running = true;
                        while(bytesRead != -1 && running){
                            System.out.println("bytes read:" + bytesRead);

                            if(byteBuffer.position() > 2){
                                byteBuffer.flip();

                                //byte[] lineBytes = new byte[bytesRead];
                                //byteBuffer.get(lineBytes, 0, bytesRead);
                                String line = new String(byteBuffer.array()).trim();
                                line =line.substring(1, line.length() - 1);
                                String[] arr = line.split(", ");
                                message = Arrays.asList(arr);

                                System.out.println("Message:" + line);

                                //channel.write(ByteBuffer.wrap(line.getBytes()));

                                byteBuffer.clear();

                                bytesRead = channel.read(byteBuffer).get(20, TimeUnit.SECONDS);
                            } else {
                                running = false;
                            }
                        }

                        DataTransferPacket packet = new DataTransferPacket(message);
                        dispatcher.dispatchObject(packet);
                        System.out.println(packet.toString());
                    } catch (InterruptedException | ExecutionException e){
                        e.printStackTrace();
                    } catch (TimeoutException e){
                        String messBye = "Good bye";
                        channel.write(ByteBuffer.wrap(messBye.getBytes()));
                        System.out.println("Connection timeout");
                    }

                    System.out.println("End of conversation");
                    try{
                        if(channel.isOpen()){
                            channel.close();
                        }
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void failed(Throwable exc, Void att){

                }
            });
        } catch (IOException e){
            e.printStackTrace();
        }

        /*try (AsynchronousServerSocketChannel server =
                     AsynchronousServerSocketChannel.open()) {
            server.bind(new InetSocketAddress(5000));
            Future<AsynchronousSocketChannel> acceptCon =
                    server.accept();
            AsynchronousSocketChannel client = acceptCon.get(40, TimeUnit.SECONDS);
            if ((client!= null) && (client.isOpen())) {
                ByteBuffer buffer = ByteBuffer.allocate(4096);
                buffer.clear();
                Future<Integer> readval = client.read(buffer);
                readval.get();
                System.out.println("Received from client: " + new String(buffer.array()).trim());
                buffer.flip();
                String str= "I'm fine. Thank you!";
                Future<Integer> writeVal = client.write(ByteBuffer.wrap(str.getBytes()));
                System.out.println("Writing back to client: " +str);
                writeVal.get();
                buffer.clear();
            }
            client.close();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }
}
