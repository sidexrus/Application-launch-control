package com.practice;



import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Main {


    public static void main(String[] args) {

        if(args.length == 0){
            System.out.println("Incorrect arguments");
            return;
        }

        ArrayList<Pair> servers = new ArrayList<>();

        readConfigFile(servers, "config");

        ArrayList<ConsoleInterface> interfaces = new ArrayList<>();

        System.out.println(Arrays.toString(args));

        for(Pair pair : servers){
            List<String> msg = new ArrayList<>();
            msg.add("command");

            if(args[0].equals("forwardSeq")){
                msg.add(args[0]);
                msg.addAll(readSequenceFile(args[1]));
            } else if(args[0].contains("Seq")){
                msg.add(args[0]);
                msg.add(pair.second);
            } else{
                msg.addAll(Arrays.asList(args));
            }

            ConsoleInterface consoleInterface = new ConsoleInterface(pair.first, pair.second, msg);

            interfaces.add(consoleInterface);

            consoleInterface.run();
        }

        for(ConsoleInterface consoleInterface: interfaces){
            try {
                consoleInterface.join();
                printMsg(consoleInterface.getMessage());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }



//
//        List<String> msg = new ArrayList<>();
//        msg.add("command");
//
//        if(args[0].equals("forwardSeq")){
//            msg.add(args[0]);
//            msg.addAll(consoleInterface.readSequenceFile(args[1]));
//        } else{
//            msg.addAll(Arrays.asList(args));
//        }
//
//
//        consoleInterface.send(msg);
//
//        System.out.println(consoleInterface.receive());

    }

    private static void readConfigFile(ArrayList<Pair> servers, String filePath)
    {
        try {
            File configFile = new File(filePath);
            FileInputStream in = new FileInputStream(configFile);
            byte[] data = new byte[(int)configFile.length()];

            in.read(data);
            in.close();

            String configContent = new String(data, StandardCharsets.UTF_8);

            JSONObject rootObject = new JSONObject(configContent);
            JSONArray serversArr = rootObject.getJSONArray("servers");

            for(int i = 0; i < serversArr.length(); ++i){
                JSONObject el = serversArr.getJSONObject(i);

                String ipAddress = el.getString("IP_address");
                int port = el.getInt("port");
                String seqName = el.getString("sequenceName");

                servers.add(new Pair(new InetSocketAddress(ipAddress, port), seqName));
            }

        } catch (IOException ignored){

        }
    }

    private static ArrayList<String> readSequenceFile(String filePath)
    {
        ArrayList<String> appList = new ArrayList<>();

        String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
        System.out.println(fileName);

        appList.add(fileName);

        try {
            File configFile = new File(filePath);
            FileInputStream in = new FileInputStream(configFile);
            byte[] data = new byte[(int)configFile.length()];

            in.read(data);
            in.close();

            String configContent = new String(data, StandardCharsets.UTF_8);

            JSONObject rootObject = new JSONObject(configContent);

            JSONArray apps = rootObject.getJSONArray("sequence");

            for(int i = 0; i < apps.length(); ++i){
                appList.add(apps.getString(i));
            }

        } catch (IOException ignored){

        }

        return appList;
    }

    public static void printMsg(List<String> msg)
    {
        for(String line: msg){
            System.out.println(line);
        }
        System.out.println("==============================");
    }
}
