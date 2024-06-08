package client;

import javax.swing.*;
import remote.RemoteWhiteboard;
import server.SerializableBufferedImage;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.rmi.RemoteException;
import java.util.List;

import static client.Constants.*;

/**
 * Author: Dingyuan Wu 1538073
 * PaintPanel represents the drawing area for the whiteboard application.
 * It allows users to draw shapes, text, and freehand, and also provides functionalities to save and open images.
 */
public class PaintPanel extends JPanel implements MouseListener, MouseMotionListener {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private RemoteWhiteboard whiteboard;
    private String toolSelected;
    private Color selectedColor;
    private int x1, y1, x2, y2;
    private SerializableBufferedImage canvas;
    private File currentFile;

    /**
     * Constructor for PaintPanel.
     * @param whiteboard The remote whiteboard interface for communication with the server.
     */
    public PaintPanel(RemoteWhiteboard whiteboard) {
    	this.whiteboard = whiteboard;
        reset();
        setBackground(Color.WHITE);
        addMouseListener(this);
        addMouseMotionListener(this);
        canvas = new SerializableBufferedImage(PAINT_PANEL_WIDTH, PAINT_PANEL_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        refresh();
    }
    
    /**
     * Reset tool selected, tool color and file.
     */
    public void reset() {
    	toolSelected = null;
    	selectedColor = Color.BLACK;
    	currentFile = null;
    }

    /**
     * Custom paint component to draw the canvas image.
     * @param g The graphics context.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(canvas.getImage(), 0, 0, null);
        if (toolSelected != null && (RECTANGLE.equals(toolSelected) || OVAL.equals(toolSelected) || LINE.equals(toolSelected) || CIRCLE.equals(toolSelected))) {
            drawCurrentShape(g);
        }
    }
    
    /**
     * Draws the current shape being dragged.
     * @param g The graphics context.
     */
    private void drawCurrentShape(Graphics g) {
        if (toolSelected == null) return;
        g.setColor(selectedColor);
        switch (toolSelected) {
	        case LINE:
	            g.drawLine(x1, y1, x2, y2);
	            break;
            case RECTANGLE:
                g.drawRect(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x2 - x1), Math.abs(y2 - y1));
                break;
            case CIRCLE:
                int radius = Math.max(Math.abs(x2 - x1), Math.abs(y2 - y1));
                int circleStartX = x1 - radius;
                int circleStartY = y1 - radius;
                g.drawOval(circleStartX, circleStartY, radius * 2, radius * 2);
                break;
            case OVAL:
                g.drawOval(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x2 - x1), Math.abs(y2 - y1));
                break;
        }
    }

    /**
     * Handles mouse press events for drawing.
     * @param e The mouse event.
     */
    @Override
    public void mousePressed(MouseEvent e) {
        x1 = e.getX();
        y1 = e.getY();
        if (toolSelected == null) return;
        if (toolSelected.equals(TEXT)) {
            String text = JOptionPane.showInputDialog("Enter text:");
            if (text != null && !text.trim().isEmpty()) {
                try {
                    whiteboard.drawText(x1, y1, text, selectedColor);
                    Graphics2D g2d = canvas.createGraphics();
                    g2d.setColor(selectedColor);
                    g2d.drawString(text, x1, y1);
                    g2d.dispose();
                    repaint();
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     * Handles mouse release events for drawing shapes.
     * @param e The mouse event.
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        x2 = e.getX();
        y2 = e.getY();
        if (toolSelected == null) return;
        if (!((toolSelected.equals(LINE)) || (toolSelected.equals(RECTANGLE)) || (toolSelected.equals(CIRCLE)) || (toolSelected.equals(OVAL)))) return;
        Graphics2D g2d = canvas.createGraphics();
        g2d.setColor(selectedColor);
        
        // Draw on remote canvas
        try {
	        switch (toolSelected) {
		        case LINE:
		        	whiteboard.drawLine(x1, y1, x2, y2, selectedColor);
		            break;
		        case RECTANGLE:
		        	whiteboard.drawRectangle(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x2 - x1), Math.abs(y2 - y1), selectedColor);
		            break;
		        case CIRCLE:
	                int radius = Math.max(Math.abs(x2 - x1), Math.abs(y2 - y1));
	                whiteboard.drawCircle(x1, y1, radius, selectedColor);
		            break;
		        case OVAL:
		        	whiteboard.drawOval(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x2 - x1), Math.abs(y2 - y1), selectedColor);
		            break;
		    }
        } catch (RemoteException e1) {
            e1.printStackTrace();
        }
        g2d.dispose();
        repaint();
    }

    /**
     * Handles mouse drag events for free drawing and erasing.
     * @param e The mouse event.
     */
    
    @Override
    public void mouseDragged(MouseEvent e) {
        if (toolSelected == null) return;
        x2 = e.getX();
        y2 = e.getY();
        
        try {
            Graphics2D g2d = canvas.createGraphics();
            if (toolSelected.equals(FREE_DRAW)) {
                whiteboard.freeDraw(x1, y1, e.getX(), e.getY(), selectedColor);
                g2d.setColor(selectedColor);
                g2d.drawLine(x1, y1, x2, y2);
                x1 = e.getX();
                y1 = e.getY();
            } else if (toolSelected.equals(SMALL_ERASER) || toolSelected.equals(MEDIUM_ERASER) || toolSelected.equals(LARGE_ERASER)) {
                int eraserSize;
                switch (toolSelected) {
                	case SMALL_ERASER:
                		eraserSize = SMALL_ERASER_SIZE;
                		break;
                	case MEDIUM_ERASER:
                		eraserSize = MEDIUM_ERASER_SIZE;
                		break;
                	case LARGE_ERASER:
                		eraserSize = LARGE_ERASER_SIZE;
                		break;
                	default:
                		eraserSize = SMALL_ERASER_SIZE;
                }
                whiteboard.erase(x2, y2, eraserSize);
                g2d.setColor(Color.WHITE);
                g2d.fillRect(x2 - eraserSize / 2, y2 - eraserSize / 2, eraserSize, eraserSize);
            }
            g2d.dispose();
            repaint();
        } catch (RemoteException e1) {
            e1.printStackTrace();
        }
    }

    // Empty implementations for other mouse events
    @Override
    public void mouseClicked(MouseEvent e) {}
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}
    @Override
    public void mouseMoved(MouseEvent e) {}

    /**
     * Sets the selected drawing tool.
     * @param toolSelected The tool to be selected.
     */
    public void setToolSelected(String toolSelected) {
        this.toolSelected = toolSelected;
    }

    /**
     * Sets the selected drawing color.
     * @param color The color to be selected.
     */
    public void setColor(Color color) {
        this.selectedColor = color;
    }

    /**
     * Refreshes the paint panel by getting the latest canvas image and shapes from the server.
     */
    public void refresh() {
        try {
            // Clear the canvas
        	Graphics2D g2d = canvas.createGraphics();
            g2d.setComposite(AlphaComposite.Clear);
            g2d.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
            g2d.setComposite(AlphaComposite.SrcOver);   
            
            SerializableBufferedImage serverCanvas = whiteboard.getCanvasImage();
            g2d.drawImage(serverCanvas.getImage(), 0, 0, null);
            List<RemoteWhiteboard.Shape> shapes = whiteboard.getShapes();
            for (RemoteWhiteboard.Shape shape : shapes) {
                shape.draw(g2d);
            }
            g2d.dispose();
            repaint();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Clears the current canvas and creates a new one.
     */
    public void newCanvas() {
        try {
            whiteboard.clearShapes();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens an image file and loads it onto the canvas.
     */
    public void openImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Open Image");
        int userSelection = fileChooser.showOpenDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToOpen = fileChooser.getSelectedFile();
            try {
                canvas = new SerializableBufferedImage(ImageIO.read(fileToOpen));
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(canvas.getImage(), "png", baos);
                byte[] imageBytes = baos.toByteArray();
                whiteboard.loadImage(imageBytes);
                currentFile = fileToOpen;
                repaint();
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to open image!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Saves the current canvas image to a file.
     */
    public void saveImage() {
        if (currentFile != null) {
            try {
                ImageIO.write(canvas.getImage(), "png", currentFile);
                JOptionPane.showMessageDialog(this, "Image saved successfully!");
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to save image!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            saveImageAs();
        }
    }

    /**
     * Saves the current canvas image to a new file specified by the user.
     */
    public void saveImageAs() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Image As");
        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            try {
                ImageIO.write(canvas.getImage(), "png", new File(fileToSave.getAbsolutePath() + ".png"));
                currentFile = fileToSave;
                JOptionPane.showMessageDialog(this, "Image saved successfully!");
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to save image!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
