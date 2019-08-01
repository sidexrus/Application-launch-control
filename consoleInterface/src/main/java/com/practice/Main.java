package com.practice;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        if(args.length == 0){
            System.out.println("Incorrect arguments");
            return;
        }

        ConsoleInterface consoleInterface = new ConsoleInterface();
        consoleInterface.run();

        System.out.println(Arrays.toString(args));

        List<String> msg = new ArrayList<>();
        msg.add("command");
        msg.addAll(Arrays.asList(args));

        consoleInterface.send(msg);

        System.out.println(consoleInterface.receive());

    }


}
