package com;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class NIOSocketServer
{
    private Dispatcher dispatcher;
    public NIOSocketServer(final Dispatcher dispatcher)
    {
        this.dispatcher = dispatcher;
    }

    public void run()
    {
        try{
            final AsynchronousServerSocketChannel listener =
                    AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(5000));

            listener.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {

                @Override
                public void completed(AsynchronousSocketChannel channel, Void attributes)
                {
                    listener.accept(null, this);

                    DataTransferPacket packet = receive(channel);
                    dispatcher.dispatchObject(packet);
                    send(channel, packet);

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
    }

    private DataTransferPacket receive(AsynchronousSocketChannel channel)
    {
        DataTransferPacket packet = null;
        ByteBuffer byteBuffer = ByteBuffer.allocate(4096);
        List<String> message = new ArrayList<>();

        try{
            int bytesRead = channel.read(byteBuffer).get(20, TimeUnit.SECONDS);

            boolean running = true;
            while(bytesRead != -1 && running){
                System.out.println("bytes read:" + bytesRead);

                if(byteBuffer.position() > 2){
                    byteBuffer.flip();

                    String line = new String(byteBuffer.array()).trim();

                    line =line.substring(1, line.length() - 1);
                    String[] arr = line.split(", ");
                    message.addAll(Arrays.asList(arr));

                    System.out.println("Message:" + line);

                    byteBuffer.clear();

                    bytesRead = channel.read(byteBuffer).get(20, TimeUnit.SECONDS);
                } else {
                    running = false;
                }
            }
        } catch (InterruptedException | ExecutionException e){
            e.printStackTrace();
        } catch (TimeoutException e){
            System.out.println("Connection timeout");
        }

        packet = new DataTransferPacket(message);
        System.out.println(packet.toString());

        return packet;
    }

    private void send(AsynchronousSocketChannel channel, DataTransferPacket packet)
    {
        String msg = String.valueOf(packet.attributes);
        channel.write(ByteBuffer.wrap(msg.getBytes()));
    }
}
