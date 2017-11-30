package server;
import java.io.IOException;
import java.util.logging.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server
{
    private Logger logger = Logger.getLogger(Server.class.getName());
    static final int PORT = 1111;
    private ArrayList<ClientHandler> clients = new ArrayList<>();

    public Server()
    {
        createLogFile();
        Socket clientSocket = null;
        ServerSocket serverSocket = null;
        try
        {
            serverSocket = new ServerSocket(PORT);

            logger.log(Level.INFO, "Сервер запущен");
            while (true)
            {
                clientSocket = serverSocket.accept();
                logger.log(Level.INFO, "Принято подключение от " + clientSocket.getRemoteSocketAddress());
                ClientHandler client = new ClientHandler(clientSocket, this);
                clients.add(client);
                new Thread(client).start();
            }
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            logger.log(Level.WARNING, "Не удалось установить соединение с клиентом", ex);
        }
        finally
        {
            try
            {
                clientSocket.close();
                logger.log(Level.INFO, "Сервер остановлен");
                serverSocket.close();
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
                logger.log(Level.WARNING, "Не удалось завершить подключение", ex);
            }
        }
    }

    private void createLogFile()
    {
        try
        {
            FileHandler file = new FileHandler("Server");
            logger.addHandler(file);
        }
        catch (SecurityException e)
        {
            logger.log(Level.SEVERE, "Не удалось создать файл лога из-за политики безопасности.", e);
        }
        catch (IOException e)
        {
            logger.log(Level.SEVERE, "Не удалось создать файл лога из-за ошибки ввода-вывода.", e);
        }
    }

    public void SendMessageToAllClients(String message)
    {
        for (ClientHandler o : clients)
        {
            o.SendMessage(message);
        }

    }

    // удаляем клиента из коллекции при выходе из чата
    public void RemoveClient(ClientHandler client)
    {
        logger.log(Level.INFO, "Подключение с " + client.ClientSocket.getRemoteSocketAddress() + " завершено");
        clients.remove(client);
    }
}
