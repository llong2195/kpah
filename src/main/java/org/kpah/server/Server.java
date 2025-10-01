package org.kpah.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.sql.SQLException;
import java.util.Scanner;

import org.fusesource.jansi.AnsiConsole;
import org.kpah.manager.ClanManager;
import org.kpah.manager.ClientManager;
import org.kpah.manager.ExecutorVirtualThread;
import org.kpah.manager.Manager;
import org.kpah.manager.Settings;
import org.kpah.manager.TopManager;
import org.kpah.network.MessageHandler;
import org.kpah.network.MessageSendCollect;
import org.kpah.network.Session;
import org.kpah.utils.Logger;
import org.kpah.utils.Printer;

public class Server implements Runnable {

    private ServerSocketChannel serverChannel;
    private boolean isBaoTri;

    public static void main(String[] args) {
        Server server = new Server();
        server.init();
    }

    public void init() {
        new Thread(this, "Server Socket Thread").start();
    }

    @Override
    public void run() {
        try {
            AnsiConsole.systemInstall();
            serverChannel = ServerSocketChannel.open();
            serverChannel.bind(new InetSocketAddress(Settings.PORT_SERVER));
            serverChannel.configureBlocking(false);
            // Printer.printAscii(Settings.LOGO_GAME, 0, 255, 255);
            // Printer.printAscii(Settings.ICON, Ansi.Color.RED);
            Manager.init();
            activeCommandLine();
            Printer.printGreen("Listen Port " + Settings.PORT_SERVER);
            ExecutorVirtualThread.submitServer(ClanManager.update());
            ExecutorVirtualThread.submitServer(TopManager.updateTopClan());
            while (!isBaoTri) {
                SocketChannel clientChannel = serverChannel.accept();
                if (clientChannel != null) {
                    if (ClientManager.getPlayers().size() < Settings.MAX_PLAYER) {
                        ExecutorVirtualThread.submitServer(() -> handleClient(clientChannel));
                    } else {
                        clientChannel.close();
                    }
                }
            }
        } catch (IOException e) {
            Printer.printRed("Port Is Already Open");
        } finally {
            try {
                serverChannel.close();
            } catch (IOException e) {
                Printer.printRed("Error closing server channel.");
            }
            ExecutorVirtualThread.shutdownServer();
        }
    }

    private void handleClient(SocketChannel clientChannel) {
        try {
            Session session = new Session(clientChannel.socket());
            session.initThreadSession();
            session.setMessageHandler(new MessageHandler()).setSendCollect(new MessageSendCollect()).startCollect();
            ExecutorVirtualThread.submitThreadSession(session.update());
            Printer.printGreen("Accept IpAddress " + session.getIP());
            ClientManager.joinClient(session);
        } catch (IOException e) {
            Printer.printRed("Error Handling Client");
            Logger.logError("Lỗi Handle Client", e);
        }
    }

    private void activeCommandLine() {
        Thread.ofVirtual().start(this::commandLineHandle);
    }

    private void closeServer() throws SQLException, IOException {
        ClanManager.saveDataClan();
        ClientManager.close();
        if (serverChannel != null) {
            serverChannel.close();
        }
    }

    private void commandLineHandle() {
        try {
            try (Scanner sc = new Scanner(System.in)) {
                while (sc.hasNextLine()) {
                    String line;
                    line = sc.nextLine();
                    switch (line) {
                        case "baotri" -> {
                            isBaoTri = true;
                            closeServer();
                        }
                        case "thread" -> Printer.printRed("Thread count: " + Thread.activeCount());
                        case "player" -> Printer.printRed("Player in game: " + ClientManager.getPlayers().size());
                        case "session" -> Printer.printRed("Session connect: " + ClientManager.getClients().size());
                        case "help" -> {
                            Printer.printYellow("Command list:");
                            Printer.printYellow("baotri: Bảo trì server");
                            Printer.printYellow("thread: Xem số lượng thread đang hoạt động");
                            Printer.printYellow("player: Xem số lượng người chơi đang online");
                            Printer.printYellow("session: Xem số lượng session đang kết nối");
                            Printer.printYellow("help: Xem danh sách command");
                        }
                        default -> Printer.printRed("Unknown command: " + line);
                    }
                }
            }
        } catch (Exception e) {
            Logger.logError("Lỗi command line", e);
        }
    }
}
