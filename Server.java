import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class Server extends JFrame {

    private JTextField userText;
    private JTextArea chatWindow;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private ServerSocket server;
    private Socket connection;

    //constructor
    public Server() {
        super("Instant Messaging System Server Side");
        this.userText = new JTextField();
        //You can't type anything if you're not connected to anyone. So this is false by default.
        this.userText.setEditable(false);
        this.userText.addActionListener(
            new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    sendMessage(e.getActionCommand());
                    //When you send your message, we reset our message writing area with a blank string
                    userText.setText("");
                }
            }
        );
        add(userText, BorderLayout.SOUTH);
        this.chatWindow = new JTextArea();
        add(new JScrollPane(chatWindow));
        setSize(1000,500);
        setVisible(true);
        //setResizable(false);


    }

    //Set up and run the server
    public void startRunning() {
        try {
            server = new ServerSocket(6789, 100);

            while (true) {
                try {
                    waitForConnection();
                    setupStreams();
                    whileChatting();
                } catch (EOFException e) {
                    showMessage("\nServer connection terminated!");
                } finally {
                    closeApplication();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    //Wait for connection, then display connection information
    private void waitForConnection() throws IOException {

        showMessage("\nWaiting for someone to connect...");
        //Establishing connection
        connection = server.accept();
        showMessage("\nConnection established to " + connection.getInetAddress().getHostAddress() + "\n");
    }

    //Get stream to send and receive data
    private void setupStreams() throws IOException {
        output = new ObjectOutputStream(connection.getOutputStream());
        output.flush();
        input = new ObjectInputStream(connection.getInputStream());
        showMessage("\nStreams are now set up.");
    }

    //During the chat conversation
    private void whileChatting() throws IOException {
        String message = "You are now connected!";
        sendMessage(message);
        ableToType(true);
        do {
            //Have a conversation
            try {
                message = (String) input.readObject();
                showMessage("\n" + message);
            } catch (ClassNotFoundException e) {
                showMessage("\nCan't process this message.");
            }
        } while (!message.equals("CLIENT: END"));
    }

    //Close streams and sockets after you are done chatting
    private void closeApplication() {
        showMessage("\nClosing connections...");
        ableToType(false);
        try {
            output.close();
            input.close();
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //Send a message to client
    private void sendMessage(String message) {
        try {
            output.writeObject("SERVER: " + message);
            output.flush();
            showMessage("\nSERVER: " + message);
        } catch (IOException e) {
            chatWindow.append("\nWrong input.");
        }
    }

    //Updates chatWindow
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

    //Let the user type stuff into chat
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






























