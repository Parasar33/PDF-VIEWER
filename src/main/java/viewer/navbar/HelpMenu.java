package viewer.navbar;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.net.URI;

@SuppressWarnings("unused")
public class HelpMenu extends JFrame {
    private static final long serialVersionUID = 1L;

    public HelpMenu() {
        getContentPane().setLayout(null);
        
        // Create gradient background
        JPanel backgroundPanel = new JPanel() {
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
        backgroundPanel.setLayout(null);
        backgroundPanel.setBounds(0, 0, 1366, 768);
        getContentPane().add(backgroundPanel);

        // Title with shadow effect
        JLabel titleLabel = createStyledLabel("Help & Information", UIConstants.TITLE_FONT);
        titleLabel.setBounds(10, 10, 500, 60);
        backgroundPanel.add(titleLabel);

        // Help Panel
        JPanel helpPanel = createStyledPanel();
        helpPanel.setBounds(10, 80, 1330, 300);
        backgroundPanel.add(helpPanel);

        // Instructions
        JLabel instructionsTitle = createStyledLabel("Instructions:", UIConstants.HEADING_FONT);
        instructionsTitle.setBounds(20, 10, 300, 40);
        helpPanel.add(instructionsTitle);

        String[] instructions = {
            "1. Click 'Open' in the File menu to select a PDF file",
            "2. Use 'Open Document' button to choose your PDF",
            "3. Click 'Preview Document' to view the PDF",
            "4. Use the RAG AI interface to ask questions about the PDF",
            "5. Connect to server before using AI features",
            "6. Use Home to return to the main interface"
        };

        int y = 60;
        for (String instruction : instructions) {
            JLabel label = createAnimatedLabel(instruction);
            label.setBounds(30, y, 1270, 30);
            helpPanel.add(label);
            y += 35;
        }

        // Contact Panel
        JPanel contactPanel = createStyledPanel();
        contactPanel.setBounds(10, 400, 1330, 200);
        backgroundPanel.add(contactPanel);

        // Contact Information
        JLabel contactTitle = createStyledLabel("Contact & Support:", UIConstants.HEADING_FONT);
        contactTitle.setBounds(20, 10, 300, 40);
        contactPanel.add(contactTitle);

        // GitHub Link with hover effect
        JLabel githubLabel = new JLabel("<html><a href=''>@parasar33</a></html>");
        githubLabel.setFont(UIConstants.CONTENT_FONT);
        githubLabel.setBounds(30, 60, 150, 30);
        githubLabel.setForeground(UIConstants.BUTTON_HOVER);
        githubLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        githubLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI("https://github.com/Parasar33"));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                githubLabel.setFont(githubLabel.getFont().deriveFont(Font.BOLD));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                githubLabel.setFont(githubLabel.getFont().deriveFont(Font.PLAIN));
            }
        });
        contactPanel.add(githubLabel);

        JLabel supportLabel = createAnimatedLabel("For support and bug reports, please visit the GitHub repository");
        supportLabel.setBounds(30, 100, 1270, 30);
        contactPanel.add(supportLabel);

        // Version info with gradient effect
        JLabel versionLabel = new JLabel("Version: 1.1");
        versionLabel.setFont(new Font("Segoe UI Light", Font.ITALIC, 14));
        versionLabel.setForeground(UIConstants.TEXT_PRIMARY);
        versionLabel.setBounds(10, 620, 200, 30);
        backgroundPanel.add(versionLabel);
    }

    private JPanel createStyledPanel() {
        JPanel panel = new JPanel(null) {
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
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        return panel;
    }

    private JLabel createStyledLabel(String text, Font font) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(UIConstants.TEXT_PRIMARY);
        return label;
    }

    private JLabel createAnimatedLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UIConstants.CONTENT_FONT);
        label.setForeground(UIConstants.TEXT_PRIMARY);
        
        label.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                label.setForeground(UIConstants.BUTTON_HOVER);
                label.setFont(label.getFont().deriveFont(Font.BOLD));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                label.setForeground(UIConstants.TEXT_PRIMARY);
                label.setFont(label.getFont().deriveFont(Font.PLAIN));
            }
        });
        return label;
    }
}