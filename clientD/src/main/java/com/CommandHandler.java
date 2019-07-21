package com;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CommandHandler
{
    private String PATH_WILDFLY_BIN;

    public CommandHandler(String pathWildflyBin)
    {
        PATH_WILDFLY_BIN = pathWildflyBin;
    }

    void processCommand(DataTransferPacket packet)
    {
        if(packet.commandText.equals("startServer")){
            startServer(packet);
        } else if(packet.commandText.equals("shutdownServer")){
            shutdownServer(packet);
        } else if(packet.commandText.equals("enableApp")){
            enableApplication(packet);
        } else if(packet.commandText.equals("disableApp")){
            disableApplication(packet);
        } else if(packet.commandText.equals("statusApp")){
            statusApplication(packet);
        } else if(packet.commandText.equals("deployApp")){
            deployApplication(packet);
        } else if(packet.commandText.equals("undeployApp")){
            undeployApplication(packet);
        } else if(packet.commandText.equals("enableSeq")){
            enableSequence(packet);
        } else if(packet.commandText.equals("disableSeq")){
            disableSequence(packet);
        } else if(packet.commandText.equals("Hi")){
            packet.attributes = Collections.singletonList("Hi");
        }

    }

    private void disableSequence(DataTransferPacket packet) {
        List<String> listApp = readFile(packet.attributes.get(0));

        try {
            ProcessBuilder builder = new ProcessBuilder(PATH_WILDFLY_BIN + "jboss-cli.sh","--connect");
            Process proc = builder.start();

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                    proc.getOutputStream()));

            for (String s : listApp) {
                String str = String.format("deployment disable %s\n", s);
                writer.write(str);
            }
            writer.flush();
            writer.close();

            packet.typePacket = "response";

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<String> readFile(String filePath)
    {
        List<String> list = new ArrayList<>();
        try {
            File configFile = new File(filePath);
            FileInputStream in = new FileInputStream(configFile);
            byte[] data = new byte[(int)configFile.length()];

            in.read(data);
            in.close();

            String configContent = new String(data, StandardCharsets.UTF_8);

            JSONObject rootObject = new JSONObject(configContent);

            JSONArray appArray = rootObject.getJSONArray("sequence");



            for(int i = 0; i < appArray.length(); ++i){
                list.add(appArray.getString(i));
            }

            System.out.println(list.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }

    private void enableSequence(DataTransferPacket packet) {
        List<String> listApp = readFile(packet.attributes.get(0));

        try {
            ProcessBuilder builder = new ProcessBuilder(PATH_WILDFLY_BIN + "jboss-cli.sh","--connect");
            Process proc = builder.start();

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                    proc.getOutputStream()));

            for (String s : listApp) {
                String str = String.format("deployment enable %s\n", s);
                writer.write(str);
            }
            writer.flush();
            writer.close();

            packet.typePacket = "response";

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void undeployApplication(DataTransferPacket packet) {
        try {
            ProcessBuilder builder = new ProcessBuilder(PATH_WILDFLY_BIN + "jboss-cli.sh","--connect");
            Process proc = builder.start();

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                    proc.getOutputStream()));

            BufferedReader input = new BufferedReader(new InputStreamReader(
                    proc.getInputStream()));

            String str = String.format("undeploy %s\n", packet.attributes.get(0));
            writer.write(str);
            writer.flush();
            writer.close();

            packet.typePacket = "response";

            String line;
            while((line = input.readLine()) != null){
                System.out.println(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deployApplication(DataTransferPacket packet) {
        try {
            ProcessBuilder builder = new ProcessBuilder(PATH_WILDFLY_BIN + "jboss-cli.sh","--connect");
            Process proc = builder.start();

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                    proc.getOutputStream()));

            BufferedReader input = new BufferedReader(new InputStreamReader(
                    proc.getInputStream()));

            String str = String.format("deploy %s\n", packet.attributes.get(0));
            writer.write(str);
            writer.flush();
            writer.close();

            packet.typePacket = "response";

            String line;
            while((line = input.readLine()) != null){
                System.out.println(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void disableApplication(DataTransferPacket packet) {
        try {
            ProcessBuilder builder = new ProcessBuilder(PATH_WILDFLY_BIN + "jboss-cli.sh","--connect");
            Process proc = builder.start();

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                    proc.getOutputStream()));

            String str = String.format("deployment disable %s\n", packet.attributes.get(0));
            writer.write(str);
            writer.flush();
            writer.close();

            packet.typePacket = "response";

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void enableApplication(DataTransferPacket packet) {
        try {
            ProcessBuilder builder = new ProcessBuilder(PATH_WILDFLY_BIN + "jboss-cli.sh","--connect");
            Process proc = builder.start();

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                    proc.getOutputStream()));

            String str = String.format("deployment enable %s\n", packet.attributes.get(0));
            writer.write(str);
            writer.flush();
            writer.close();

            packet.typePacket = "response";

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void shutdownServer(DataTransferPacket packet) {
        try {
            Process proc = Runtime.getRuntime().exec(PATH_WILDFLY_BIN + "jboss-cli.sh -c --command=:shutdown");

            BufferedReader input = new BufferedReader(new InputStreamReader(
                    proc.getInputStream()));

            packet.typePacket = "response";

            String line;
            while((line = input.readLine()) != null){
                if(line.contains("\"outcome\" => \"success\"")){
                    packet.attributes = Collections.singletonList("Server was shutdown");
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startServer(DataTransferPacket packet)
    {
        try {
            Process proc = Runtime.getRuntime().exec(PATH_WILDFLY_BIN + "standalone.sh");

            BufferedReader input = new BufferedReader(new InputStreamReader(
                    proc.getInputStream()));

            packet.typePacket = "response";

            String line;
            while((line = input.readLine()) != null){
                if(line.contains("listening on http://127.0.0.1:9990")){
                    packet.attributes = Collections.singletonList("Server was running");
                    break;
                } else if(line.contains("Address already in use /127.0.0.1:9990")){
                    packet.attributes = Collections.singletonList("Server is already running");
                    break;
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void statusApplication(DataTransferPacket packet)
    {
        try {
            ProcessBuilder builder = new ProcessBuilder(PATH_WILDFLY_BIN + "jboss-cli.sh","--connect");
            Process proc = builder.start();

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                    proc.getOutputStream()));

            BufferedReader input = new BufferedReader(new InputStreamReader(
                    proc.getInputStream()));

            String line;

            packet.typePacket = "response";

            writer.write("deploy -l\n");
            writer.flush();
            writer.close();


            while((line = input.readLine()) != null){
                if(line.contains(packet.attributes.get(0))){
                    String[] arr = line.split(" ");
                    if(arr[2].contains("true")){
                        packet.attributes = Arrays.asList("Application: " + packet.attributes.get(0), "Status: " + "enabled");
                    } else {
                        packet.attributes = Arrays.asList("Application: " + packet.attributes.get(0), "Status: " + "disabled");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
