package com.practice;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestMain {

//    @Test
//    public void testMain()
//    {
//        try{
//            AsynchronousServerSocketChannel listener =
//                    AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(5000));
//
//            listener.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {
//
//                @Override
//                public void completed(AsynchronousSocketChannel channel, Void attributes)
//                {
//                    listener.accept(null, this);
//
//                    ByteBuffer byteBuffer = ByteBuffer.allocate(4096);
//
//                    try{
//                        channel.read(byteBuffer).get(20, TimeUnit.SECONDS);
//
//                        byteBuffer.flip();
//
//                        String line = new String(byteBuffer.array()).trim();
//
//                        assertEquals("[command, ]", line);
//
//                        byteBuffer.clear();
//
//                        channel.write(ByteBuffer.wrap("".getBytes()));
//
//                    } catch (InterruptedException | ExecutionException e){
//                        e.printStackTrace();
//                    } catch (TimeoutException e){
//                    }
//
//                    try{
//                        if(channel.isOpen()){
//                            channel.close();
//                            listener.close();
//                        }
//                    }catch (IOException e){
//                        e.printStackTrace();
//                    }
//                }
//
//                @Override
//                public void failed(Throwable exc, Void att){
//
//                }
//            });
//        } catch (IOException e){
//            e.printStackTrace();
//        }
//
//
//        Main.main(new String[]{""});
//    }
//
//    @Test
//    public void testMainWithZeroParameters()
//    {
//        try{
//            AsynchronousServerSocketChannel listener =
//                    AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(5000));
//
//            listener.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {
//
//                @Override
//                public void completed(AsynchronousSocketChannel channel, Void attributes)
//                {
//                    listener.accept(null, this);
//
//                    ByteBuffer byteBuffer = ByteBuffer.allocate(4096);
//
//                    try{
//                        channel.read(byteBuffer).get(20, TimeUnit.SECONDS);
//
//                        byteBuffer.flip();
//
//                        String line = new String(byteBuffer.array()).trim();
//
//                        assertEquals("[command, ]", line);
//
//                        byteBuffer.clear();
//
//                        channel.write(ByteBuffer.wrap("".getBytes()));
//
//                    } catch (InterruptedException | ExecutionException e){
//                        e.printStackTrace();
//                    } catch (TimeoutException e){
//                    }
//
//                    try{
//                        if(channel.isOpen()){
//                            channel.close();
//                            listener.close();
//                        }
//                    }catch (IOException e){
//                        e.printStackTrace();
//                    }
//                }
//
//                @Override
//                public void failed(Throwable exc, Void att){
//
//                }
//            });
//        } catch (IOException e){
//            e.printStackTrace();
//        }
//
//
//        Main.main(new String[]{});
//    }
}
