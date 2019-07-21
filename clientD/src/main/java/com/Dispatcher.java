package com;

import org.json.JSONObject;

import java.io.*;
import java.util.concurrent.Future;

public class Dispatcher
{
    private CommandHandler commandHandler;

    public Dispatcher()
    {
        try {
            File configFile = new File("config");
            FileInputStream in = new FileInputStream(configFile);
            byte[] data = new byte[(int)configFile.length()];

            in.read(data);
            in.close();

            String configContent = new String(data, "UTF-8");

            JSONObject rootObject = new JSONObject(configContent);

            String pathWildflyBin = rootObject.getString("PATH_WILDFLY_BIN");
            commandHandler = new CommandHandler(pathWildflyBin);
            System.out.println(pathWildflyBin);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void dispatchObject(DataTransferPacket packet)
    {
        commandHandler.processCommand(packet);
    }
}
