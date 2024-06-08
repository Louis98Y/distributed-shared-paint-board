package remote;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Author: Dingyuan Wu 1538073
 * IClient interface defines the remote methods that can be invoked on a client in the whiteboard application.
 */
public interface IClient extends Remote {

    /**
     * Refreshes the paint panel in the client's GUI.
     * @throws RemoteException if there is an error during the remote method call.
     */
    void refreshPaintPanel() throws RemoteException;

    /**
     * Refreshes the chat panel in the client's GUI.
     * @throws RemoteException if there is an error during the remote method call.
     */
    void refreshChatPanel() throws RemoteException;

    /**
     * Refreshes the user list panel in the client's GUI.
     * @throws RemoteException if there is an error during the remote method call.
     */
    void refreshUserListPanel() throws RemoteException;

    /**
     * Creates the GUI for the client using the provided RemoteWhiteboard server.
     * @param wbserver the remote whiteboard server interface.
     * @throws RemoteException if there is an error during the remote method call.
     */
    void createGUI(RemoteWhiteboard wbserver) throws RemoteException;

    /**
     * Notifies the client that they have been kicked out by the manager.
     * @throws RemoteException if there is an error during the remote method call.
     */
    void kickOutByManager() throws RemoteException;

    /**
     * Notifies the client that the whiteboard has been closed by the manager.
     * @throws RemoteException if there is an error during the remote method call.
     */
    void closeByManager() throws RemoteException;

    /**
     * Notifies the manager of a new client requesting to join the whiteboard.
     * @param server the remote whiteboard server interface.
     * @param username the username of the client requesting to join.
     * @param client the remote client interface of the client requesting to join.
     * @throws RemoteException if there is an error during the remote method call.
     */
    void notifyManager(RemoteWhiteboard server, String username, IClient client) throws RemoteException;
    
    /**
     * Reset tool selected, tool color and file for paint panel.
     */
    void resetPaintPanel() throws RemoteException;
}
