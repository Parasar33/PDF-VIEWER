package viewer.navbar;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import viewer.frontend.Main;

@SuppressWarnings("unused")
public class HomeMenu extends JFrame {
    private static final long serialVersionUID = 1L;
    private Navigations navigationFrame;

    public HomeMenu(Navigations nav) {
        this.navigationFrame = nav;
        getContentPane().setLayout(null);
        
        // Create gradient background
        JPanel backgroundPanel = new JPanel() {
            /**
			 * 
			 */
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

        // Welcome Panel with shadow and rounded corners
        JPanel welcomePanel = createStyledPanel();
        welcomePanel.setBounds(10, 10, 1330, 130);
        backgroundPanel.add(welcomePanel);

        // Welcome Text with shadow effect
        JLabel welcomeLabel = createStyledLabel("Welcome to PDF Opener", UIConstants.TITLE_FONT);
        welcomeLabel.setBounds(20, 20, 1290, 60);
        welcomePanel.add(welcomeLabel);

        JLabel subtitleLabel = createStyledLabel("Your Smart PDF Assistant with RAG AI", UIConstants.SUBTITLE_FONT);
        subtitleLabel.setBounds(25, 80, 1290, 30);
        welcomePanel.add(subtitleLabel);

        // Quick Start Panel
        JPanel quickStartPanel = createStyledPanel();
        quickStartPanel.setBounds(10, 150, 1330, 235);
        backgroundPanel.add(quickStartPanel);

        JLabel quickStartLabel = createStyledLabel("Quick Start Guide", UIConstants.HEADING_FONT);
        quickStartLabel.setBounds(20, 10, 300, 40);
        quickStartPanel.add(quickStartLabel);

        // Quick start steps with hover effect
        String[] steps = {
            "→ Click 'Open' in the File menu",
            "→ Select your PDF document",
            "→ Preview and interact with your document",
            "→ Use RAG AI to ask questions about your PDF",
            "→ Enjoy smart PDF analysis!"
        };

        int y = 60;
        for (String step : steps) {
            JLabel stepLabel = createAnimatedLabel(step);
            stepLabel.setBounds(30, y, 1270, 25);
            quickStartPanel.add(stepLabel);
            y += 35;
        }

        // Features Panel
        JPanel featuresPanel = createStyledPanel();
        featuresPanel.setBounds(10, 395, 1330, 195);
        backgroundPanel.add(featuresPanel);

        JLabel featuresLabel = createStyledLabel("Key Features", UIConstants.HEADING_FONT);
        featuresLabel.setBounds(20, 10, 200, 40);
        featuresPanel.add(featuresLabel);

        // Feature list with icons and hover effect
        String[] features = {
            "→ Smart PDF Analysis with RAG AI",
            "→ Interactive Question-Answering",
            "→ User-Friendly Interface",
            "→ Quick PDF Navigation"
        };

        y = 60;
        for (String feature : features) {
            JLabel featureLabel = createAnimatedLabel(feature);
            featureLabel.setBounds(30, y, 1270, 25);
            featuresPanel.add(featureLabel);
            y += 35;
        }

        // Sexy animated button
        JButton openDocButton = createStyledButton("Open a New Document");
        openDocButton.setBounds(565, 600, 250, 40);
        openDocButton.addActionListener(e -> {
            if (navigationFrame != null) {
                EventQueue.invokeLater(() -> {
                    try {
                        Main window = new Main(navigationFrame);
                        window.getFrame().setVisible(true);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
            }
        });
        backgroundPanel.add(openDocButton);

        // Footer with gradient
        JLabel footerLabel = new JLabel("Created by Parasar | Version 1.1");
        footerLabel.setFont(new Font("Segoe UI Light", Font.ITALIC, 14));
        footerLabel.setForeground(UIConstants.TEXT_PRIMARY);
        footerLabel.setBounds(10, 650, 200, 20);
        backgroundPanel.add(footerLabel);
    }

    private JPanel createStyledPanel() {
        JPanel panel = new JPanel(null) {
            /**
			 * 
			 */
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

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text) {
            /**
			 * 
			 */
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
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                super.paintComponent(g);
            }
        };
        
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setForeground(UIConstants.TEXT_PRIMARY);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }
}