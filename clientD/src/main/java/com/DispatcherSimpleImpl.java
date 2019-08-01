package com;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class DispatcherSimpleImpl implements Dispatcher {
    private CommandHandler commandHandler;

    public DispatcherSimpleImpl()
    {
        try {
            File configFile = new File("config");
            FileInputStream in = new FileInputStream(configFile);
            byte[] data = new byte[(int)configFile.length()];

            in.read(data);
            in.close();

            String configContent = new String(data, StandardCharsets.UTF_8);

            JSONObject rootObject = new JSONObject(configContent);

            String pathWildflyBin = rootObject.getString("PATH_WILDFLY_BIN");
            commandHandler = new CommandHandler(pathWildflyBin);
            System.out.println(pathWildflyBin);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void dispatchObject(DataTransferPacket packet)
    {
        commandHandler.processCommand(packet);
    }
}
