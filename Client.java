import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Client extends JFrame {

    private JTextField userText;
    private JTextArea chatWindow;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private String message = "";
    private String serverIP;
    private Socket connection;

    //Constructor
    public Client(String host) {
        super("Client");
        serverIP = host;
        this.userText = new JTextField();
        this.userText.setEditable(false);
        this.userText.addActionListener(
            new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    sendMessage(e.getActionCommand());
                    userText.setText("");
                }
            }
        );
        add(userText, BorderLayout.SOUTH);
        chatWindow = new JTextArea();
        add(new JScrollPane(chatWindow), BorderLayout.CENTER);
        setSize(1000, 500);
        setVisible(true);
    }

    //Connect to server
    public void startRunning() {
        try {
            connectToServer();
            setupStreams();
            whileChatting();
        } catch (EOFException e) {
            showMessage("\nClient terminated connection.");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeApplication();
        }
    }

    //Connect to server
    private void connectToServer() throws IOException {
        showMessage("\nAttempting connection...");
        connection = new Socket(InetAddress.getByName(serverIP), 6789);
        showMessage("\nConnected to: " + connection.getInetAddress().getHostName());
    }

    //set up streams to send and receive messages
    private void setupStreams() throws IOException {
        output = new ObjectOutputStream(connection.getOutputStream());
        output.flush();
        input = new ObjectInputStream(connection.getInputStream());
        showMessage("\nYour streams are ready!");
    }

    //While chatting with server
    private void whileChatting() throws IOException {
        ableToType(true);
        do {
            try {
                message = (String) input.readObject();
                showMessage("\n" + message);
            } catch (ClassNotFoundException e) {
                showMessage("\nCan't process object type.");
            }
        } while (!message.equals("SERVER: END"));
    }

    //Close the streams and sockets
    private void closeApplication() {
        showMessage("\nClosing application...");
        ableToType(false);
        try {
            output.close();
            input.close();
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Send messages to server
    private void sendMessage(String message) {
        try {
            output.writeObject("CLIENT: " + message);
            output.flush();
            showMessage("\nCLIENT: " + message);
        } catch (IOException e) {
            chatWindow.append("\nCan't process message.");
        }
    }

    //Change or update chatWindow
    private void showMessage(final String text) {
        SwingUtilities.invokeLater(
            new Runnable() {
                @Override
                public void run() {
                    chatWindow.append(text);
                }
            }
        );
    }

    //Gives user permission to type
    private void ableToType(final boolean b) {
        SwingUtilities.invokeLater(
            new Runnable() {
                @Override
                public void run() {
                    userText.setEditable(b);
                }
            }
        );
    }
}
