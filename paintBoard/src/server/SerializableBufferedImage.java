package server;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import javax.imageio.ImageIO;
import java.awt.image.WritableRaster;
import java.awt.image.ColorModel;

/**
 * Author: Dingyuan Wu 1538073
 * SerializableBufferedImage is a wrapper class around BufferedImage that allows it to be serialized.
 * This class implements the Serializable interface and handles the serialization and deserialization
 * of the BufferedImage using custom methods.
 */
public class SerializableBufferedImage implements Serializable {
    private static final long serialVersionUID = 1L;
    
    // The transient keyword prevents the image from being serialized directly
    private transient BufferedImage image;

    /**
     * Constructs a SerializableBufferedImage with specified width, height, and image type.
     * @param width the width of the image.
     * @param height the height of the image.
     * @param imageType the type of the image (e.g., BufferedImage.TYPE_INT_ARGB).
     */
    public SerializableBufferedImage(int width, int height, int imageType) {
        this.image = new BufferedImage(width, height, imageType);
    }

    /**
     * Constructs a SerializableBufferedImage from an existing BufferedImage.
     * @param image the BufferedImage to wrap.
     */
    public SerializableBufferedImage(BufferedImage image) {
        this.image = image;
    }

    /**
     * Returns the BufferedImage.
     * @return the BufferedImage.
     */
    public BufferedImage getImage() {
        return image;
    }

    /**
     * Serializes the SerializableBufferedImage to the specified ObjectOutputStream.
     * @param out the ObjectOutputStream to write the object to.
     * @throws IOException if an I/O error occurs during serialization.
     */
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject(); // Serialize non-transient fields
        ImageIO.write(image, "png", out); // Serialize the image as a PNG
    }

    /**
     * Deserializes the SerializableBufferedImage from the specified ObjectInputStream.
     * @param in the ObjectInputStream to read the object from.
     * @throws IOException if an I/O error occurs during deserialization.
     * @throws ClassNotFoundException if the class of the serialized object could not be found.
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject(); // Deserialize non-transient fields
        image = ImageIO.read(in); // Deserialize the image from the input stream
    }

    /**
     * Creates a Graphics2D context for the BufferedImage.
     * @return the Graphics2D context.
     */
    public Graphics2D createGraphics() {
        return image.createGraphics();
    }

    /**
     * Returns the width of the image.
     * @return the width of the image.
     */
    public int getWidth() {
        return image.getWidth();
    }

    /**
     * Returns the height of the image.
     * @return the height of the image.
     */
    public int getHeight() {
        return image.getHeight();
    }

    /**
     * Returns the WritableRaster of the image.
     * @return the WritableRaster of the image.
     */
    public WritableRaster getRaster() {
        return image.getRaster();
    }

    /**
     * Returns the ColorModel of the image.
     * @return the ColorModel of the image.
     */
    public ColorModel getColorModel() {
        return image.getColorModel();
    }
}
