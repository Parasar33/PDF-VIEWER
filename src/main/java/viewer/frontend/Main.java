package viewer.frontend;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.security.Security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import viewer.navbar.Navigations;
import viewer.navbar.UIConstants;

public class Main {
    private JFrame frmDocumentSigner;
    private JTextField txtDocPath;
    private File selectedFile;
    private Navigations navigationFrame;

    public Main(Navigations navigation) {
        this.navigationFrame = navigation;
        initialize();
    }

    public JFrame getFrame() {
        return frmDocumentSigner;
    }

    private void initialize() {
        Security.addProvider(new BouncyCastleProvider());

        frmDocumentSigner = new JFrame();
        frmDocumentSigner.setResizable(false);
        frmDocumentSigner.setTitle("PDF Opener");
        frmDocumentSigner.setBounds(100, 100, 745, 180);
        frmDocumentSigner.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frmDocumentSigner.setLocationRelativeTo(null);

        // Main panel with gradient background
        JPanel mainPanel = new JPanel() {
            private static final long serialVersionUID = 1L;

			@Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, UIConstants.SECONDARY_PINK, 
                                                    getWidth(), getHeight(), UIConstants.PRIMARY_PINK);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(null);
        frmDocumentSigner.setContentPane(mainPanel);

        // Content panel with rounded corners
        JPanel contentPanel = new JPanel(null) {
            private static final long serialVersionUID = 1L;

			@Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(UIConstants.PANEL_BACKGROUND);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), UIConstants.BORDER_RADIUS, UIConstants.BORDER_RADIUS);
            }
        };
        contentPanel.setBounds(10, 10, 710, 120);
        contentPanel.setOpaque(false);
        mainPanel.add(contentPanel);

        // File path components
        JLabel lblFullPath = new JLabel("Full Path:");
        lblFullPath.setFont(UIConstants.CONTENT_FONT);
        lblFullPath.setForeground(UIConstants.TEXT_PRIMARY);
        lblFullPath.setBounds(15, 15, 80, 25);
        contentPanel.add(lblFullPath);

        txtDocPath = new JTextField();
        txtDocPath.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(UIConstants.TEXT_PRIMARY, 1, true),
            BorderFactory.createEmptyBorder(2, 5, 2, 5)
        ));
        txtDocPath.setFont(UIConstants.CONTENT_FONT);
        txtDocPath.setEditable(false);
        txtDocPath.setBounds(95, 15, 420, 25);
        contentPanel.add(txtDocPath);

        // Styled buttons
        JButton btnOpenDocument = createStyledButton("Open Document");
        btnOpenDocument.setBounds(525, 15, 175, 25);
        btnOpenDocument.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter("PDF document", "pdf"));
            if (fileChooser.showOpenDialog(frmDocumentSigner) == JFileChooser.APPROVE_OPTION) {
                selectedFile = fileChooser.getSelectedFile();
                txtDocPath.setText(selectedFile.getAbsolutePath());
            }
        });
        contentPanel.add(btnOpenDocument);

        JButton btnPreviewDocument = createStyledButton("Preview Document");
        btnPreviewDocument.setBounds(15, 55, 635, 50);
        btnPreviewDocument.setFont(UIConstants.CONTENT_FONT.deriveFont(Font.BOLD, 16));
        btnPreviewDocument.addActionListener(e -> {
            if (selectedFile == null) {
                showErrorDialog("Preview file not selected!");
                return;
            }
            navigationFrame.setSelectedFile(selectedFile);
            navigationFrame.showViewer();
            frmDocumentSigner.dispose();
        });
        contentPanel.add(btnPreviewDocument);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text) {
            private static final long serialVersionUID = 1L;

			@Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2d.setColor(UIConstants.BUTTON_HOVER);
                } else if (getModel().isRollover()) {
                    g2d.setColor(UIConstants.PRIMARY_PINK);
                } else {
                    g2d.setColor(UIConstants.PANEL_BACKGROUND);
                }
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                super.paintComponent(g);
            }
        };
        
        button.setFont(UIConstants.CONTENT_FONT);
        button.setForeground(UIConstants.TEXT_PRIMARY);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(
            frmDocumentSigner,
            message,
            "Error",
            JOptionPane.ERROR_MESSAGE
        );
    }
}