package viewer.frontend;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

public class PdfViewer extends JPanel {

    private static final long serialVersionUID = 1L;
    private PDFRenderer renderer;
    private JPanel panelAllPages;
    private int numberOfPages;
    private JTextField txtPageNumber; // Page input field
    private JTextField txtInvalid; // Invalid page text field
    private JButton btnGotoPage; // Goto Page button
    private JButton btnFirstPage; // First Page button
    private JButton btnLastPage; // Last Page button

    private int width;
    private int height;

    public PdfViewer(File document) throws Exception {
        initialize(document);
    }

    private void initialize(File file) throws Exception {
        PDDocument doc = PDDocument.load(file);
        renderer = new PDFRenderer(doc);

        Float realWidth = doc.getPage(0).getMediaBox().getWidth();
        Float realHeight = doc.getPage(0).getMediaBox().getHeight();

        numberOfPages = doc.getNumberOfPages();

        width = 510;
        height = (int) (width * (realHeight / realWidth));

        setLayout(new BorderLayout());

        // Panel to hold all pages
        panelAllPages = new JPanel();
        panelAllPages.setBackground(Color.LIGHT_GRAY);
        panelAllPages.setLayout(new GridLayout(0, 1, 0, 10)); // Vertically stacked pages

        JScrollPane scrollPane = new JScrollPane(panelAllPages);
        scrollPane.setPreferredSize(new Dimension(width + 20, height * 2)); // Add scroll pane
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Increase scroll speed

        // Add a listener to update the page number when scrolling
        scrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                updateCurrentPageNumber();
            }
        });

        add(scrollPane, BorderLayout.CENTER);

        // Control panel for Goto Page and navigation buttons
        JPanel panelControls = new JPanel();
        panelControls.setBackground(new Color(255, 215, 215));
        panelControls.setLayout(new FlowLayout());

        // Smaller page number box
        txtPageNumber = new JTextField();
        txtPageNumber.setHorizontalAlignment(SwingConstants.CENTER);
        txtPageNumber.setPreferredSize(new Dimension(60, 30)); // Reduced width
        panelControls.add(txtPageNumber);

        // Add mouse listener to select text when clicked
        txtPageNumber.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                txtPageNumber.selectAll(); // Select all text in the field
            }
        });

        // Goto Page button
        btnGotoPage = new JButton("Goto Page");
        btnGotoPage.setBackground(new Color(255, 255, 255));
        btnGotoPage.setPreferredSize(new Dimension(95, 30));
        btnGotoPage.addActionListener(e -> goToPage());
        panelControls.add(btnGotoPage);

        // TextBox for "INVALID" output
        txtInvalid = new JTextField();
        txtInvalid.setHorizontalAlignment(SwingConstants.CENTER);
        txtInvalid.setPreferredSize(new Dimension(70, 30));
        txtInvalid.setEditable(false);
        panelControls.add(txtInvalid);

        // First Page button
        btnFirstPage = new JButton("First");
        btnFirstPage.setBackground(new Color(255, 255, 255));
        btnFirstPage.setPreferredSize(new Dimension(70, 30));
        btnFirstPage.addActionListener(e -> {
            int firstPageIndex = 0; // Index for the first page
            panelAllPages.scrollRectToVisible(panelAllPages.getComponent(firstPageIndex).getBounds());
            txtInvalid.setText(""); // Clear "INVALID" if page is valid
            txtPageNumber.setText(String.valueOf(firstPageIndex + 1)); // Update to current page number
        });
        panelControls.add(btnFirstPage);

        // Last Page button
        btnLastPage = new JButton("Last");
        btnLastPage.setBackground(new Color(255, 255, 255));
        btnLastPage.setPreferredSize(new Dimension(70, 30));
        btnLastPage.addActionListener(e -> {
            int lastPageIndex = numberOfPages - 1; // Index for the last page
            panelAllPages.scrollRectToVisible(panelAllPages.getComponent(lastPageIndex).getBounds());
            txtInvalid.setText(""); // Clear "INVALID" if page is valid
            txtPageNumber.setText(String.valueOf(lastPageIndex + 1)); // Update to current page number
        });
        panelControls.add(btnLastPage);

        add(panelControls, BorderLayout.SOUTH);

        // Render all pages initially
        renderAllPages();

        doc.close(); // Close the document after loading
    }

    private void renderAllPages() {
        for (int i = 0; i < numberOfPages; i++) {
            BufferedImage renderImage = null;
            try {
                renderImage = renderer.renderImage(i);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ImagePanel imagePanel = new ImagePanel(renderImage, width, height);
            imagePanel.setBorder(new EmptyBorder(0, 0, 0, 0));
            imagePanel.setPreferredSize(new Dimension(width, height));
            panelAllPages.add(imagePanel);
        }

        panelAllPages.revalidate();
        panelAllPages.repaint();
    }

    private void goToPage() {
        try {
            int pageIndex = Integer.parseInt(txtPageNumber.getText()) - 1; // Get the input and convert to index
            if (pageIndex >= 0 && pageIndex < numberOfPages) {
                panelAllPages.scrollRectToVisible(panelAllPages.getComponent(pageIndex).getBounds());
                txtInvalid.setText(""); // Clear "INVALID" if page is valid
            } else {
                txtInvalid.setText("INVALID"); // Output "INVALID" if out of range
            }
        } catch (NumberFormatException e) {
            txtInvalid.setText("INVALID"); // Handle invalid input
        }
    }

    private void updateCurrentPageNumber() {
        // Determine which page is currently visible
        for (int i = 0; i < numberOfPages; i++) {
            if (panelAllPages.getComponent(i).getBounds().intersects(panelAllPages.getVisibleRect())) {
                txtPageNumber.setText(String.valueOf(i + 1)); // Update the text field with the current page number
                break;
            }
        }
    }
}
