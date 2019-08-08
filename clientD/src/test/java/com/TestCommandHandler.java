package com;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestCommandHandler {

    private String PATH_WILDFLY_BIN = "wildfly-17.0.0.Final/bin/";
    private String DB_SERVER_ADDRESS = "127.0.0.1";
    private int DB_SERVER_PORT = 5010;
    String WAR_PLACE = "exampleWar/";

    @Test
    public void testStartServer()
    {
        CommandHandler commandHandler = new CommandHandler(PATH_WILDFLY_BIN, DB_SERVER_ADDRESS, DB_SERVER_PORT);

        DataTransferPacket packet = new DataTransferPacket();
        packet.commandText = "startServer";

        commandHandler.processCommand(packet);

        assertEquals("Server was running", packet.attributes.get(0));

        try {
            Runtime.getRuntime().exec(PATH_WILDFLY_BIN + "jboss-cli.sh -c --command=:shutdown");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testStartServerTwice()
    {
        CommandHandler commandHandler = new CommandHandler(PATH_WILDFLY_BIN, DB_SERVER_ADDRESS, DB_SERVER_PORT);

        DataTransferPacket packet = new DataTransferPacket();
        packet.commandText = "startServer";

        commandHandler.processCommand(packet);
        packet.attributes.clear();
        commandHandler.processCommand(packet);

        assertEquals("Server is already running", packet.attributes.get(0));

        try {
            Runtime.getRuntime().exec(PATH_WILDFLY_BIN + "jboss-cli.sh -c --command=:shutdown");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testShutdownServer()
    {
        CommandHandler commandHandler = new CommandHandler(PATH_WILDFLY_BIN, DB_SERVER_ADDRESS, DB_SERVER_PORT);

        DataTransferPacket packet = new DataTransferPacket();

        packet.commandText = "startServer";
        commandHandler.processCommand(packet);
        packet.attributes.clear();

        packet.commandText = "shutdownServer";
        commandHandler.processCommand(packet);

        assertEquals("Server was shutdown", packet.attributes.get(0));
    }

    @Test
    public void testShutdownServerTwice()
    {
        CommandHandler commandHandler = new CommandHandler(PATH_WILDFLY_BIN, DB_SERVER_ADDRESS, DB_SERVER_PORT);

        DataTransferPacket packet = new DataTransferPacket();

        packet.commandText = "startServer";
        commandHandler.processCommand(packet);

        packet.commandText = "shutdownServer";
        commandHandler.processCommand(packet);
        packet.attributes.clear();
        commandHandler.processCommand(packet);

        assertEquals("Server is already shutdown", packet.attributes.get(0));
    }

    @Test
    public void testDeployApplication()
    {
        CommandHandler commandHandler = new CommandHandler(PATH_WILDFLY_BIN, DB_SERVER_ADDRESS, DB_SERVER_PORT);

        DataTransferPacket packet = new DataTransferPacket();

        packet.commandText = "startServer";
        commandHandler.processCommand(packet);

        packet.commandText = "deployApp";
        packet.attributes.clear();
        packet.attributes.add(WAR_PLACE + "helloworld.war");
        commandHandler.processCommand(packet);

        assertEquals("Application was deployed", packet.attributes.get(0));

        packet.commandText = "undeployApp";
        packet.attributes.clear();
        packet.attributes.add("helloworld.war");
        commandHandler.processCommand(packet);

        packet.commandText = "shutdownServer";
        commandHandler.processCommand(packet);
    }

    @Test
    public void testDeployApplicationTwice()
    {
        CommandHandler commandHandler = new CommandHandler(PATH_WILDFLY_BIN, DB_SERVER_ADDRESS, DB_SERVER_PORT);

        DataTransferPacket packet = new DataTransferPacket();

        packet.commandText = "startServer";
        commandHandler.processCommand(packet);

        packet.commandText = "deployApp";
        packet.attributes.clear();
        packet.attributes.add(WAR_PLACE + "helloworld.war");
        commandHandler.processCommand(packet);

        packet.commandText = "deployApp";
        packet.attributes.clear();
        packet.attributes.add(WAR_PLACE + "helloworld.war");
        commandHandler.processCommand(packet);

        assertEquals("Application was already deployed", packet.attributes.get(0));

        packet.commandText = "undeployApp";
        packet.attributes.clear();
        packet.attributes.add("helloworld.war");
        commandHandler.processCommand(packet);

        packet.commandText = "shutdownServer";
        commandHandler.processCommand(packet);
    }

    @Test
    public void testUndeployApplicationTwice()
    {
        CommandHandler commandHandler = new CommandHandler(PATH_WILDFLY_BIN, DB_SERVER_ADDRESS, DB_SERVER_PORT);

        DataTransferPacket packet = new DataTransferPacket();

        packet.commandText = "startServer";
        commandHandler.processCommand(packet);

        packet.commandText = "deployApp";
        packet.attributes.clear();
        packet.attributes.add(WAR_PLACE + "helloworld.war");
        commandHandler.processCommand(packet);

        packet.commandText = "undeployApp";
        packet.attributes.clear();
        packet.attributes.add("helloworld.war");
        commandHandler.processCommand(packet);

        packet.commandText = "undeployApp";
        packet.attributes.clear();
        packet.attributes.add("helloworld.war");
        commandHandler.processCommand(packet);

        packet.commandText = "shutdownServer";
        commandHandler.processCommand(packet);
    }

    @Test
    public void testEnableApplication()
    {
        CommandHandler commandHandler = new CommandHandler(PATH_WILDFLY_BIN, DB_SERVER_ADDRESS, DB_SERVER_PORT);

        DataTransferPacket packet = new DataTransferPacket();

        packet.commandText = "startServer";
        commandHandler.processCommand(packet);

        packet.commandText = "deployApp";
        packet.attributes.clear();
        packet.attributes.add(WAR_PLACE + "helloworld.war");
        commandHandler.processCommand(packet);

        packet.commandText = "enableApp";
        packet.attributes.clear();
        packet.attributes.add("helloworld.war");
        commandHandler.processCommand(packet);

        packet.commandText = "statusApp";
        packet.attributes.clear();
        packet.attributes.add("helloworld.war");
        commandHandler.processCommand(packet);

        assertEquals("helloworld.war - \u001B[32menabled\u001B[0m", packet.attributes.get(1));

        packet.commandText = "undeployApp";
        packet.attributes.clear();
        packet.attributes.add("helloworld.war");
        commandHandler.processCommand(packet);

        packet.commandText = "shutdownServer";
        commandHandler.processCommand(packet);
    }

    @Test
    public void testDisableApplication()
    {
        CommandHandler commandHandler = new CommandHandler(PATH_WILDFLY_BIN, DB_SERVER_ADDRESS, DB_SERVER_PORT);

        DataTransferPacket packet = new DataTransferPacket();

        packet.commandText = "startServer";
        commandHandler.processCommand(packet);

        packet.commandText = "deployApp";
        packet.attributes.clear();
        packet.attributes.add(WAR_PLACE + "helloworld.war");
        commandHandler.processCommand(packet);

        packet.commandText = "enableApp";
        packet.attributes.clear();
        packet.attributes.add("helloworld.war");
        commandHandler.processCommand(packet);

        packet.commandText = "disableApp";
        packet.attributes.clear();
        packet.attributes.add("helloworld.war");
        commandHandler.processCommand(packet);

        packet.commandText = "statusApp";
        packet.attributes.clear();
        packet.attributes.add("helloworld.war");
        commandHandler.processCommand(packet);

        assertEquals("helloworld.war - \u001B[31mdisabled\u001B[0m", packet.attributes.get(1));

        packet.commandText = "undeployApp";
        packet.attributes.clear();
        packet.attributes.add("helloworld.war");
        commandHandler.processCommand(packet);

        packet.commandText = "shutdownServer";
        commandHandler.processCommand(packet);
    }

    @Test
    public void testDeployApplicationSequence()
    {
        CommandHandler commandHandler = new CommandHandler(PATH_WILDFLY_BIN, DB_SERVER_ADDRESS, DB_SERVER_PORT);

        DataTransferPacket packet = new DataTransferPacket();

        packet.commandText = "startServer";
        commandHandler.processCommand(packet);

        packet.commandText = "deploySeq";
        packet.attributes.clear();
        packet.attributes.add("seq");
        commandHandler.processCommand(packet);

        packet.commandText = "statusApp";
        packet.attributes.clear();
        packet.attributes.add("helloworld.war");
        commandHandler.processCommand(packet);

        assertEquals("helloworld.war - \u001B[32menabled\u001B[0m", packet.attributes.get(1));

        packet.commandText = "statusApp";
        packet.attributes.clear();
        packet.attributes.add("greeter.war");
        commandHandler.processCommand(packet);

        assertEquals("greeter.war - \u001B[32menabled\u001B[0m", packet.attributes.get(1));

        packet.commandText = "shutdownServer";
        commandHandler.processCommand(packet);
    }

    @Test
    public void testUndeployApplicationSequence()
    {
        CommandHandler commandHandler = new CommandHandler(PATH_WILDFLY_BIN, DB_SERVER_ADDRESS, DB_SERVER_PORT);

        DataTransferPacket packet = new DataTransferPacket();

        packet.commandText = "startServer";
        commandHandler.processCommand(packet);

        packet.commandText = "deploySeq";
        packet.attributes.clear();
        packet.attributes.add("seq");
        commandHandler.processCommand(packet);

        packet.commandText = "undeploySeq";
        packet.attributes.clear();
        packet.attributes.add("seq");
        commandHandler.processCommand(packet);

        packet.commandText = "statusApp";
        packet.attributes.clear();
        packet.attributes.add("helloworld.war");
        commandHandler.processCommand(packet);

        assertEquals("helloworld.war - \u001B[33mdoesn't exist or wasn't deployed\u001B[0m", packet.attributes.get(1));

        packet.commandText = "statusApp";
        packet.attributes.clear();
        packet.attributes.add("greeter.war");
        commandHandler.processCommand(packet);

        assertEquals("greeter.war - \u001B[33mdoesn't exist or wasn't deployed\u001B[0m", packet.attributes.get(1));

        packet.commandText = "shutdownServer";
        commandHandler.processCommand(packet);
    }

    @Test
    public void testEnableApplicationSequence()
    {
        CommandHandler commandHandler = new CommandHandler(PATH_WILDFLY_BIN, DB_SERVER_ADDRESS, DB_SERVER_PORT);

        DataTransferPacket packet = new DataTransferPacket();

        packet.commandText = "startServer";
        commandHandler.processCommand(packet);

        packet.commandText = "deploySeq";
        packet.attributes.clear();
        packet.attributes.add("seq");
        commandHandler.processCommand(packet);

        packet.commandText = "enableSeq";
        packet.attributes.clear();
        packet.attributes.add("seq");
        commandHandler.processCommand(packet);

        packet.commandText = "statusApp";
        packet.attributes.clear();
        packet.attributes.add("helloworld.war");
        commandHandler.processCommand(packet);

        assertEquals("helloworld.war - \u001B[32menabled\u001B[0m", packet.attributes.get(1));

        packet.commandText = "statusApp";
        packet.attributes.clear();
        packet.attributes.add("greeter.war");
        commandHandler.processCommand(packet);

        assertEquals("greeter.war - \u001B[32menabled\u001B[0m", packet.attributes.get(1));

        packet.commandText = "undeploySeq";
        packet.attributes.clear();
        packet.attributes.add("seq");
        commandHandler.processCommand(packet);

        packet.commandText = "shutdownServer";
        commandHandler.processCommand(packet);
    }

    @Test
    public void testDisableApplicationSequence()
    {
        CommandHandler commandHandler = new CommandHandler(PATH_WILDFLY_BIN, DB_SERVER_ADDRESS, DB_SERVER_PORT);

        DataTransferPacket packet = new DataTransferPacket();

        packet.commandText = "startServer";
        commandHandler.processCommand(packet);

        packet.commandText = "deploySeq";
        packet.attributes.clear();
        packet.attributes.add("seq");
        commandHandler.processCommand(packet);

        packet.commandText = "enableSeq";
        packet.attributes.clear();
        packet.attributes.add("seq");
        commandHandler.processCommand(packet);

        packet.commandText = "disableSeq";
        packet.attributes.clear();
        packet.attributes.add("seq");
        commandHandler.processCommand(packet);

        packet.commandText = "statusApp";
        packet.attributes.clear();
        packet.attributes.add("helloworld.war");
        commandHandler.processCommand(packet);

        assertEquals("helloworld.war - \u001B[31mdisabled\u001B[0m", packet.attributes.get(1));

        packet.commandText = "statusApp";
        packet.attributes.clear();
        packet.attributes.add("greeter.war");
        commandHandler.processCommand(packet);

        assertEquals("greeter.war - \u001B[31mdisabled\u001B[0m", packet.attributes.get(1));

        packet.commandText = "undeploySeq";
        packet.attributes.clear();
        packet.attributes.add("seq");
        commandHandler.processCommand(packet);

        packet.commandText = "shutdownServer";
        commandHandler.processCommand(packet);
    }

    @Test
    public void testStatusApplicationSequence()
    {
        CommandHandler commandHandler = new CommandHandler(PATH_WILDFLY_BIN, DB_SERVER_ADDRESS, DB_SERVER_PORT);

        DataTransferPacket packet = new DataTransferPacket();

        packet.commandText = "startServer";
        commandHandler.processCommand(packet);

        packet.commandText = "deploySeq";
        packet.attributes.clear();
        packet.attributes.add("seq");
        commandHandler.processCommand(packet);

        packet.commandText = "statusSeq";
        packet.attributes.clear();
        packet.attributes.add("seq");
        commandHandler.processCommand(packet);

        assertEquals("greeter.war - \u001B[32menabled\u001B[0m", packet.attributes.get(1));
        assertEquals("helloworld.war - \u001B[32menabled\u001B[0m", packet.attributes.get(2));

        packet.commandText = "undeploySeq";
        packet.attributes.clear();
        packet.attributes.add("seq");
        commandHandler.processCommand(packet);

        packet.commandText = "shutdownServer";
        commandHandler.processCommand(packet);
    }

}
