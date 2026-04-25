import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

class ImageEditor extends JPanel {

    private final Stack<BufferedImage> UNDO_STACK;
    private final Stack<BufferedImage> REDO_STACK;
    private final JPanel IMAGE_PANEL;
    private final JMenuBar MENU_BAR;
    private final JScrollPane SCROLL_PANE;
    private final ShortcutKeyMap SHORTCUT_KEY_MAP;
    private final ZoomMouseEventListener ZOOM_LISTENER;
    private int zoomImageIndex;

    ImageEditor() {
        this.UNDO_STACK = new Stack<>();
        this.REDO_STACK = new Stack<>();
        this.SHORTCUT_KEY_MAP = new ShortcutKeyMap(this);
        this.IMAGE_PANEL = new ImagePanel(this);
        this.SCROLL_PANE = new JScrollPane(this.IMAGE_PANEL, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.MENU_BAR = new MenuBar(this);
        this.ZOOM_LISTENER = new ZoomMouseEventListener(this, this.IMAGE_PANEL);
        this.zoomImageIndex = 0;
        this.setLayout(new BorderLayout());
        this.add(this.MENU_BAR, BorderLayout.NORTH);
        this.add(this.SCROLL_PANE, BorderLayout.CENTER);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        this.IMAGE_PANEL.repaint();
    }

    @Override
    public void revalidate() {
        super.revalidate();
        if (this.IMAGE_PANEL != null) {
            this.IMAGE_PANEL.revalidate();
        }
    }

    /**
     * Open a PPM Image file, read the data into a new BufferedImage, then return the image
     *
     * @param in - the filename of the PPM image
     */
    void readPpmImage(String in) {
        try(BufferedReader br = new BufferedReader(new FileReader(in))){
            String line;
            List<String> tokens = new ArrayList<>();

            while ((line = br.readLine()) != null) {
                boolean blank = true;
                for (int i = 0; i < line.length(); i++) {
                    char ch = line.charAt(i);
                    if (ch != ' ' && ch != '\t') {
                        blank = false;
                        break;
                    }
                }
                if (blank) {
                    continue;
                }
                int pos = 0;
                while (pos < line.length() && (line.charAt(pos) == ' ' || line.charAt(pos) == '\t')) {
                    pos++;
                }
                if (pos < line.length() && line.charAt(pos) == '#') {
                    continue;
                }

                String[] parts = line.split("\\s+");
                for (int i = 0; i < parts.length; i++) {
                    if (parts[i].length() > 0) {
                        tokens.add(parts[i]);
                    }
                }
            }
            int index = 0;

            String format = tokens.get(index++);
            if (!format.equals("P3")) {
                throw new IOException("Not a P3 PPM file");
            }

            int width = Integer.parseInt(tokens.get(index++));
            int height = Integer.parseInt(tokens.get(index++));
            int maxVal = Integer.parseInt(tokens.get(index++));

            BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int r = Integer.parseInt(tokens.get(index++));
                    int g = Integer.parseInt(tokens.get(index++));
                    int b = Integer.parseInt(tokens.get(index++));

                    Color c = new Color(r, g, b);
                    img.setRGB(x, y, c.getRGB());
                }
            }
            this.UNDO_STACK.clear();
            this.REDO_STACK.clear();
            this.zoomImageIndex = 0;
            this.addImage(img);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



    /**
     * Given a file name, this method opens the file and write out the PPM header data and then the image pixel data
     *
     * @param out - the filename that we are writing the PPM header and image data
     */
    void writePpmImage(String out) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(out))) {

            BufferedImage img = this.getImage();
            bw.write("P3");
            bw.newLine();
            bw.write(img.getWidth() + " " + img.getHeight());
            bw.newLine();
            bw.write("255");
            bw.newLine();
            for (int y = 0; y < img.getHeight(); y++) {
                for (int x = 0; x < img.getWidth(); x++) {
                    Color c = new Color(img.getRGB(x, y));

                    bw.write(c.getRed() + " " + c.getGreen() + " " + c.getBlue());

                    if (x < img.getWidth() - 1) {
                        bw.write(" ");
                    }
                }
                bw.newLine();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Adds a new image to the editor and the undo stack. It is assumed that the image
     * being passed is not zoomed. If so, use the other addImage method.
     *
     * @param img image to add.
     */
    void addImage(BufferedImage img) {
        this.UNDO_STACK.push(img);
        this.REDO_STACK.clear();
        this.revalidate();
        this.repaint();
        this.zoomImageIndex++;
    }

    /**
     * Adds a new zoomed image to the editor. Because we only want to apply transformations
     * to non-zoomed images, we need to keep track of where the last non-zoomed image is in
     * the undo stack.
     *
     * @param img    image to add.
     * @param zoomed flag indicating whether the image is zoomed. This is always true.
     */
    void addImage(BufferedImage img, boolean zoomed) {
        this.UNDO_STACK.push(img);
        this.REDO_STACK.clear();
        this.revalidate();
        this.repaint();
        if (!zoomed) {
            this.zoomImageIndex++;
        }
    }

    /**
     * Removes the current image from the editor and the undo stack.
     * The undone image is pushed to the redo stack. If there are no images
     * to undo, this method does nothing.
     */
    void undoImage() {
        if (!this.UNDO_STACK.isEmpty()) {
            this.REDO_STACK.push(this.UNDO_STACK.pop());
            this.revalidate();
            this.repaint();
        }
    }

    /**
     * Redoes the last undone image. The redone image is pushed to the undo stack.
     * If there are no images to redo, this method does nothing.
     */
    void redoImage() {
        if (!this.REDO_STACK.isEmpty()) {
            this.UNDO_STACK.push(this.REDO_STACK.pop());
            this.revalidate();
            this.repaint();
        }
    }

    Stack<BufferedImage> getUndoStack() {
        return this.UNDO_STACK;
    }

    Stack<BufferedImage> getRedoStack() {
        return this.REDO_STACK;
    }

    BufferedImage getImage() {
        return this.UNDO_STACK.isEmpty() ? null : this.UNDO_STACK.peek();
    }

    BufferedImage getOriginalImage() {
        if (this.zoomImageIndex < 1 || this.zoomImageIndex >= this.UNDO_STACK.size()) {
            return null;
        } else {
            return this.UNDO_STACK.elementAt(this.zoomImageIndex - 1);
        }
    }

    MenuBar getMenuBar() {
        return (MenuBar) MENU_BAR;
    }

    JScrollPane getScrollPane() {
        return this.SCROLL_PANE;
    }

    ZoomMouseEventListener getZoomListener() {
        return this.ZOOM_LISTENER;
    }
}
