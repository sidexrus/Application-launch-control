package com;

public class Main {
    public static void main(String[] args) {
        System.out.println("Client d");
        Dispatcher dispatcher = new Dispatcher();
        NIOSocketServer server = new NIOSocketServer(dispatcher);
        while(true){
            try
            {
                Thread.sleep(600000  );
            }
            catch( Exception e )
            {
                e.printStackTrace();
            }
        }

    }
}
