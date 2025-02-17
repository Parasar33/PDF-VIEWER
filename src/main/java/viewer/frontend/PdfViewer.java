//package viewer.frontend;
//
//import java.awt.*;
//import java.awt.event.*;
//import java.awt.image.BufferedImage;
//import java.io.File;
//import java.io.IOException;
//import javax.swing.*;
//import javax.swing.border.*;
//import org.apache.pdfbox.pdmodel.PDDocument;
//import org.apache.pdfbox.rendering.PDFRenderer;
//import viewer.navbar.UIConstants;
//
//public class PdfViewer extends JPanel {
//    private static final long serialVersionUID = 1L;
//    private PDFRenderer renderer;
//    private JPanel panelAllPages;
//    private int numberOfPages;
//    private JTextField txtPageNumber;
//    private JTextField txtInvalid;
//    private JButton btnGotoPage;
//    private JButton btnFirstPage;
//    private JButton btnLastPage;
//
//    private int width;
//    private int height;
//
//    public PdfViewer(File document) throws Exception {
//        initialize(document);
//    }
//
//    private void initialize(File file) throws Exception {
//        PDDocument doc = PDDocument.load(file);
//        renderer = new PDFRenderer(doc);
//
//        Float realWidth = doc.getPage(0).getMediaBox().getWidth();
//        Float realHeight = doc.getPage(0).getMediaBox().getHeight();
//
//        numberOfPages = doc.getNumberOfPages();
//        width = 440;  // Adjusted width
//        height = (int) (width * (realHeight / realWidth));
//
//        setLayout(new BorderLayout());
//
//        // Panel to hold all pages
//        panelAllPages = new JPanel();
//        panelAllPages.setBackground(Color.LIGHT_GRAY);
//        panelAllPages.setLayout(new GridLayout(0, 1, 0, 10));
//
//        JScrollPane scrollPane = new JScrollPane(panelAllPages);
//        scrollPane.setBorder(new LineBorder(Color.BLACK, 1));
//        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
//        add(scrollPane, BorderLayout.CENTER);
//
//        // Control panel
//        JPanel panelControls = new JPanel();
//        panelControls.setBackground(UIConstants.PANEL_BACKGROUND);
//        panelControls.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
//
//        // Page number input
//        txtPageNumber = new JTextField();
//        txtPageNumber.setHorizontalAlignment(SwingConstants.CENTER);
//        txtPageNumber.setPreferredSize(new Dimension(60, 30));
//        txtPageNumber.setBorder(BorderFactory.createCompoundBorder(
//            new LineBorder(UIConstants.TEXT_PRIMARY, 1, true),
//            BorderFactory.createEmptyBorder(2, 5, 2, 5)
//        ));
//        txtPageNumber.setFont(UIConstants.CONTENT_FONT);
//        
//        txtPageNumber.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseClicked(MouseEvent e) {
//                txtPageNumber.selectAll();
//            }
//        });
//        panelControls.add(txtPageNumber);
//
//        // Goto Page button
//        btnGotoPage = createStyledButton("Goto Page");
//        btnGotoPage.setPreferredSize(new Dimension(125, 30));
//        btnGotoPage.addActionListener(e -> goToPage());
//        panelControls.add(btnGotoPage);
//
//        // Invalid text field
//        txtInvalid = new JTextField();
//        txtInvalid.setHorizontalAlignment(SwingConstants.CENTER);
//        txtInvalid.setPreferredSize(new Dimension(70, 30));
//        txtInvalid.setEditable(false);
//        txtInvalid.setBorder(BorderFactory.createCompoundBorder(
//            new LineBorder(UIConstants.TEXT_PRIMARY, 1, true),
//            BorderFactory.createEmptyBorder(2, 5, 2, 5)
//        ));
//        panelControls.add(txtInvalid);
//
//        // Navigation buttons
//        btnFirstPage = createStyledButton("First");
//        btnFirstPage.setPreferredSize(new Dimension(70, 30));
//        btnFirstPage.addActionListener(e -> {
//            goToPageIndex(0);
//            txtInvalid.setText("");
//            txtPageNumber.setText("1");
//        });
//        panelControls.add(btnFirstPage);
//
//        btnLastPage = createStyledButton("Last");
//        btnLastPage.setPreferredSize(new Dimension(70, 30));
//        btnLastPage.addActionListener(e -> {
//            goToPageIndex(numberOfPages - 1);
//            txtInvalid.setText("");
//            txtPageNumber.setText(String.valueOf(numberOfPages));
//        });
//        panelControls.add(btnLastPage);
//
//        add(panelControls, BorderLayout.SOUTH);
//
//        // Add scroll listener
//        scrollPane.getVerticalScrollBar().addAdjustmentListener(e -> 
//            updateCurrentPageNumber());
//
//        // Render pages
//        renderAllPages();
//        doc.close();
//    }
//
//    private JButton createStyledButton(String text) {
//        JButton button = new JButton(text) {
//            private static final long serialVersionUID = 1L;
//
//			@Override
//            protected void paintComponent(Graphics g) {
//                Graphics2D g2d = (Graphics2D) g;
//                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
//                                   RenderingHints.VALUE_ANTIALIAS_ON);
//                if (getModel().isPressed()) {
//                    g2d.setColor(UIConstants.BUTTON_HOVER);
//                } else if (getModel().isRollover()) {
//                    g2d.setColor(UIConstants.PRIMARY_PINK);
//                } else {
//                    g2d.setColor(UIConstants.PANEL_BACKGROUND);
//                }
//                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
//                super.paintComponent(g);
//            }
//        };
//        
//        button.setFont(UIConstants.CONTENT_FONT);
//        button.setForeground(UIConstants.TEXT_PRIMARY);
//        button.setContentAreaFilled(false);
//        button.setBorderPainted(false);
//        button.setFocusPainted(false);
//        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
//        
//        return button;
//    }
//
//    private void renderAllPages() {
//        for (int i = 0; i < numberOfPages; i++) {
//            try {
//                BufferedImage renderImage = renderer.renderImageWithDPI(i, 300);
//                Image scaledImage = renderImage.getScaledInstance(width, height, 
//                                                                Image.SCALE_SMOOTH);
//                ImagePanel imagePanel = new ImagePanel(scaledImage, width, height);
//                panelAllPages.add(imagePanel);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        txtPageNumber.setText("1");
//    }
//
//    private void goToPage() {
//        try {
//            int pageIndex = Integer.parseInt(txtPageNumber.getText()) - 1;
//            if (pageIndex >= 0 && pageIndex < numberOfPages) {
//                goToPageIndex(pageIndex);
//                txtInvalid.setText("");
//            } else {
//                txtInvalid.setText("INVALID");
//            }
//        } catch (NumberFormatException e) {
//            txtInvalid.setText("INVALID");
//        }
//    }
//
//    private void goToPageIndex(int index) {
//        panelAllPages.scrollRectToVisible(
//            panelAllPages.getComponent(index).getBounds());
//    }
//
//    private void updateCurrentPageNumber() {
//        for (int i = 0; i < numberOfPages; i++) {
//            if (panelAllPages.getComponent(i).getBounds()
//                    .intersects(panelAllPages.getVisibleRect())) {
//                txtPageNumber.setText(String.valueOf(i + 1));
//                break;
//            }
//        }
//    }
//}


package viewer.frontend;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

public class PdfViewer extends JPanel {

    private static final long serialVersionUID = 1L;
    private PDDocument document;
    private JPanel panelAllPages;
    private int numberOfPages;
    private JTextField txtPageNumber;
    private JTextField txtInvalid;
    private JButton btnGotoPage;
    private JButton btnFirstPage;
    private JButton btnLastPage;

    private int width;
    private int height;

    public PdfViewer(File document) throws Exception {
        initialize(document);
    }

    private void initialize(File file) throws Exception {
        this.document = PDDocument.load(file);
        PDPage firstPage = this.document.getPage(0);
        
        Float realWidth = firstPage.getMediaBox().getWidth();
        Float realHeight = firstPage.getMediaBox().getHeight();

        numberOfPages = this.document.getNumberOfPages();

        width = 510;
        height = (int) (width * (realHeight / realWidth));

        setLayout(new BorderLayout());

        panelAllPages = new JPanel();
        panelAllPages.setBackground(Color.LIGHT_GRAY);
        panelAllPages.setLayout(new GridLayout(0, 1, 0, 10));

        JScrollPane scrollPane = new JScrollPane(panelAllPages);
        scrollPane.setPreferredSize(new Dimension(width + 20, height * 2));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        scrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                updateCurrentPageNumber();
            }
        });

        add(scrollPane, BorderLayout.CENTER);

        JPanel panelControls = new JPanel();
        panelControls.setBackground(new Color(255, 215, 215));
        panelControls.setLayout(new FlowLayout());

        txtPageNumber = new JTextField();
        txtPageNumber.setHorizontalAlignment(SwingConstants.CENTER);
        txtPageNumber.setPreferredSize(new Dimension(60, 30));
        panelControls.add(txtPageNumber);

        txtPageNumber.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                txtPageNumber.selectAll();
            }
        });

        btnGotoPage = new JButton("Goto Page");
        btnGotoPage.setBackground(new Color(255, 255, 255));
        btnGotoPage.setPreferredSize(new Dimension(95, 30));
        btnGotoPage.addActionListener(e -> goToPage());
        panelControls.add(btnGotoPage);

        txtInvalid = new JTextField();
        txtInvalid.setHorizontalAlignment(SwingConstants.CENTER);
        txtInvalid.setPreferredSize(new Dimension(70, 30));
        txtInvalid.setEditable(false);
        panelControls.add(txtInvalid);

        btnFirstPage = new JButton("First");
        btnFirstPage.setBackground(new Color(255, 255, 255));
        btnFirstPage.setPreferredSize(new Dimension(70, 30));
        btnFirstPage.addActionListener(e -> {
            int firstPageIndex = 0;
            panelAllPages.scrollRectToVisible(panelAllPages.getComponent(firstPageIndex).getBounds());
            txtInvalid.setText("");
            txtPageNumber.setText(String.valueOf(firstPageIndex + 1));
        });
        panelControls.add(btnFirstPage);

        btnLastPage = new JButton("Last");
        btnLastPage.setBackground(new Color(255, 255, 255));
        btnLastPage.setPreferredSize(new Dimension(70, 30));
        btnLastPage.addActionListener(e -> {
            int lastPageIndex = numberOfPages - 1;
            panelAllPages.scrollRectToVisible(panelAllPages.getComponent(lastPageIndex).getBounds());
            txtInvalid.setText("");
            txtPageNumber.setText(String.valueOf(lastPageIndex + 1));
        });
        panelControls.add(btnLastPage);

        add(panelControls, BorderLayout.SOUTH);

        // Initialize all pages with direct rendering
     // In the initialize method, where we add pages:
        for (int i = 0; i < numberOfPages; i++) {
            PDPage page = document.getPage(i);
            PDPagePanel pagePanel = new PDPagePanel(document, page);
            pagePanel.setBorder(new EmptyBorder(0, 0, 0, 0));
            
            // Wrap in a fixed size panel
            JPanel containerPanel = new JPanel(new BorderLayout());
            containerPanel.setPreferredSize(new Dimension(width, height));
            containerPanel.add(pagePanel, BorderLayout.CENTER);
            containerPanel.setBackground(Color.WHITE);
            
            panelAllPages.add(containerPanel);
        }
    }

    private void goToPage() {
        try {
            int pageIndex = Integer.parseInt(txtPageNumber.getText()) - 1;
            if (pageIndex >= 0 && pageIndex < numberOfPages) {
                panelAllPages.scrollRectToVisible(panelAllPages.getComponent(pageIndex).getBounds());
                txtInvalid.setText("");
            } else {
                txtInvalid.setText("INVALID");
            }
        } catch (NumberFormatException e) {
            txtInvalid.setText("INVALID");
        }
    }

    private void updateCurrentPageNumber() {
        for (int i = 0; i < numberOfPages; i++) {
            if (panelAllPages.getComponent(i).getBounds().intersects(panelAllPages.getVisibleRect())) {
                txtPageNumber.setText(String.valueOf(i + 1));
                break;
            }
        }
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        try {
            if (document != null) {
                document.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}