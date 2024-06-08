package client;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import remote.IClient;
import remote.RemoteWhiteboard;

/**
 * Author: Dingyuan Wu 1538073
 * JoinWhiteBoard class allows a client to join an existing whiteboard session.
 */
public class JoinWhiteBoard {
    
    private static String serverIP;
    private static int serverPort;
    private static String username;

    /**
     * Main method to start the process of joining an existing whiteboard session.
     * @param args input arguments
     * @throws MalformedURLException if the URL for the RMI registry is malformed.
     * @throws RemoteException if there is an error in the remote method call.
     * @throws NotBoundException if the specified name is not currently bound in the RMI registry.
     */
    public static void main(String args[]) throws MalformedURLException, RemoteException, NotBoundException {
        parseArguments(args);
        String hostName = serverIP + ":" + Integer.toString(serverPort);
        String serviceName = "Whiteboard";
        
        try {
            RemoteWhiteboard wbserver = (RemoteWhiteboard) Naming.lookup("rmi://" + hostName + "/" + serviceName);
            
            if (wbserver.getManager() == null) {
                JOptionPane.showMessageDialog(null, "No whiteboard exists, please create one", "No whiteboard", JOptionPane.WARNING_MESSAGE);
                System.exit(1);
            }
            
            if (wbserver.getUserList().contains(username)) {
                JOptionPane.showMessageDialog(null, "Username already exists", "Username exists", JOptionPane.WARNING_MESSAGE);
                System.exit(1);
            }
            
            IClient wbclient = new WhiteboardClient(username, false);
            
            // Ask for join permission
            wbserver.requestPermission(username, wbclient);
            boolean granted = false;
            while (!granted) {
                Thread.sleep(500);
                if (wbserver.getUserList().contains(username)) {
                    granted = true;
                } else if (!wbserver.permissionRequestsContains(username)) {
                    // Permission denied
                    JOptionPane.showMessageDialog(null, "Your join request has been denied by the manager.", "Join Request Denied", JOptionPane.INFORMATION_MESSAGE);
                    System.exit(0);
                }
            }
            
            // Add shutdown hook to handle cleanup on exit
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    wbserver.removeUser(username);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }));
            
            System.out.println("Creating GUI...");
            SwingUtilities.invokeLater(() -> {
                try {
                    wbclient.createGUI(wbserver);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Parses input arguments and initializes serverIP, serverPort, and username.
     * @param args input arguments
     */
    private static void parseArguments(String args[]) {
        if (args.length < 3) {
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(null, "Arguments should be <serverIPAddress> <serverPort> <username>", "Invalid arguments", JOptionPane.WARNING_MESSAGE);
                System.exit(1);
            });
            return;
        }

        serverIP = args[0];
        serverPort = parsePort(args[1]);
        username = args[2];
    }
    
    /**
     * Parses the port number from a string and ensures it is within the valid range.
     * @param port port string
     * @return port number as an integer
     */
    public static int parsePort(String port) {
        int result = 6000;

        try {
            result = Integer.parseInt(port);

            if (result < 1024 || result > 65535) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(null, "Valid port range is [1024, 65535]", "Invalid arguments", JOptionPane.WARNING_MESSAGE);
                    System.exit(1);
                });
            }
        } catch (NumberFormatException e) {
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(null, "Invalid port number format", "Invalid arguments", JOptionPane.WARNING_MESSAGE);
                System.exit(1);
            });
        }

        return result;
    }
}
