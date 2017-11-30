package client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Connection
{
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 1111;
    public Socket ClientSocket;
    public Scanner InMessage;
    public PrintWriter OutMessage;

    public Connection()
    {
        try
        {
            ClientSocket = new Socket(SERVER_HOST, SERVER_PORT);
            InMessage = new Scanner(ClientSocket.getInputStream());
            OutMessage = new PrintWriter(ClientSocket.getOutputStream());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
