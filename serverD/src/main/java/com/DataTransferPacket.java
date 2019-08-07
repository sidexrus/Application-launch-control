package com;

import java.util.ArrayList;

public class DataTransferPacket
{
    public String typePacket;
    public String commandText;
    public ArrayList<String> attributes = new ArrayList<>();

    public DataTransferPacket()
    {
        typePacket = "";
        commandText = "";
    }

    public DataTransferPacket(ArrayList<String> text)
    {
        typePacket = text.get(0);
        if(typePacket.equals("command") || typePacket.equals("dbRequest")){
            commandText = text.get(1);
            attributes.addAll(text.subList(2, text.size()));
        } else {
            attributes.addAll(text.subList(1, text.size()));
        }
    }

    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append(typePacket + ": ");
        builder.append(commandText + " ");
        builder.append("(" + attributes + ")");
        return builder.toString();
    }
}
