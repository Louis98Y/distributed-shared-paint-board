package client;

import javax.swing.*;

import remote.RemoteWhiteboard;

import java.awt.*;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Author: Dingyuan Wu 1538073
 * ChatPanel class provides a GUI component for the chat functionality in the whiteboard application.
 */
public class ChatPanel extends JPanel {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private RemoteWhiteboard whiteboard;
    private String username;
    private JTextArea chatArea;
    private JTextField messageField;
    private JButton sendButton;

    /**
     * Constructor to initialize the ChatPanel with the RemoteWhiteboard instance and the username of the user.
     * @param whiteboard the remote whiteboard interface.
     * @param username the username of the current user.
     */
    public ChatPanel(RemoteWhiteboard whiteboard, String username) {
        this.whiteboard = whiteboard;
        this.username = username;

        // Set the layout of the panel
        setLayout(new BorderLayout());

        // Title label for the chat room
        JLabel titleLabel = new JLabel("Chat Room", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 25));
        titleLabel.setOpaque(true);
        titleLabel.setBackground(Color.yellow);
        add(titleLabel, BorderLayout.NORTH);

        // Text area to display chat messages
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane chatScrollPane = new JScrollPane(chatArea);

        // Text field for inputting new messages
        messageField = new JTextField();
        sendButton = new JButton("Send");
        // Add action listener to the send button
        sendButton.addActionListener(e -> sendMessage());

        // Panel to hold the message input field and send button
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        // Add components to the main panel
        add(chatScrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);
        
        // Refresh the chat area with current messages
        refresh();
    }

    /**
     * Method to send a message. It retrieves the text from the message field, sends it through the whiteboard
     * interface, and then clears the message field.
     */
    private void sendMessage() {
        String message = messageField.getText();
        if (!message.trim().isEmpty()) {
            try {
                whiteboard.sendMessage(username, message);
                messageField.setText("");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Method to refresh the chat area with the latest messages from the whiteboard.
     */
    public void refresh() {
        try {
            List<String> messages = whiteboard.getMessages();
            chatArea.setText("");
            for (String message : messages) {
                chatArea.append(message + "\n");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
