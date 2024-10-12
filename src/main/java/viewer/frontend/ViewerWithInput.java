package viewer.frontend;

import java.awt.EventQueue;
import java.awt.Color;
import java.io.File;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

public class ViewerWithInput extends JFrame {

    private static final long serialVersionUID = 1L;
    private final JPanel javaPanel = new JPanel();

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                // Specify the path to the test PDF file
                File testFile = new File("G:\\workspace\\Java-PDF-Viewer\\src\\res\\test.pdf"); // Adjust the path as needed
                ViewerWithInput frame = new ViewerWithInput(testFile);
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    // Constructor that accepts a PDF file
    public ViewerWithInput(File pdfFile) {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 810, 825);
        setLocationRelativeTo(null);
        getContentPane().setLayout(null);
        setTitle("PDF Opener");
        // Create an instance of PdfViewer and add it to javaPanel
        try {
            javaPanel.setBorder(new LineBorder(new Color(0, 0, 0), 2));
            javaPanel.setLayout(null);
            PdfViewer pdfViewer = new PdfViewer(pdfFile); // Use the passed File object
            pdfViewer.setBounds(4, 4, 504, 774);
            pdfViewer.setPreferredSize(new java.awt.Dimension(510, 773)); // Set preferred size
            javaPanel.add(pdfViewer);
        } catch (Exception e) {
            e.printStackTrace();
        }


        javaPanel.setBounds(4, 4, 514, 780);
        getContentPane().add(javaPanel);
        
        JPanel pythonPanel = new JPanel();
        pythonPanel.setBorder(new LineBorder(new Color(0, 0, 0), 2));
        pythonPanel.setBackground(new Color(192, 192, 192));
        pythonPanel.setBounds(520, 4, 272, 780);
        getContentPane().add(pythonPanel);
        pythonPanel.setLayout(null);
    }
}
