package viewer.frontend;

import java.awt.*;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class ImagePanel extends JPanel {
    private static final long serialVersionUID = -8483797305070521030L;
    
    private Image image;
    private int width;
    private int height;

    public ImagePanel(Image image, int width, int height) {
        this.image = image;
        this.width = width;
        this.height = height;
        
        setBorder(new EmptyBorder(0, 0, 0, 0));
        setBackground(Color.WHITE);  // Set white background
        setPreferredSize(new Dimension(width, height));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (image != null) {
            Graphics2D g2d = (Graphics2D) g;
            
            // Enable anti-aliasing
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                               RenderingHints.VALUE_ANTIALIAS_ON);
            // Enable better quality rendering
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                               RenderingHints.VALUE_RENDER_QUALITY);
            // Enable better image interpolation
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                               RenderingHints.VALUE_INTERPOLATION_BICUBIC);

            g2d.drawImage(image, 0, 0, width, height, this);
        }
    }
}