package client;

import java.rmi.RemoteException;
import javax.swing.*;
import java.rmi.server.UnicastRemoteObject;
import remote.IClient;
import remote.RemoteWhiteboard;

/**
 * Author: Dingyuan Wu 1538073
 * WhiteboardClient class implements the IClient interface and represents the client-side logic for the whiteboard application.
 */
public class WhiteboardClient extends UnicastRemoteObject implements IClient {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String username;
    private Boolean isManager;
    private ClientGUI gui;

    /**
     * Constructor to initialize the WhiteboardClient with the username and manager status.
     * @param username the username of the client.
     * @param isManager a boolean indicating if the client is a manager.
     * @throws RemoteException
     */
    public WhiteboardClient(String username, Boolean isManager) throws RemoteException {
        this.username = username;
        this.isManager = isManager;
    }

    /**
     * Creates the GUI for the client.
     * @param wbserver the remote whiteboard interface.
     */
    @Override
    public void createGUI(RemoteWhiteboard wbserver) {
        gui = new ClientGUI(wbserver, username, isManager);
        gui.showGUI();
    }

    /**
     * Refreshes the paint panel in the GUI.
     * @throws RemoteException
     */
    @Override
    public void refreshPaintPanel() throws RemoteException {
        SwingUtilities.invokeLater(() -> {
            if (gui != null) {
                gui.refreshPaintPanel();
            }
        });
    }

    /**
     * Refreshes the chat panel in the GUI.
     * @throws RemoteException
     */
    @Override
    public void refreshChatPanel() throws RemoteException {
        SwingUtilities.invokeLater(() -> {
            if (gui != null) {
                gui.refreshChatPanel();
            }
        });
    }

    /**
     * Refreshes the user list panel in the GUI.
     * @throws RemoteException
     */
    @Override
    public void refreshUserListPanel() throws RemoteException {
        SwingUtilities.invokeLater(() -> {
            if (gui != null) {
                gui.refreshUserListPanel();
            }
        });
    }

    /**
     * Kicks out the client from the whiteboard, displaying a dialog and then closing the application.
     * @throws RemoteException
     */
    @Override
    public void kickOutByManager() throws RemoteException {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(null, "You have been kicked out by the manager.", "Kicked Out", JOptionPane.INFORMATION_MESSAGE);
            gui.dispose();
            System.exit(0);
        });
    }

    /**
     * Closes the whiteboard for the client, displaying a dialog and then closing the application.
     * @throws RemoteException
     */
    @Override
    public void closeByManager() throws RemoteException {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(null, "The manager has closed the whiteboard.", "Whiteboard Closed", JOptionPane.INFORMATION_MESSAGE);
            if (gui != null) {
                gui.dispose();
            }
            System.exit(0);
        });
    }

    /**
     * Notifies the manager of a permission request from another user.
     * @param server the remote whiteboard server interface.
     * @param username the username of the user requesting permission.
     * @param client the client object of the user requesting permission.
     * @throws RemoteException
     */
    @Override
    public void notifyManager(RemoteWhiteboard server, String username, IClient client) throws RemoteException {
        // Notify the manager with a dialog popup in the GUI
        SwingUtilities.invokeLater(() -> {
            int result = JOptionPane.showConfirmDialog(null,
                "User " + username + " wants to join. Do you allow?",
                "Permission Request",
                JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                try {
                    server.grantPermission(username, client);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    server.denyPermission(username);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Reset tool selected, tool color and file for paint panel.
     */
	@Override
	public void resetPaintPanel() throws RemoteException {
		if (gui != null) {
			gui.resetPaintPanel();
		}
	}
}
