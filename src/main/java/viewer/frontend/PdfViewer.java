package viewer.frontend;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

public class PdfViewer extends JPanel {

    private static final long serialVersionUID = 1L; // Added serialVersionUID
    private PDFRenderer renderer;
    private JPanel panelSelectedPage;

    private int numberOfPages;
    private int currentPageIndex = 0;

    private int width;
    private int height;

    private JTextField txtPageNumber;
    private JButton btnLastPage;
    private JButton btnNextPage;
    private JButton btnPreviousPage;
    private JButton btnFirstPage;

    public PdfViewer(File document) throws Exception {
        initialize(document);
    }

    private void enableDisableButtons(int actionIndex) {
        switch (actionIndex) {
            case 0:
                btnFirstPage.setEnabled(false);
                btnPreviousPage.setEnabled(false);
                btnNextPage.setEnabled(true);
                btnLastPage.setEnabled(true);
                break;
            case 1:
                btnFirstPage.setEnabled(true);
                btnPreviousPage.setEnabled(true);
                btnNextPage.setEnabled(false);
                btnLastPage.setEnabled(false);
                break;
            default:
                btnFirstPage.setEnabled(true);
                btnPreviousPage.setEnabled(true);
                btnNextPage.setEnabled(true);
                btnLastPage.setEnabled(true);
        }
    }

    private void selectPage(int pageIndex) {
        BufferedImage renderImage = null;

        try {
            renderImage = renderer.renderImage(pageIndex);
        } catch (IOException e) {
            e.printStackTrace();
        }

        panelSelectedPage.removeAll(); // Remove existing images

        ImagePanel imagePanel = new ImagePanel(renderImage, width, height);
        imagePanel.setBorder(new EmptyBorder(0, 0, 0, 0));
        imagePanel.setPreferredSize(new Dimension(width, height));
        panelSelectedPage.add(imagePanel, BorderLayout.CENTER);
        currentPageIndex = pageIndex;

        String pageText = String.format("Page: %d / %d", pageIndex + 1, numberOfPages);
        txtPageNumber.setText(pageText);

        if (pageIndex == 0) {
            enableDisableButtons(0);
        } else if (pageIndex == (numberOfPages - 1)) {
            enableDisableButtons(1);
        } else {
            enableDisableButtons(-1);
        }

        panelSelectedPage.revalidate();
        panelSelectedPage.repaint();
    }

    private void initialize(File file) throws Exception {
        PDDocument doc = PDDocument.load(file);
        renderer = new PDFRenderer(doc); // Create a new PDFRenderer

        // Getting/calculating screen dimensions...
        Float realWidth = doc.getPage(0).getMediaBox().getWidth();
        Float realHeight = doc.getPage(0).getMediaBox().getHeight();

        numberOfPages = doc.getNumberOfPages();

        // Set default dimensions (you can customize as needed)
        width = 510;  // Fixed width to match javaPanel
        height = (int) (width * (realHeight / realWidth)); // Maintain aspect ratio

        setLayout(new BorderLayout()); // Use BorderLayout for this JPanel

        panelSelectedPage = new JPanel();
        panelSelectedPage.setBackground(Color.LIGHT_GRAY);
        panelSelectedPage.setPreferredSize(new Dimension(width, height));
        panelSelectedPage.setLayout(new BorderLayout(0, 0));
        add(panelSelectedPage, BorderLayout.CENTER);

        // Create and set up the control panel for navigation buttons
        JPanel panelControls = new JPanel();
        panelControls.setLayout(new FlowLayout()); // Use FlowLayout for button layout

        btnFirstPage = new JButton("First Page");
        btnFirstPage.setPreferredSize(new Dimension(100, 30)); // Set smaller size
        btnFirstPage.addActionListener(event -> selectPage(0));
        panelControls.add(btnFirstPage);

        // Load images for buttons and resize
        ImageIcon prevIcon = new ImageIcon("G:\\workspace\\Java-PDF-Viewer\\src\\res\\prev.png"); // Adjust the path
        Image prevImage = prevIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH); // Resize
        btnPreviousPage = new JButton(new ImageIcon(prevImage)); // Set resized image
        btnPreviousPage.setPreferredSize(new Dimension(30, 30)); // Set smaller size
        btnPreviousPage.addActionListener(e -> {
            if (currentPageIndex > 0) {
                selectPage(currentPageIndex - 1);
            }
        });
        panelControls.add(btnPreviousPage); // Add to panelControls

        txtPageNumber = new JTextField();
        txtPageNumber.setHorizontalAlignment(SwingConstants.CENTER);
        txtPageNumber.setEditable(false);
        txtPageNumber.setPreferredSize(new Dimension(100, 30)); // Set preferred size for better appearance
        panelControls.add(txtPageNumber);

        ImageIcon nextIcon = new ImageIcon("G:\\workspace\\Java-PDF-Viewer\\src\\res\\next.png"); // Adjust the path
        Image nextImage = nextIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH); // Resize
        btnNextPage = new JButton(new ImageIcon(nextImage)); // Set resized image
        btnNextPage.setPreferredSize(new Dimension(30, 30)); // Set smaller size
        btnNextPage.addActionListener(e -> {
            if (currentPageIndex < (numberOfPages - 1)) {
                selectPage(currentPageIndex + 1);
            }
        });
        panelControls.add(btnNextPage); // Add to panelControls

        btnLastPage = new JButton("Last Page");
        btnLastPage.setPreferredSize(new Dimension(100, 30)); // Set smaller size
        btnLastPage.addActionListener(e -> selectPage(numberOfPages - 1));
        panelControls.add(btnLastPage); // Add to panelControls

        add(panelControls, BorderLayout.SOUTH); // Place controls at the bottom

        selectPage(0); // Select the first page
    }


}
