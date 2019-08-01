package com;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestCommandHandler {

    private String PATH_WILDFLY_BIN = "/home/tester/Downloads/wildfly-17.0.0.Final/bin/";
    String WAR_PLACE = "exampleWar/";

    @Test
    public void testStartServer()
    {
        CommandHandler commandHandler =
                new CommandHandler(PATH_WILDFLY_BIN);

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
        CommandHandler commandHandler =
                new CommandHandler(PATH_WILDFLY_BIN);

        DataTransferPacket packet = new DataTransferPacket();
        packet.commandText = "startServer";

        commandHandler.processCommand(packet);
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
        CommandHandler commandHandler =
                new CommandHandler(PATH_WILDFLY_BIN);

        DataTransferPacket packet = new DataTransferPacket();

        packet.commandText = "startServer";
        commandHandler.processCommand(packet);

        packet.commandText = "shutdownServer";
        commandHandler.processCommand(packet);

        assertEquals("Server was shutdown", packet.attributes.get(0));
    }

    @Test
    public void testShutdownServerTwice()
    {
        CommandHandler commandHandler =
                new CommandHandler(PATH_WILDFLY_BIN);

        DataTransferPacket packet = new DataTransferPacket();

        packet.commandText = "startServer";
        commandHandler.processCommand(packet);

        packet.commandText = "shutdownServer";
        commandHandler.processCommand(packet);
        commandHandler.processCommand(packet);

        assertEquals("Server is already shutdown", packet.attributes.get(0));
    }

    @Test
    public void testDeployApplication()
    {
        CommandHandler commandHandler = new CommandHandler(PATH_WILDFLY_BIN);

        DataTransferPacket packet = new DataTransferPacket();

        packet.commandText = "startServer";
        commandHandler.processCommand(packet);

        packet.commandText = "deployApp";
        packet.attributes = Collections.singletonList(WAR_PLACE + "helloworld.war");
        commandHandler.processCommand(packet);

        assertEquals("Application was deployment", packet.attributes.get(0));

        packet.commandText = "undeployApp";
        packet.attributes = Collections.singletonList("helloworld.war");
        commandHandler.processCommand(packet);

        packet.commandText = "shutdownServer";
        commandHandler.processCommand(packet);
    }

    @Test
    public void testDeployApplicationTwice()
    {
        CommandHandler commandHandler = new CommandHandler(PATH_WILDFLY_BIN);

        DataTransferPacket packet = new DataTransferPacket();

        packet.commandText = "startServer";
        commandHandler.processCommand(packet);

        packet.commandText = "deployApp";
        packet.attributes = Collections.singletonList(WAR_PLACE + "helloworld.war");
        commandHandler.processCommand(packet);

        packet.commandText = "deployApp";
        packet.attributes = Collections.singletonList(WAR_PLACE + "helloworld.war");
        commandHandler.processCommand(packet);

        assertEquals("Application was deployment", packet.attributes.get(0));

        packet.commandText = "undeployApp";
        packet.attributes = Collections.singletonList("helloworld.war");
        commandHandler.processCommand(packet);

        packet.commandText = "shutdownServer";
        commandHandler.processCommand(packet);
    }

    @Test
    public void testUndeployApplicationTwice()
    {
        CommandHandler commandHandler = new CommandHandler(PATH_WILDFLY_BIN);

        DataTransferPacket packet = new DataTransferPacket();

        packet.commandText = "startServer";
        commandHandler.processCommand(packet);

        packet.commandText = "deployApp";
        packet.attributes = Collections.singletonList(WAR_PLACE + "helloworld.war");
        commandHandler.processCommand(packet);

        packet.commandText = "undeployApp";
        packet.attributes = Collections.singletonList("helloworld.war");
        commandHandler.processCommand(packet);

        packet.commandText = "undeployApp";
        packet.attributes = Collections.singletonList("helloworld.war");
        commandHandler.processCommand(packet);

        packet.commandText = "shutdownServer";
        commandHandler.processCommand(packet);
    }

    @Test
    public void testEnableApplication()
    {
        CommandHandler commandHandler = new CommandHandler(PATH_WILDFLY_BIN);

        DataTransferPacket packet = new DataTransferPacket();

        packet.commandText = "startServer";
        commandHandler.processCommand(packet);

        packet.commandText = "deployApp";
        packet.attributes = Collections.singletonList(WAR_PLACE + "helloworld.war");
        commandHandler.processCommand(packet);

        packet.commandText = "enableApp";
        packet.attributes = Collections.singletonList("helloworld.war");
        commandHandler.processCommand(packet);

        packet.commandText = "statusApp";
        packet.attributes = Collections.singletonList("helloworld.war");
        commandHandler.processCommand(packet);

        assertEquals("Status: enabled", packet.attributes.get(1));

        packet.commandText = "undeployApp";
        packet.attributes = Collections.singletonList("helloworld.war");
        commandHandler.processCommand(packet);

        packet.commandText = "shutdownServer";
        commandHandler.processCommand(packet);
    }

    @Test
    public void testDisableApplication()
    {
        CommandHandler commandHandler = new CommandHandler(PATH_WILDFLY_BIN);

        DataTransferPacket packet = new DataTransferPacket();

        packet.commandText = "startServer";
        commandHandler.processCommand(packet);

        packet.commandText = "deployApp";
        packet.attributes = Collections.singletonList(WAR_PLACE + "helloworld.war");
        commandHandler.processCommand(packet);

        packet.commandText = "enableApp";
        packet.attributes = Collections.singletonList("helloworld.war");
        commandHandler.processCommand(packet);

        packet.commandText = "disableApp";
        packet.attributes = Collections.singletonList("helloworld.war");
        commandHandler.processCommand(packet);

        packet.commandText = "statusApp";
        packet.attributes = Collections.singletonList("helloworld.war");
        commandHandler.processCommand(packet);

        assertEquals("Status: disabled", packet.attributes.get(1));

        packet.commandText = "undeployApp";
        packet.attributes = Collections.singletonList("helloworld.war");
        commandHandler.processCommand(packet);

        packet.commandText = "shutdownServer";
        commandHandler.processCommand(packet);
    }

    @Test
    public void testDeployApplicationSequence()
    {
        CommandHandler commandHandler = new CommandHandler(PATH_WILDFLY_BIN);

        DataTransferPacket packet = new DataTransferPacket();

        packet.commandText = "startServer";
        commandHandler.processCommand(packet);

        packet.commandText = "deploySeq";
        packet.attributes = Collections.singletonList("seq");
        commandHandler.processCommand(packet);

        packet.commandText = "statusApp";
        packet.attributes = Collections.singletonList("helloworld.war");
        commandHandler.processCommand(packet);

        assertEquals("Status: enabled", packet.attributes.get(1));

        packet.commandText = "statusApp";
        packet.attributes = Collections.singletonList("greeter.war");
        commandHandler.processCommand(packet);

        assertEquals("Status: enabled", packet.attributes.get(1));

        packet.commandText = "shutdownServer";
        commandHandler.processCommand(packet);
    }

    @Test
    public void testUndeployApplicationSequence()
    {
        CommandHandler commandHandler = new CommandHandler(PATH_WILDFLY_BIN);

        DataTransferPacket packet = new DataTransferPacket();

        packet.commandText = "startServer";
        commandHandler.processCommand(packet);

        packet.commandText = "deploySeq";
        packet.attributes = Collections.singletonList("seq");
        commandHandler.processCommand(packet);

        packet.commandText = "undeploySeq";
        packet.attributes = Collections.singletonList("seq");
        commandHandler.processCommand(packet);

        packet.commandText = "statusApp";
        packet.attributes = Collections.singletonList("helloworld.war");
        commandHandler.processCommand(packet);

        assertEquals(1, packet.attributes.size());

        packet.commandText = "statusApp";
        packet.attributes = Collections.singletonList("greeter.war");
        commandHandler.processCommand(packet);

        assertEquals(1, packet.attributes.size());

        packet.commandText = "shutdownServer";
        commandHandler.processCommand(packet);
    }

    @Test
    public void testEnableApplicationSequence()
    {
        CommandHandler commandHandler = new CommandHandler(PATH_WILDFLY_BIN);

        DataTransferPacket packet = new DataTransferPacket();

        packet.commandText = "startServer";
        commandHandler.processCommand(packet);

        packet.commandText = "deploySeq";
        packet.attributes = Collections.singletonList("seq");
        commandHandler.processCommand(packet);

        packet.commandText = "enableSeq";
        packet.attributes = Collections.singletonList("seq");
        commandHandler.processCommand(packet);

        packet.commandText = "statusApp";
        packet.attributes = Collections.singletonList("helloworld.war");
        commandHandler.processCommand(packet);

        assertEquals("Status: enabled", packet.attributes.get(1));

        packet.commandText = "statusApp";
        packet.attributes = Collections.singletonList("greeter.war");
        commandHandler.processCommand(packet);

        assertEquals("Status: enabled", packet.attributes.get(1));

        packet.commandText = "undeploySeq";
        packet.attributes = Collections.singletonList("seq");
        commandHandler.processCommand(packet);

        packet.commandText = "shutdownServer";
        commandHandler.processCommand(packet);
    }

    @Test
    public void testDisableApplicationSequence()
    {
        CommandHandler commandHandler = new CommandHandler(PATH_WILDFLY_BIN);

        DataTransferPacket packet = new DataTransferPacket();

        packet.commandText = "startServer";
        commandHandler.processCommand(packet);

        packet.commandText = "deploySeq";
        packet.attributes = Collections.singletonList("seq");
        commandHandler.processCommand(packet);

        packet.commandText = "enableSeq";
        packet.attributes = Collections.singletonList("seq");
        commandHandler.processCommand(packet);

        packet.commandText = "disableSeq";
        packet.attributes = Collections.singletonList("seq");
        commandHandler.processCommand(packet);

        packet.commandText = "statusApp";
        packet.attributes = Collections.singletonList("helloworld.war");
        commandHandler.processCommand(packet);

        assertEquals("Status: disabled", packet.attributes.get(1));

        packet.commandText = "statusApp";
        packet.attributes = Collections.singletonList("greeter.war");
        commandHandler.processCommand(packet);

        assertEquals("Status: disabled", packet.attributes.get(1));

        packet.commandText = "undeploySeq";
        packet.attributes = Collections.singletonList("seq");
        commandHandler.processCommand(packet);

        packet.commandText = "shutdownServer";
        commandHandler.processCommand(packet);
    }

}
