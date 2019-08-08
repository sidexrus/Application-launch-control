package com;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class CommandHandler
{
    private String PATH_WILDFLY_BIN;
    private String DB_SERVER_ADDRESS;
    private int DB_SERVER_PORT;

    private Process cliProcess = null;
    private BufferedWriter cliWriter = null;
    private BufferedReader cliReader = null;


    public CommandHandler(String pathWildflyBin, String dbServerAddress, int dbServerPort)
    {
        PATH_WILDFLY_BIN = pathWildflyBin;
        DB_SERVER_ADDRESS = dbServerAddress;
        DB_SERVER_PORT = dbServerPort;
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
        } else if(packet.commandText.equals("statusSeq")){
            statusApplicationSequence(packet);
        } else if(packet.commandText.equals("checkSeq")){
            checkApplicationSequence(packet);
        } else if(packet.commandText.equals("forwardSeq")){
            forwardApplicationSequence(packet);
        } else if(packet.commandText.equals("Hi")){
            packet.attributes.clear();
            packet.attributes.add("Hi");
        } else{
            packet.attributes.clear();
            packet.attributes.add("Incorrect command");
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
                    packet.attributes.clear();
                    packet.attributes.add("Server is down");
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

    private void forwardApplicationSequence(DataTransferPacket packet)
    {
        try {
            File sequenceFile = new File(packet.attributes.get(0));
            FileOutputStream fout = new FileOutputStream(sequenceFile);
            fout.write("{ \"sequence\" : [".getBytes());

            Iterator<String> iterator = packet.attributes.iterator();
            iterator.next();
            while(iterator.hasNext()){
                String str = String.format("\"%s\"", iterator.next());
                fout.write(str.getBytes());
                if(iterator.hasNext()){
                    fout.write(", ".getBytes());
                }
            }

            fout.write("]}".getBytes());

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void checkApplicationSequence(DataTransferPacket packet)
    {
        List<String> appList = readFile(packet.attributes.get(0));

        if(appList.size() == 0){
            return;
        }

        ArrayList<String> message = new ArrayList<>();
        message.add("dbRequest");
        message.add("checkSeq");
        message.addAll(appList);

        try {
            AsynchronousSocketChannel client = AsynchronousSocketChannel.open();
            Future<Void> res = client.connect(new InetSocketAddress(DB_SERVER_ADDRESS, DB_SERVER_PORT));
            res.get();

            Future<Integer> writeValue = client.write(ByteBuffer.wrap(message.toString().getBytes()));
            System.out.println("Send to server: " + message.toString());
            writeValue.get();

            String receivedMessage = "";

            ByteBuffer buffer = ByteBuffer.allocate(4096);
            Future<Integer> readValue =  client.read(buffer);
            readValue.get();

            receivedMessage = new String(buffer.array()).trim();
            System.out.println("app seq: " + receivedMessage);
            receivedMessage = receivedMessage.substring(1, receivedMessage.length() - 1);
            ArrayList<String> apps = new ArrayList<String>();
            apps.addAll(Arrays.asList(receivedMessage.split(", ")));

            File sequenceFile = new File(packet.attributes.get(1));
            FileOutputStream fout = new FileOutputStream(sequenceFile);
            fout.write("{ \"sequence\" : [".getBytes());

            Iterator<String> iterator = apps.iterator();
            while(iterator.hasNext()){
                String str = String.format("\"exampleWar/%s.war\"", iterator.next());
                fout.write(str.getBytes());
                if(iterator.hasNext()){
                    fout.write(", ".getBytes());
                }
            }

            fout.write("]}".getBytes());

        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

    }

    private void undeployApplicationSequence(DataTransferPacket packet)
    {
        List<String> appList = readFile(packet.attributes.get(0));

        if(appList.size() == 0 || (cliProcess == null && !connectToCLI(packet))){
            return;
        }

        packet.typePacket = "response";
        packet.attributes.clear();
        packet.attributes.add("Applications have been undeployed");

        try {
            for(String app :appList){
                String appName = app.substring(app.lastIndexOf("/") + 1);
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
        List<String> appList = readFile(packet.attributes.get(0));

        if(appList.size() == 0 || (cliProcess == null && !connectToCLI(packet))){
            return;
        }

        packet.typePacket = "response";

        packet.attributes.clear();
        packet.attributes.add("");

        int undeployedAppCount = 0;

        try {
            for(String app :appList){
                String str = String.format("deploy %s\n", app);
                cliWriter.write(str);
                cliWriter.flush();

                cliWriter.write("command\n");
                cliWriter.flush();

                String line;
                while((line = cliReader.readLine()) != null && !line.contains("Command is missing.")){
                    System.out.println(line);
                    if(line.contains("doesn't exist")){
                        packet.attributes.add(String.format("Application: %s doesn't exist", app));
                        ++undeployedAppCount;
                    } else if(line.contains("Failed to mount")){
                        packet.attributes.add(String.format("Application: %s incorrect", app));
                        ++undeployedAppCount;
                    }
                }

            }

            packet.attributes.set(0,
                                  String.format("%d/%d Applications have been deployed",
                                                appList.size() - undeployedAppCount, appList.size()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void disableSequence(DataTransferPacket packet) {
        List<String> appList = readFile(packet.attributes.get(0));

        try {
            if(appList.size() == 0 || (cliProcess == null && !connectToCLI(packet))){
                return;
            }

            packet.typePacket = "response";
            packet.attributes.clear();
            packet.attributes.add("");

            int undisabledAppCount = 0;

            for (String app : appList) {
                String appName = app.substring(app.lastIndexOf("/") + 1);
                String str = String.format("deployment disable %s\n", appName);
                cliWriter.write(str);
                cliWriter.flush();

                cliWriter.write("command\n");
                cliWriter.flush();

                String line;
                while((line = cliReader.readLine()) != null && !line.contains("Command is missing.")){
                    System.out.println(line);
                    if(line.contains("not found")){
                        packet.attributes.add(String.format("Application: %s wasn't deployed", appName));
                        ++undisabledAppCount;
                    }
                }
            }

            packet.attributes.set(0,
                    String.format("%d/%d Applications have been disabled",
                            appList.size() - undisabledAppCount, appList.size()));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void enableSequence(DataTransferPacket packet) {
        List<String> appList = readFile(packet.attributes.get(0));

        try {
            if(appList.size() == 0 || (cliProcess == null && !connectToCLI(packet))){
                return;
            }

            packet.typePacket = "response";
            packet.attributes.clear();
            packet.attributes.add("");

            int unenabledAppCount = 0;

            for (String app : appList) {
                String appName = app.substring(app.lastIndexOf("/") + 1);
                String str = String.format("deployment enable %s\n", appName);
                cliWriter.write(str);
                cliWriter.flush();

                cliWriter.write("command\n");
                cliWriter.flush();

                String line;
                while((line = cliReader.readLine()) != null && !line.contains("Command is missing.")){
                    System.out.println(line);
                    if(line.contains("not found")){
                        packet.attributes.add(String.format("Application: %s wasn't deployed", appName));
                        ++unenabledAppCount;
                    }
                }
            }

            packet.attributes.set(0,
                    String.format("%d/%d Applications have been enabled",
                            appList.size() - unenabledAppCount, appList.size()));

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
            packet.attributes.clear();
            packet.attributes.add("Application was undeployed");

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

            String str = String.format("deploy %s\n", packet.attributes.get(0));
            cliWriter.write(str);
            cliWriter.flush();

            cliWriter.write("command\n");
            cliWriter.flush();

            packet.typePacket = "response";
            packet.attributes.clear();

            String msg;
            msg = "Application was deployed";

            String line;
            while((line = cliReader.readLine()) != null && !line.contains("Command is missing.")){
                System.out.println(line);
                if(line.contains("already exists")){
                    msg = "Application was already deployed";
                } else if(line.contains("doesn't exist")){
                    msg = "Application doesn't exist";
                } else if(line.contains("Failed to mount")){
                    msg = "Application incorrect";
                }
            }

            packet.attributes.add(msg);

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

            cliWriter.write("command\n");
            cliWriter.flush();

            packet.typePacket = "response";
            packet.attributes.clear();

            String msg;
            msg = "Application was disabled";

            String line;
            while((line = cliReader.readLine()) != null && !line.contains("Command is missing.")){
                System.out.println(line);
                if(line.contains("not found")){
                    msg = "Application wasn't deployed";
                }
            }

            packet.attributes.add(msg);

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

            cliWriter.write("command\n");
            cliWriter.flush();

            packet.typePacket = "response";
            packet.attributes.clear();

            String msg;
            msg = "Application was enabled";

            String line;
            while((line = cliReader.readLine()) != null && !line.contains("Command is missing.")){
                System.out.println(line);
                if(line.contains("not found")){
                    msg = "Application wasn't deployed";
                }
            }

            packet.attributes.add(msg);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void shutdownServer(DataTransferPacket packet) {
        try {
            Process proc = Runtime.getRuntime().exec(PATH_WILDFLY_BIN + "jboss-cli.sh -c --command=:shutdown");

            BufferedReader input = new BufferedReader(new InputStreamReader(
                    proc.getInputStream()));


            String msg = "";

            String line;
            while((line = input.readLine()) != null){
                if(line.contains("\"outcome\" => \"success\"")){
                    msg = "Server was shutdown";
                    break;
                } else if(line.contains("Failed to connect to the controller")){
                    msg = "Server is already shutdown";
                }
            }

            packet.typePacket = "response";
            packet.attributes.add(msg);

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

            String msg = "";

            String line;
            while((line = input.readLine()) != null){
                if(line.contains("listening on http://127.0.0.1:9990")){
                    msg = "Server was running";
                    break;
                } else if(line.contains("Address already in use /127.0.0.1:9990")){
                    msg = "Server is already running";
                    break;
                }
            }

            packet.typePacket = "response";
            packet.attributes.add(msg);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void statusApplicationSequence(DataTransferPacket packet)
    {
        List<String> appListWithFullPath = readFile(packet.attributes.get(0));
        LinkedList<String> appList = new LinkedList<>();

        for (String app : appListWithFullPath) {
            String appName = app.substring(app.lastIndexOf("/") + 1);
            appList.add(appName);
        }

        if(cliProcess == null && !connectToCLI(packet)){
            return;
        }

        getStatusApplications(packet, appList);
    }

    private void statusApplication(DataTransferPacket packet)
    {
            if(cliProcess == null && !connectToCLI(packet)){
                return;
            }

            LinkedList<String> appList = new LinkedList<>();
            appList.addAll(packet.attributes);
            getStatusApplications(packet, appList);
    }

    private void getStatusApplications(DataTransferPacket packet, LinkedList<String> appList)
    {
        try {
            String line;

            packet.typePacket = "response";

            cliWriter.write("deploy -l\n");
            cliWriter.flush();

            cliWriter.write("command\n");
            cliWriter.flush();

            packet.attributes.clear();
            packet.attributes.add("Application - Status");

            while((line = cliReader.readLine()) != null && !line.contains("Command is missing.")){
                System.out.println(line);
                line = line.trim().replaceAll(" +", " ");
                String[] splitLine = line.split(" ");
                if(appList.contains(splitLine[0])){
                    if(splitLine[2].contains("true")){
                        packet.attributes.add(String.format("%s - \u001B[32menabled\u001B[0m",
                                appList.get(appList.indexOf(splitLine[0]))));
                        appList.remove(splitLine[0]);
                    } else if(splitLine[2].contains("false")) {
                        packet.attributes.add(String.format("%s - \u001B[31mdisabled\u001B[0m",
                                appList.get(appList.indexOf(splitLine[0]))));
                        appList.remove(splitLine[0]);
                    }
                }
            }

            for(String app: appList){
                packet.attributes.add(String.format("%s - \u001B[33mdoesn't exist or wasn't deployed\u001B[0m", app));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
