package com;

public class Main {
    public static void main(String[] args) {
        System.out.println("Client d");
        Dispatcher dispatcher = new Dispatcher();
        NIOSocketServer server = new NIOSocketServer(dispatcher);
        try
        {
            Thread.sleep( 60000 );
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
    }
}
