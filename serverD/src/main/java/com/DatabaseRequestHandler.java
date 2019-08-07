package com;

import java.util.ArrayList;

public class DatabaseRequestHandler {

    DBConnection connection;

    public DatabaseRequestHandler(String dbAServerAddress, String dbUser, String dbUserPassword)
    {
        connection = new DBConnection(dbAServerAddress, dbUser, dbUserPassword);
    }

    public void processRequest(DataTransferPacket packet)
    {

        if(packet.commandText.equals("checkSeq")){
            checkApplicationSequence(packet);
        } else{
            packet.attributes.clear();
            packet.attributes.add("Incorrect command");
        }

    }

    private void checkApplicationSequence(DataTransferPacket packet)
    {
        ArrayList res = SequenceFinder.FindSequence(packet.attributes, connection);

        packet.attributes.clear();
        packet.attributes.addAll(res);
    }
}
