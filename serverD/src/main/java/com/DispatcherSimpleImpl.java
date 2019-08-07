package com;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class DispatcherSimpleImpl implements Dispatcher {
    private DatabaseRequestHandler databaseRequestHandler;

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

            String dbServerAddress = rootObject.getString("DB_SERVER_ADDRESS");
            String dbUser = rootObject.getString("DB_USER");
            String dbUserPassword = rootObject.getString("DB_USER_PASSWORD");
            databaseRequestHandler = new DatabaseRequestHandler(dbServerAddress, dbUser, dbUserPassword);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void dispatchObject(DataTransferPacket packet)
    {
        if(packet.typePacket.equals("dbRequest")){
            databaseRequestHandler.processRequest(packet);
        }
    }
}
