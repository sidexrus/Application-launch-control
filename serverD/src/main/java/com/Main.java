package com;

public class Main {
    public static void main(String[] args) {
        System.out.println("Server d");

        int port = 5010;

        if(args.length != 0){
            port = Integer.parseInt(args[0]);
        }

        Dispatcher dispatcher = new DispatcherSimpleImpl();
        NIOSocketServer server = new NIOSocketServer(port, dispatcher);
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
