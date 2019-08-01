package com;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class CommandHandler
{
    private String PATH_WILDFLY_BIN;
    private Process cliProcess = null;
    private BufferedWriter cliWriter = null;
    private BufferedReader cliReader = null;


    public CommandHandler(String pathWildflyBin)
    {
        PATH_WILDFLY_BIN = pathWildflyBin;
    }

    public void processCommand(DataTransferPacket packet)
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
        } else if(packet.commandText.equals("deploySeq")){
            deployApplicationSequence(packet);
        } else if(packet.commandText.equals("undeploySeq")){
            undeployApplicationSequence(packet);
        } else if(packet.commandText.equals("enableSeq")){
            enableSequence(packet);
        } else if(packet.commandText.equals("disableSeq")){
            disableSequence(packet);
        } else if(packet.commandText.equals("Hi")){
            packet.attributes = Collections.singletonList("Hi");
        }

    }

    private boolean connectToCLI(DataTransferPacket packet)
    {
        ProcessBuilder builder = new ProcessBuilder(PATH_WILDFLY_BIN + "jboss-cli.sh","--connect");
        try {
            cliProcess = builder.start();
            cliReader = new BufferedReader(new InputStreamReader(cliProcess.getInputStream()));

            CharBuffer buffer = CharBuffer.allocate(4096);
            int readByte = cliReader.read(buffer);
            while((readByte) > 0){
                String str = buffer.toString().trim();
                if(str.contains("Failed to connect to the controller")){
                    packet.attributes = Collections.singletonList("Server is down");
                    return false;
                } else if(str.equals("")){
                    break;
                }
                readByte = cliReader.read(buffer);
            }

            cliProcess = builder.start();
            cliReader = new BufferedReader(new InputStreamReader(cliProcess.getInputStream()));
            cliWriter = new BufferedWriter(new OutputStreamWriter(cliProcess.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
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

    private void undeployApplicationSequence(DataTransferPacket packet)
    {
        List<String> listApp = readFile(packet.attributes.get(0));

        if(cliProcess == null && !connectToCLI(packet)){
            return;
        }

        try {
            for(String s :listApp){
                String appName = s.substring(s.lastIndexOf("/") + 1);
                String str = String.format("undeploy %s\n", appName);
                cliWriter.write(str);
                cliWriter.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deployApplicationSequence(DataTransferPacket packet)
    {
        List<String> listApp = readFile(packet.attributes.get(0));

        if(cliProcess == null && !connectToCLI(packet)){
            return;
        }

        try {
            for(String s :listApp){
                String str = String.format("deploy %s --force\n", s);
                cliWriter.write(str);
                cliWriter.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void disableSequence(DataTransferPacket packet) {
        List<String> listApp = readFile(packet.attributes.get(0));

        try {
            if(cliProcess == null && !connectToCLI(packet)){
                return;
            }

            for (String s : listApp) {
                String appName = s.substring(s.lastIndexOf("/") + 1);
                String str = String.format("deployment disable %s\n", appName);
                cliWriter.write(str);
            }
            cliWriter.flush();

            packet.typePacket = "response";

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void enableSequence(DataTransferPacket packet) {
        List<String> listApp = readFile(packet.attributes.get(0));

        try {
            if(cliProcess == null && !connectToCLI(packet)){
                return;
            }

            for (String s : listApp) {
                String appName = s.substring(s.lastIndexOf("/") + 1);
                String str = String.format("deployment enable %s\n", appName);
                cliWriter.write(str);
            }
            cliWriter.flush();

            packet.typePacket = "response";

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void undeployApplication(DataTransferPacket packet) {
        try {
            if(cliProcess == null && !connectToCLI(packet)){
                return;
            }

            String str = String.format("undeploy %s\n", packet.attributes.get(0));
            cliWriter.write(str);
            cliWriter.flush();

            packet.typePacket = "response";

            String line;
            line = cliReader.readLine();

            System.out.println(line);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deployApplication(DataTransferPacket packet) {
        try {
            if(cliProcess == null && !connectToCLI(packet)){
                return;
            }

            String str = String.format("deploy %s --force\n", packet.attributes.get(0));
            cliWriter.write(str);
            cliWriter.flush();

            packet.typePacket = "response";

            cliReader.readLine();

            packet.attributes = Collections.singletonList("Application was deployment");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void disableApplication(DataTransferPacket packet) {
        try {
            if(cliProcess == null && !connectToCLI(packet)){
                return;
            }

            String str = String.format("deployment disable %s\n", packet.attributes.get(0));
            cliWriter.write(str);
            cliWriter.flush();

            packet.typePacket = "response";

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void enableApplication(DataTransferPacket packet) {
        try {
            if(cliProcess == null && !connectToCLI(packet)){
                return;
            }

            String str = String.format("deployment enable %s\n", packet.attributes.get(0));
            cliWriter.write(str);
            cliWriter.flush();

            System.out.println(cliReader.readLine());

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


            String line;
            while((line = input.readLine()) != null){
                if(line.contains("\"outcome\" => \"success\"")){
                    packet.attributes = Collections.singletonList("Server was shutdown");
                    break;
                } else if(line.contains("Failed to connect to the controller")){
                    packet.attributes = Collections.singletonList("Server is already shutdown");
                }
            }


            packet.typePacket = "response";
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

            packet.typePacket = "response";

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void statusApplication(DataTransferPacket packet)
    {
        try {
            if(cliProcess == null && !connectToCLI(packet)){
                return;
            }

            String line;

            packet.typePacket = "response";

            cliWriter.write("deploy -l");
            cliWriter.newLine();
            cliWriter.flush();

            cliWriter.write("command");
            cliWriter.newLine();
            cliWriter.flush();

            while((line = cliReader.readLine()) != null && !line.contains("Command is missing.")){
                System.out.println(line);
                line = line.trim().replaceAll(" +", " ");
                if(line.contains(packet.attributes.get(0))){
                    String[] arr = line.split(" ");
                    if(arr[2].contains("true")){
                        packet.attributes = Arrays.asList("Application: " + packet.attributes.get(0), "Status: " + "enabled");
                    } else if(arr[2].contains("false")) {
                        packet.attributes = Arrays.asList("Application: " + packet.attributes.get(0), "Status: " + "disabled");
                    }
                    //break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
