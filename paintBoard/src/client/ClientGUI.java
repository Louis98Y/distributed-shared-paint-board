package client;

import javax.swing.*;
import remote.RemoteWhiteboard;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.RemoteException;
import static client.Constants.*;

/**
 * Author: Dingyuan Wu 1538073
 * ClientGUI class provides the graphical user interface for the whiteboard client.
 */
public class ClientGUI extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private RemoteWhiteboard wbserver;
    private PaintPanel paintPanel;
    private UserListPanel userListPanel;
    private ChatPanel chatPanel;
    private String username;

    /**
     * Constructor to initialize the ClientGUI with the RemoteWhiteboard instance, username, and manager status.
     * @param wbserver the remote whiteboard interface.
     * @param username the username of the current user.
     * @param isManager a boolean indicating if the client is a manager.
     */
    public ClientGUI(RemoteWhiteboard wbserver, String username, boolean isManager) {
        this.wbserver = wbserver;
        this.username = username;

        setTitle("Whiteboard Client");
        setSize(CANVAS_WIDTH, CANVAS_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Add window listener to handle window closing event
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (isManager) {
                    closeWhiteboard();
                } else {
                    leave();
                }
            }
        });

        userListPanel = new UserListPanel(wbserver, isManager);
        paintPanel = new PaintPanel(wbserver);
        chatPanel = new ChatPanel(wbserver, username);

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(USERLIST_PANEL_WIDTH, USERLIST_PANEL_HEIGHT));
        leftPanel.add(userListPanel, BorderLayout.CENTER);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setPreferredSize(new Dimension(PAINT_PANEL_WIDTH, PAINT_PANEL_HEIGHT));
        centerPanel.add(paintPanel, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setPreferredSize(new Dimension(CHAT_PANEL_WIDTH, CHAT_PANEL_HEIGHT));
        rightPanel.add(chatPanel, BorderLayout.CENTER);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(rightPanel, BorderLayout.EAST);

        getContentPane().add(mainPanel, BorderLayout.CENTER);

        // Create Menu Bar
        JMenuBar menuBar = new JMenuBar();
        
        // Create File Menu
        JMenu fileMenu = new JMenu("File");

        // Create File Menu Items
        JMenuItem newMenuItem = new JMenuItem("New");
        JMenuItem openMenuItem = new JMenuItem("Open");
        JMenuItem saveMenuItem = new JMenuItem("Save");
        JMenuItem saveAsMenuItem = new JMenuItem("Save As");
        JMenuItem closeMenuItem = new JMenuItem("Close");

        // Add Action Listeners for Menu Items
        newMenuItem.addActionListener(e -> paintPanel.newCanvas());
        openMenuItem.addActionListener(e -> paintPanel.openImage());
        saveMenuItem.addActionListener(e -> paintPanel.saveImage());
        saveAsMenuItem.addActionListener(e -> paintPanel.saveImageAs());
        closeMenuItem.addActionListener(e -> closeWhiteboard());

        // Add Menu Items to File Menu
        fileMenu.add(newMenuItem);
        fileMenu.add(openMenuItem);
        fileMenu.add(saveMenuItem);
        fileMenu.add(saveAsMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(closeMenuItem);

        // Enable/Disable file menu items based on manager role
        fileMenu.setEnabled(isManager);
        
        // Add File Menu to Menu Bar
        menuBar.add(fileMenu);

        // Set Menu Bar to Frame
        setJMenuBar(menuBar);

        JPanel toolPanel = new JPanel();
        JButton lineButton = new JButton("Line");
        lineButton.addActionListener(e -> paintPanel.setToolSelected(LINE));
        JButton rectButton = new JButton("Rectangle");
        rectButton.addActionListener(e -> paintPanel.setToolSelected(RECTANGLE));
        JButton circleButton = new JButton("Circle");
        circleButton.addActionListener(e -> paintPanel.setToolSelected(CIRCLE));
        JButton ovalButton = new JButton("Oval");
        ovalButton.addActionListener(e -> paintPanel.setToolSelected(OVAL));
        JButton freeDrawButton = new JButton("Free Draw");
        freeDrawButton.addActionListener(e -> paintPanel.setToolSelected(FREE_DRAW));
        JButton textButton = new JButton("Text");
        textButton.addActionListener(e -> paintPanel.setToolSelected(TEXT));
        JButton smallEraserButton = new JButton("Small Eraser");
        smallEraserButton.addActionListener(e -> paintPanel.setToolSelected(SMALL_ERASER));
        JButton mediumEraserButton = new JButton("Medium Eraser");
        mediumEraserButton.addActionListener(e -> paintPanel.setToolSelected(MEDIUM_ERASER));
        JButton largeEraserButton = new JButton("Large Eraser");
        largeEraserButton.addActionListener(e -> paintPanel.setToolSelected(LARGE_ERASER));

        toolPanel.add(lineButton);
        toolPanel.add(rectButton);
        toolPanel.add(circleButton);
        toolPanel.add(ovalButton);
        toolPanel.add(freeDrawButton);
        toolPanel.add(textButton);
        toolPanel.add(smallEraserButton);
        toolPanel.add(mediumEraserButton);
        toolPanel.add(largeEraserButton);
        
        JPanel colorPanel = new JPanel();
        Color[] colors = {
                Color.RED,
                Color.BLUE,
                Color.GREEN,
                Color.YELLOW,
                Color.ORANGE,
                Color.PINK,
                Color.CYAN,
                Color.MAGENTA,
                Color.LIGHT_GRAY,
                Color.GRAY,
                Color.DARK_GRAY,
                Color.BLACK,
                new Color(165, 42, 42),     // Brown
                new Color(255, 105, 180),   // Hot Pink
                new Color(127, 255, 0),     // Chartreuse
                new Color(0, 128, 128)      // Teal
            };
        for (Color color : colors) {
            JButton colorButton = new JButton();
            colorButton.setBackground(color);
            colorButton.setPreferredSize(new Dimension(30, 30));
            colorButton.addActionListener(e -> paintPanel.setColor(color));
            colorPanel.add(colorButton);
        }

        getContentPane().add(toolPanel, BorderLayout.NORTH);
        getContentPane().add(colorPanel, BorderLayout.SOUTH);
    }

    /**
     * Displays the GUI on the Event Dispatch Thread.
     */
    public void showGUI() {
        SwingUtilities.invokeLater(() -> {
            setVisible(true);
        });
    }
    
    /**
     * Refreshes the paint panel.
     */
    public void refreshPaintPanel() {
        paintPanel.refresh();
    }
    
    /**
     * Refreshes the chat panel.
     */
    public void refreshChatPanel() {
        chatPanel.refresh();
    }
    
    /**
     * Refreshes the user list panel.
     */
    public void refreshUserListPanel() {
        userListPanel.refresh();
    }
    
    /**
     * Handles the user leaving the whiteboard.
     * Notifies the server to remove the user and then closes the application.
     */
    private void leave() {
        try {
            wbserver.removeUser(username);
        } catch (RemoteException e) {
            e.printStackTrace();
        } finally {
            dispose();
            System.exit(0);
        }
    }
    
    /**
     * Handles closing the whiteboard.
     * Notifies the server to close the whiteboard and then closes the application.
     */
    private void closeWhiteboard() {
        try {
            wbserver.notifyClosure();
        } catch (RemoteException e) {
            e.printStackTrace();
        } finally {
            dispose();
            System.exit(0);
        }
    }
    
    /**
     * Reset tool selected, tool color and file for paint panel.
     */
    public void resetPaintPanel() {
    	paintPanel.reset();
    }
}
