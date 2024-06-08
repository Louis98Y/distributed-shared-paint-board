package client;

import javax.swing.*;

import remote.RemoteWhiteboard;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Author: Dingyuan Wu 1538073
 * UserListPanel represents a panel that displays the list of online users and allows the manager to kick out users.
 */
public class UserListPanel extends JPanel {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private RemoteWhiteboard whiteboard;
    private JList<String> userList;
    private JTextField kickOutField;
    private JButton kickOutButton;
    private boolean isManager;

    /**
     * Constructor for UserListPanel.
     * @param whiteboard The remote whiteboard interface for communication with the server.
     * @param isManager A boolean indicating if the current user is the manager.
     */
    public UserListPanel(RemoteWhiteboard whiteboard, boolean isManager) {
        this.whiteboard = whiteboard;
        this.isManager = isManager;
        
        setLayout(new BorderLayout());

        // Add label at the top
        JLabel titleLabel = new JLabel("Online Users", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 25));
        titleLabel.setOpaque(true);
        titleLabel.setBackground(Color.yellow);
        add(titleLabel, BorderLayout.NORTH);

        userList = new JList<>();
        JScrollPane userListScrollPane = new JScrollPane(userList);
        
        JPanel kickOutPanel = new JPanel(new BorderLayout());
        kickOutField = new JTextField();
        kickOutButton = new JButton("Kick Out");
        kickOutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                kickOutUser();
            }
        });
        
        kickOutPanel.add(kickOutField, BorderLayout.CENTER);
        kickOutPanel.add(kickOutButton, BorderLayout.EAST);
        kickOutPanel.setVisible(isManager);

        add(userListScrollPane, BorderLayout.CENTER);
        add(kickOutPanel, BorderLayout.SOUTH);
        
        refresh();
    }

    /**
     * Kicks out the user specified in the kickOutField.
     * This method is called when the kick out button is pressed.
     */
    private void kickOutUser() {
        String username = kickOutField.getText().trim();
        if (!username.isEmpty()) {
            try {
            	if (!whiteboard.getUserList().contains(username)) {
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, "User does not exist", "Kickout Failed", JOptionPane.WARNING_MESSAGE));
                } else if (username.equals(whiteboard.getManager())) {
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, "Cannot kick out manager", "Kickout Failed", JOptionPane.WARNING_MESSAGE));
                } else {
	                whiteboard.kickOutUser(username);
	                kickOutField.setText("");
            	}
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Refreshes the user list by fetching the latest list of online users from the server.
     */
    public void refresh() {
        try {
            List<String> users = whiteboard.getUserList();
            DefaultListModel<String> listModel = new DefaultListModel<>();
            for (String user : users) {
                listModel.addElement(user);
            }
            userList.setModel(listModel);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
