package com;

public class Main {
    public static void main(String[] args) {
        System.out.println("Client d");

        Dispatcher dispatcher = new DispatcherSimpleImpl();
        NIOSocketServer server = new NIOSocketServer(dispatcher);
        server.run();
        while(true){
            try
            {
                Thread.sleep(600000);
            }
            catch( Exception e )
            {
                e.printStackTrace();
            }
        }

    }
}
