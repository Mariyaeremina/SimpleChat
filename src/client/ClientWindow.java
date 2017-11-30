package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class ClientWindow extends JFrame
{
    private Connection connection;

    private JTextField messageInput;
    private JTextField nameInput;
    private JTextArea messageArea;
    private String clientName = "";

    public ClientWindow()
    {
        connection = new Connection();

        initialize();

        JScrollPane scroll = new JScrollPane(messageArea);
        JLabel numberOfClients = new JLabel("Количество клиентов в чате: ");
        JButton jbSendMessage = new JButton("Отправить");
        JPanel bottomPanel = new JPanel(new BorderLayout());

        bottomPanel.add(jbSendMessage, BorderLayout.EAST);
        bottomPanel.add(messageInput, BorderLayout.CENTER);
        bottomPanel.add(nameInput, BorderLayout.WEST);

        add(scroll, BorderLayout.CENTER);
        add(numberOfClients, BorderLayout.NORTH);
        add(bottomPanel, BorderLayout.SOUTH);

        jbSendMessage.addActionListener((e) ->
        {
            if (!messageInput.getText().trim().isEmpty() && !nameInput.getText().trim().isEmpty())
            {
                clientName = nameInput.getText();
                sendMessage();
                messageInput.grabFocus();
            }
        });

        messageInput.addFocusListener(new FocusAdapter()
        {
            @Override
            public void focusGained(FocusEvent e)
            {
                messageInput.setText("");
            }
        });

        nameInput.addFocusListener(new FocusAdapter()
        {
            @Override
            public void focusGained(FocusEvent e)
            {
                nameInput.setText("");
            }
        });

        new Thread(() ->
        {
            try
            {
                while (true)
                {
                    if (connection.InMessage.hasNext())
                    {
                        String inMes = connection.InMessage.nextLine();
                        String clientsInChat = "Клиентов в чате = ";
                        if (inMes.indexOf(clientsInChat) == 0)
                        {
                            numberOfClients.setText(inMes);
                        }
                        else
                        {
                            messageArea.append(inMes);
                            messageArea.append("\n");
                        }
                    }
                }
            }
            catch (Exception e)
            {
            }
        }).start();

        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                super.windowClosing(e);
                try
                {
                    if (!clientName.isEmpty() && clientName != "Введите ваше имя: ")
                    {
                        connection.OutMessage.println(clientName + " вышел из чата.");
                    }
                    else
                    {
                        connection.OutMessage.println("Участник вышел из чата, так и не представившись.");
                    }

                    connection.OutMessage.println("##session##end##");
                    connection.OutMessage.flush();
                    connection.OutMessage.close();
                    connection.InMessage.close();
                    connection.ClientSocket.close();
                }
                catch (IOException exc)
                {
                    exc.printStackTrace();
                }
            }
        });

        setVisible(true);
    }

    public void sendMessage()
    {
        String messageStr = nameInput.getText() + ": " + messageInput.getText();
        connection.OutMessage.println(messageStr);
        connection.OutMessage.flush();
        messageInput.setText("");
    }

    private void initialize()
    {
        setBounds(600, 300, 400, 500);
        setTitle("Client");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        messageArea = new JTextArea();
        messageArea.setEditable(false);
        messageArea.setLineWrap(true);
        messageInput = new JTextField("Введите ваше сообщение: ");
        nameInput = new JTextField("Введите ваше имя: ");
    }
}