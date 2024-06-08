package remote;

import java.awt.Color;
import java.awt.Graphics2D;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import server.SerializableBufferedImage;

/**
 * Author: Dingyuan Wu 1538073
 * RemoteWhiteboard interface defines the remote methods that can be invoked on a whiteboard server in the application.
 */
public interface RemoteWhiteboard extends Remote {

    // Drawing methods

    /**
     * Draws a line on the whiteboard.
     * @param x1 the x-coordinate of the start point.
     * @param y1 the y-coordinate of the start point.
     * @param x2 the x-coordinate of the end point.
     * @param y2 the y-coordinate of the end point.
     * @param color the color of the line.
     * @throws RemoteException if there is an error during the remote method call.
     */
    void drawLine(int x1, int y1, int x2, int y2, Color color) throws RemoteException;

    /**
     * Draws a rectangle on the whiteboard.
     * @param x the x-coordinate of the top-left corner.
     * @param y the y-coordinate of the top-left corner.
     * @param width the width of the rectangle.
     * @param height the height of the rectangle.
     * @param color the color of the rectangle.
     * @throws RemoteException if there is an error during the remote method call.
     */
    void drawRectangle(int x, int y, int width, int height, Color color) throws RemoteException;

    /**
     * Draws a circle on the whiteboard.
     * @param x the x-coordinate of the center.
     * @param y the y-coordinate of the center.
     * @param radius the radius of the circle.
     * @param color the color of the circle.
     * @throws RemoteException if there is an error during the remote method call.
     */
    void drawCircle(int x, int y, int radius, Color color) throws RemoteException;

    /**
     * Draws an oval on the whiteboard.
     * @param x the x-coordinate of the top-left corner.
     * @param y the y-coordinate of the top-left corner.
     * @param width the width of the oval.
     * @param height the height of the oval.
     * @param color the color of the oval.
     * @throws RemoteException if there is an error during the remote method call.
     */
    void drawOval(int x, int y, int width, int height, Color color) throws RemoteException;

    /**
     * Free draws on the whiteboard.
     * @param x1 the x-coordinate of the start point.
     * @param y1 the y-coordinate of the start point.
     * @param x2 the x-coordinate of the end point.
     * @param y2 the y-coordinate of the end point.
     * @param color the color of the drawing.
     * @throws RemoteException if there is an error during the remote method call.
     */
    void freeDraw(int x1, int y1, int x2, int y2, Color color) throws RemoteException;

    /**
     * Erases part of the whiteboard.
     * @param x the x-coordinate of the center of the eraser.
     * @param y the y-coordinate of the center of the eraser.
     * @param size the size of the eraser.
     * @throws RemoteException if there is an error during the remote method call.
     */
    void erase(int x, int y, int size) throws RemoteException;

    /**
     * Draws text on the whiteboard.
     * @param x the x-coordinate of the start point.
     * @param y the y-coordinate of the start point.
     * @param text the text to draw.
     * @param color the color of the text.
     * @throws RemoteException if there is an error during the remote method call.
     */
    void drawText(int x, int y, String text, Color color) throws RemoteException;

    /**
     * Clears all shapes from the whiteboard.
     * @throws RemoteException if there is an error during the remote method call.
     */
    void clearShapes() throws RemoteException;

    /**
     * Loads an image onto the whiteboard.
     * @param imageBytes the byte array of the image.
     * @throws RemoteException if there is an error during the remote method call.
     */
    void loadImage(byte[] imageBytes) throws RemoteException;

    // User management

    /**
     * Adds a user to the whiteboard.
     * @param username the username of the user.
     * @param client the remote client interface of the user.
     * @throws RemoteException if there is an error during the remote method call.
     */
    void addUser(String username, IClient client) throws RemoteException;

    /**
     * Removes a user from the whiteboard.
     * @param username the username of the user.
     * @throws RemoteException if there is an error during the remote method call.
     */
    void removeUser(String username) throws RemoteException;

    /**
     * Gets the list of usernames currently on the whiteboard.
     * @return the list of usernames.
     * @throws RemoteException if there is an error during the remote method call.
     */
    List<String> getUserList() throws RemoteException;

    /**
     * Gets the list of shapes currently on the whiteboard.
     * @return the list of shapes.
     * @throws RemoteException if there is an error during the remote method call.
     */
    List<Shape> getShapes() throws RemoteException;

    /**
     * Gets the current canvas image of the whiteboard.
     * @return the SerializableBufferedImage of the canvas.
     * @throws RemoteException if there is an error during the remote method call.
     */
    SerializableBufferedImage getCanvasImage() throws RemoteException;

    // Broadcast methods

    /**
     * Broadcasts the drawing updates to all clients.
     * @throws RemoteException if there is an error during the remote method call.
     */
    void broadcastDrawing() throws RemoteException;

    /**
     * Broadcasts the chat updates to all clients.
     * @throws RemoteException if there is an error during the remote method call.
     */
    void broadcastChat() throws RemoteException;

    /**
     * Broadcasts the user list updates to all clients.
     * @throws RemoteException if there is an error during the remote method call.
     */
    void broadcastUserList() throws RemoteException;

    // Manager methods

    /**
     * Sets the manager of the whiteboard.
     * @param managerName the username of the manager.
     * @throws RemoteException if there is an error during the remote method call.
     */
    void setManager(String managerName) throws RemoteException;

    /**
     * Gets the username of the manager.
     * @return the username of the manager.
     * @throws RemoteException if there is an error during the remote method call.
     */
    String getManager() throws RemoteException;

    // Chat management methods

    /**
     * Sends a message to the chat.
     * @param username the username of the sender.
     * @param message the message to send.
     * @throws RemoteException if there is an error during the remote method call.
     */
    void sendMessage(String username, String message) throws RemoteException;

    /**
     * Gets the list of messages in the chat.
     * @return the list of messages.
     * @throws RemoteException if there is an error during the remote method call.
     */
    List<String> getMessages() throws RemoteException;

    // Permission and role management methods

    /**
     * Requests permission for a user to join the whiteboard.
     * @param username the username of the user.
     * @param client the remote client interface of the user.
     * @throws RemoteException if there is an error during the remote method call.
     */
    void requestPermission(String username, IClient client) throws RemoteException;

    /**
     * Grants permission for a user to join the whiteboard.
     * @param username the username of the user.
     * @param client the remote client interface of the user.
     * @throws RemoteException if there is an error during the remote method call.
     */
    void grantPermission(String username, IClient client) throws RemoteException;

    /**
     * Denies permission for a user to join the whiteboard.
     * @param username the username of the user.
     * @throws RemoteException if there is an error during the remote method call.
     */
    void denyPermission(String username) throws RemoteException;

    /**
     * Kicks out a user from the whiteboard.
     * @param username the username of the user.
     * @throws RemoteException if there is an error during the remote method call.
     */
    void kickOutUser(String username) throws RemoteException;

    /**
     * Notifies clients about the closure of the whiteboard.
     * @throws RemoteException if there is an error during the remote method call.
     */
    void notifyClosure() throws RemoteException;

    /**
     * Checks if a permission request for a user exists.
     * @param username the username of the user.
     * @return true if the permission request exists, false otherwise.
     * @throws RemoteException if there is an error during the remote method call.
     */
    boolean permissionRequestsContains(String username) throws RemoteException;

    // Shape inner classes

    /**
     * Abstract Shape class representing a drawable shape on the whiteboard.
     */
    abstract class Shape implements java.io.Serializable {
        Color color;

        Shape(Color color) {
            this.color = color;
        }

        public abstract void draw(Graphics2D g);
    }

    /**
     * Line class representing a line shape.
     */
    class Line extends Shape {
        int x1, y1, x2, y2;

        public Line(int x1, int y1, int x2, int y2, Color color) {
            super(color);
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }

        @Override
        public void draw(Graphics2D g) {
            g.setColor(color);
            g.drawLine(x1, y1, x2, y2);
        }
    }

    /**
     * Rectangle class representing a rectangle shape.
     */
    class Rectangle extends Shape {
        int x, y, width, height;

        public Rectangle(int x, int y, int width, int height, Color color) {
            super(color);
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        @Override
        public void draw(Graphics2D g) {
            g.setColor(color);
            g.drawRect(x, y, width, height);
        }
    }

    /**
     * Circle class representing a circle shape.
     */
    class Circle extends Shape {
        int x, y, radius;

        public Circle(int x, int y, int radius, Color color) {
            super(color);
            this.x = x;
            this.y = y;
            this.radius = radius;
        }

        @Override
        public void draw(Graphics2D g) {
            g.setColor(color);
            g.drawOval(x - radius, y - radius, radius * 2, radius * 2);
        }
    }

    /**
     * Oval class representing an oval shape.
     */
    class Oval extends Shape {
        private int x, y, width, height;

        public Oval(int x, int y, int width, int height, Color color) {
            super(color);
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        @Override
        public void draw(Graphics2D g) {
            g.setColor(color);
            g.drawOval(x, y, width, height);
        }
    }

    /**
     * FreeDraw class representing a free drawing shape.
     */
    class FreeDraw extends Shape {
        int x1, y1, x2, y2;

        public FreeDraw(int x1, int y1, int x2, int y2, Color color) {
            super(color);
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }

        @Override
        public void draw(Graphics2D g) {
            g.setColor(color);
            g.drawLine(x1, y1, x2, y2);
        }
    }

    /**
     * Eraser class representing an eraser shape.
     */
    class Eraser extends Shape {
        int x, y, size;

        public Eraser(int x, int y, int size) {
            super(Color.WHITE); // Eraser uses white color
            this.x = x;
            this.y = y;
            this.size = size;
        }

        @Override
        public void draw(Graphics2D g) {
            g.setColor(color);
            g.fillRect(x - size / 2, y - size / 2, size, size);
        }
    }

    /**
     * Text class representing a text shape.
     */
    class Text extends Shape {
        int x, y;
        String text;

        public Text(int x, int y, String text, Color color) {
            super(color);
            this.x = x;
            this.y = y;
            this.text = text;
        }

        @Override
        public void draw(Graphics2D g) {
            g.setColor(color);
            g.drawString(text, x, y);
        }
    }
}
