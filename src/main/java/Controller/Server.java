package Controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server implements Runnable {
    Socket socket;

    public Server(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            System.out.printf("Connection received from %s\n", socket.getOutputStream());
            PrintWriter pw = new PrintWriter(socket.getOutputStream());
            Scanner in = new Scanner(socket.getInputStream());
            while (in.hasNextLine()) {
                String line = in.nextLine();
                System.out.printf("%s says %s\n", socket, line);
                pw.printf("echo: %s\n", line);
                pw.flush();
            }
            pw.close();
            in.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(4343);
        System.out.printf("Socket open, waiting for connections on %s\n", serverSocket);
        while(true) {
            Socket socket = serverSocket.accept();
            Server server = new Server(socket);
            new Thread(server).start();
        }
    }
}
