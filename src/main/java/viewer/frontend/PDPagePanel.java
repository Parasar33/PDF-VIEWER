package viewer.frontend;

import java.awt.*;
import javax.swing.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import java.awt.image.BufferedImage;

public class PDPagePanel extends JPanel {
    private PDDocument document;
    private PDPage page;
    private int pageIndex;
    private BufferedImage pageImage;

    public PDPagePanel(PDDocument document, PDPage page) {
        this.document = document;
        this.page = page;
        this.pageIndex = document.getPages().indexOf(page);
        setBackground(Color.WHITE);
        
        // Pre-render the page
        try {
            PDFRenderer renderer = new PDFRenderer(document);
            PDRectangle cropBox = page.getMediaBox();
            float scale = 510f / cropBox.getWidth();
            pageImage = renderer.renderImageWithDPI(pageIndex, 72 * scale);
            setPreferredSize(new Dimension(pageImage.getWidth(), pageImage.getHeight()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (pageImage != null) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                                RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                                RenderingHints.VALUE_RENDER_QUALITY);
            
            // Draw the pre-rendered image
            g2d.drawImage(pageImage, 0, 0, this);
        }
    }
}