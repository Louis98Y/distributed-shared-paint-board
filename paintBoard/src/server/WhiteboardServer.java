package server;

import java.rmi.AccessException;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.io.ByteArrayInputStream;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import remote.IClient;
import remote.RemoteWhiteboard;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static client.Constants.*;

/**
 * Author: Dingyuan Wu 1538073
 * Implementation of the RemoteWhiteboard interface.
 * This class represents the server-side logic for the whiteboard application.
 */
public class WhiteboardServer extends UnicastRemoteObject implements RemoteWhiteboard {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static String serverIP;
    private static int serverPort;

    private List<String> userList;
    private List<Shape> shapes;
    private SerializableBufferedImage canvasImage;
    private List<String> messages;
    private String manager;
    private List<String> permissionRequests;
    private ConcurrentHashMap<String, IClient> userClients;

    /**
     * Constructor for the WhiteboardServer.
     * @throws RemoteException
     */
    protected WhiteboardServer() throws RemoteException {
        userList = new ArrayList<>();
        shapes = new ArrayList<>();
        canvasImage = new SerializableBufferedImage(PAINT_PANEL_WIDTH, PAINT_PANEL_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        messages = new ArrayList<>();
        permissionRequests = new ArrayList<>();
        userClients = new ConcurrentHashMap<>();
    }

    /**
     * Draws a line on the whiteboard and broadcasts the update.
     * @param x1 The x-coordinate of the start point
     * @param y1 The y-coordinate of the start point
     * @param x2 The x-coordinate of the end point
     * @param y2 The y-coordinate of the end point
     * @param color The color of the line
     * @throws RemoteException
     */
    @Override
    public synchronized void drawLine(int x1, int y1, int x2, int y2, Color color) throws RemoteException {
        shapes.add(new Line(x1, y1, x2, y2, color));
        broadcastDrawing();
    }

    /**
     * Draws a rectangle on the whiteboard and broadcasts the update.
     * @param x The x-coordinate of the top-left corner
     * @param y The y-coordinate of the top-left corner
     * @param width The width of the rectangle
     * @param height The height of the rectangle
     * @param color The color of the rectangle
     * @throws RemoteException
     */
    @Override
    public synchronized void drawRectangle(int x, int y, int width, int height, Color color) throws RemoteException {
        shapes.add(new Rectangle(x, y, width, height, color));
        broadcastDrawing();
    }

    /**
     * Draws a circle on the whiteboard and broadcasts the update.
     * @param x The x-coordinate of the center
     * @param y The y-coordinate of the center
     * @param radius The radius of the circle
     * @param color The color of the circle
     * @throws RemoteException
     */
    @Override
    public synchronized void drawCircle(int x, int y, int radius, Color color) throws RemoteException {
        shapes.add(new Circle(x, y, radius, color));
        broadcastDrawing();
    }

    /**
     * Draws an oval on the whiteboard and broadcasts the update.
     * @param x The x-coordinate of the bounding rectangle's top-left corner
     * @param y The y-coordinate of the bounding rectangle's top-left corner
     * @param width The width of the bounding rectangle
     * @param height The height of the bounding rectangle
     * @param color The color of the oval
     * @throws RemoteException
     */
    @Override
    public synchronized void drawOval(int x, int y, int width, int height, Color color) throws RemoteException {
        shapes.add(new Oval(x, y, width, height, color));
        broadcastDrawing();
    }

    /**
     * Freehand drawing on the whiteboard and broadcasts the update.
     * @param x1 The x-coordinate of the start point
     * @param y1 The y-coordinate of the start point
     * @param x2 The x-coordinate of the end point
     * @param y2 The y-coordinate of the end point
     * @param color The color of the drawing
     * @throws RemoteException
     */
    @Override
    public synchronized void freeDraw(int x1, int y1, int x2, int y2, Color color) throws RemoteException {
        shapes.add(new FreeDraw(x1, y1, x2, y2, color));
        broadcastDrawing();
    }

    /**
     * Erases part of the whiteboard and broadcasts the update.
     * @param x The x-coordinate of the erase point
     * @param y The y-coordinate of the erase point
     * @param size The size of the eraser
     * @throws RemoteException
     */
    @Override
    public synchronized void erase(int x, int y, int size) throws RemoteException {
        shapes.add(new Eraser(x, y, size));
        broadcastDrawing();
    }

    /**
     * Draws text on the whiteboard and broadcasts the update.
     * @param x The x-coordinate of the text's start point
     * @param y The y-coordinate of the text's start point
     * @param text The text to be drawn
     * @param color The color of the text
     * @throws RemoteException
     */
    @Override
    public synchronized void drawText(int x, int y, String text, Color color) throws RemoteException {
        shapes.add(new Text(x, y, text, color));
        broadcastDrawing();
    }

    /**
     * Clears all shapes from the whiteboard and broadcasts the update.
     * @throws RemoteException
     */
    @Override
    public synchronized void clearShapes() throws RemoteException {
        shapes.clear();
        for (IClient client : userClients.values()) {
            try {
                client.resetPaintPanel();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        
        canvasImage = new SerializableBufferedImage(700, 600, BufferedImage.TYPE_INT_ARGB);
        broadcastDrawing();
    }

    /**
     * Loads an image onto the whiteboard and broadcasts the update.
     * @param imageBytes The byte array of the image
     * @throws RemoteException
     */
    @Override
    public synchronized void loadImage(byte[] imageBytes) throws RemoteException {
        try {
            this.canvasImage = new SerializableBufferedImage(ImageIO.read(new ByteArrayInputStream(imageBytes)));
            shapes.clear();
            broadcastDrawing();
        } catch (IOException e) {
            throw new RemoteException("Failed to load image", e);
        }
    }

    /**
     * Returns the list of shapes on the whiteboard.
     * @return The list of shapes
     * @throws RemoteException
     */
    @Override
    public synchronized List<Shape> getShapes() throws RemoteException {
        return new ArrayList<>(shapes);
    }

    /**
     * Returns the current canvas image.
     * @return The canvas image
     * @throws RemoteException
     */
    @Override
    public synchronized SerializableBufferedImage getCanvasImage() throws RemoteException {
        return canvasImage;
    }

    /**
     * Adds a user to the whiteboard and broadcasts the update.
     * @param username The username of the user
     * @param client The client object of the user
     * @throws RemoteException
     */
    @Override
    public synchronized void addUser(String username, IClient client) throws RemoteException {
        userList.add(username);
        userClients.put(username, client);
        broadcastUserList();
    }

    /**
     * Removes a user from the whiteboard and broadcasts the update.
     * @param username The username of the user
     * @throws RemoteException
     */
    @Override
    public synchronized void removeUser(String username) throws RemoteException {
        userList.remove(username);
        userClients.remove(username);
        broadcastUserList();
    }

    /**
     * Returns the list of users on the whiteboard.
     * @return The list of users
     * @throws RemoteException
     */
    @Override
    public synchronized List<String> getUserList() throws RemoteException {
        return userList;
    }

    /**
     * Sends a message to the chat and broadcasts the update.
     * @param username The username of the sender
     * @param message The message content
     * @throws RemoteException
     */
    @Override
    public synchronized void sendMessage(String username, String message) throws RemoteException {
        messages.add(username + ": " + message);
        broadcastChat();
    }

    /**
     * Returns the list of chat messages.
     * @return The list of messages
     * @throws RemoteException
     */
    @Override
    public synchronized List<String> getMessages() throws RemoteException {
        return new ArrayList<>(messages);
    }

    /**
     * Requests permission for a user to join the whiteboard.
     * @param username The username of the user
     * @param client The client object of the user
     * @throws RemoteException
     */
    @Override
    public synchronized void requestPermission(String username, IClient client) throws RemoteException {
        if (!permissionRequests.contains(username)) {
            permissionRequests.add(username);
            IClient managerClient = userClients.get(manager);
            managerClient.notifyManager(this, username, client);
        }
    }

    /**
     * Checks if the permission request list contains a specific username.
     * @param username The username to check
     * @return True if the username is in the list, false otherwise
     */
    @Override
    public boolean permissionRequestsContains(String username) {
        return permissionRequests.contains(username);
    }

    /**
     * Grants permission for a user to join the whiteboard.
     * @param username The username of the user
     * @param client The client object of the user
     * @throws RemoteException
     */
    @Override
    public synchronized void grantPermission(String username, IClient client) throws RemoteException {
        if (permissionRequests.contains(username)) {
            addUser(username, client);
            permissionRequests.remove(username);
        }
    }

    /**
     * Denies permission for a user to join the whiteboard.
     * @param username The username of the user
     * @throws RemoteException
     */
    @Override
    public synchronized void denyPermission(String username) throws RemoteException {
        permissionRequests.remove(username);
    }

    /**
     * Kicks out a user from the whiteboard and broadcasts the update.
     * @param username The username of the user
     * @throws RemoteException
     */
    @Override
    public synchronized void kickOutUser(String username) throws RemoteException {
        if (userList.contains(username)) {
            try {
                IClient client = userClients.get(username);
                client.kickOutByManager();
                removeUser(username);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Sets the manager of the whiteboard.
     * @param managerName The username of the manager
     * @throws RemoteException
     */
    @Override
    public void setManager(String managerName) throws RemoteException {
        this.manager = managerName;
    }

    /**
     * Returns the username of the manager.
     * @return The username of the manager
     * @throws RemoteException
     */
    @Override
    public String getManager() throws RemoteException {
        return manager;
    }

    /**
     * Broadcasts the current drawing state to all clients.
     * @throws RemoteException
     */
    @Override
    public void broadcastDrawing() throws RemoteException {
        for (IClient client : userClients.values()) {
            try {
                client.refreshPaintPanel();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Broadcasts the current chat state to all clients.
     * @throws RemoteException
     */
    @Override
    public void broadcastChat() throws RemoteException {
        for (IClient client : userClients.values()) {
            try {
                client.refreshChatPanel();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Broadcasts the current user list to all clients.
     * @throws RemoteException
     */
    @Override
    public void broadcastUserList() throws RemoteException {
        for (IClient client : userClients.values()) {
            try {
                client.refreshUserListPanel();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Notifies all clients that the whiteboard is closing.
     * @throws RemoteException
     */
    @Override
    public void notifyClosure() throws RemoteException {
        for (String username : userClients.keySet()) {
            if (!username.equals(manager)) {
                IClient client = userClients.get(username);
                client.closeByManager();
                removeUser(username);
            }
        }
        removeUser(manager);
        manager = null;
    }

    /**
     * Main method to start the WhiteboardServer.
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        parseArguments(args);
        String serviceName = "Whiteboard";

        try {
            // Initialize remote object
            WhiteboardServer wbserver = new WhiteboardServer();

            // Get remote object registry
            Registry registry = LocateRegistry.createRegistry(serverPort);

            // Bind remote object
            registry.bind(serviceName, wbserver);
            System.out.println("RMI ready");

        } catch (AlreadyBoundException e) {
        	JOptionPane.showMessageDialog(null, "RMI object already bound", "AlreadyBoundException", JOptionPane.WARNING_MESSAGE);
            System.err.println("RMI object already bound");
            System.exit(1);
        } catch (AccessException e) {
        	JOptionPane.showMessageDialog(null, "RMI access fail", "AccessException", JOptionPane.WARNING_MESSAGE);
            System.err.println("AccessException: " + e.getMessage());
            System.exit(1);
        } catch (RemoteException e) {
        	JOptionPane.showMessageDialog(null, "RMI access fail", "RemoteException", JOptionPane.WARNING_MESSAGE);
            System.err.println("RemoteException: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Parses the command-line arguments.
     * @param args command-line arguments
     */
    private static void parseArguments(String args[]) {
        if (args.length < 2) {
        	JOptionPane.showMessageDialog(null, "Arguments should be <serverIPAddress> <serverPort>", "Invalid arguments", JOptionPane.WARNING_MESSAGE);
            System.exit(1);
        }
        serverIP = args[0];
        serverPort = parsePort(args[1]);
    }

    /**
     * Parses the port number from a string.
     * @param port port string
     * @return port number as int
     */
    private static int parsePort(String port) {
        int result = 6000;

        try {
            result = Integer.parseInt(port);

            if (result < 1024 || result > 65535) {
            	JOptionPane.showMessageDialog(null, "Valid port range is [1024, 65535]", "Invalid arguments", JOptionPane.WARNING_MESSAGE);
                System.exit(1);
            }
        } catch (NumberFormatException e) {
        	JOptionPane.showMessageDialog(null, "invalid port number format", "Invalid arguments", JOptionPane.WARNING_MESSAGE);
            System.exit(1);
        }

        return result;
    }
}
