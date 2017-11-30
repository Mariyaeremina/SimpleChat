package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientHandler implements Runnable
{
    private Server server;
    private PrintWriter outMessage;
    private Scanner inMessage;
    public Socket ClientSocket = null;
    private static int clients_count = 0;

    public ClientHandler(Socket socket, Server server)
    {
        try
        {
            clients_count++;
            this.server = server;
            this.ClientSocket = socket;
            this.outMessage = new PrintWriter(socket.getOutputStream());
            this.inMessage = new Scanner(socket.getInputStream());
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void run()
    {
        try
        {
            server.SendMessageToAllClients("Новый участник вошёл в чат!");
            server.SendMessageToAllClients("Клиентов в чате = " + clients_count);

            while (true)
            {
                if (inMessage.hasNext())
                {
                    String clientMessage = inMessage.nextLine();
                    if (clientMessage.equalsIgnoreCase("##session##end##"))
                    {
                        break;
                    }
                    System.out.println(clientMessage);
                    server.SendMessageToAllClients(clientMessage);
                }
                Thread.sleep(100);
            }
        }
        catch (InterruptedException ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            this.Close();
        }
    }
    // отправляем сообщение
    public void SendMessage(String msg)
    {
        try
        {
            outMessage.println(msg);
            outMessage.flush();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void Close()
    {
        server.RemoveClient(this);
        clients_count--;
        server.SendMessageToAllClients("Клиентов в чате = " + clients_count);
    }
}