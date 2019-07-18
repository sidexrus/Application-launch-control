package com;

import java.util.List;

public class DataTransferPacket
{
    public String typePacket;
    public String commandText;
    public List<String> attributes;

    public DataTransferPacket(List<String> text)
    {
        typePacket = text.get(0);
        if(typePacket.equals("command")){
            commandText = text.get(1);
            attributes = text.subList(2, text.size());
        } else {
            attributes = text.subList(1, text.size());
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
