package viewer.navbar;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
import java.io.File;

import javax.swing.*;

import viewer.frontend.Main;
import viewer.frontend.ViewerWithInput;

public class Navigations extends JFrame {
    private static final long serialVersionUID = 1L;
    
    private HomeMenu fileMenu;
    private HelpMenu helpMenu;
    private JPanel mainPanel;
    private File selectedFile;
    

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                Navigations frame = new Navigations();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public Navigations() {
        // Initialize frames
        fileMenu = new HomeMenu(this);  // Pass 'this' to HomeMenu
        helpMenu = new HelpMenu();
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1366, 768);
        setLocationRelativeTo(null);
        setTitle("PDF Opener");
        setResizable(false);
        
        mainPanel = new JPanel();
        mainPanel.setBackground(new Color(255, 205, 205));
        mainPanel.setLayout(null);
        setContentPane(mainPanel);
        
        createMenuBar();
        showContentPane("file");
        
        addWindowListener(new java.awt.event.WindowAdapter() {
            @SuppressWarnings("deprecation")
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                try {
                    Runtime.getRuntime().exec("taskkill /F /IM python.exe");
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.exit(0);
            }
        });
    }

    public void setSelectedFile(File file) {
        this.selectedFile = file;
    }

    public void showViewer() {
        if (selectedFile != null) {
            mainPanel.removeAll();
            ViewerWithInput viewer = new ViewerWithInput(selectedFile);
            Container content = viewer.getContentPane();
            
            for (Component comp : content.getComponents()) {
                mainPanel.add(comp);
            }
            
            mainPanel.revalidate();
            mainPanel.repaint();
        }
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(255, 205, 205));
        setJMenuBar(menuBar);
        
        // File Menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem homeItem = new JMenuItem("Home         ....");
        JMenuItem openItem = new JMenuItem("Open         ....");
        JMenuItem exitItem = new JMenuItem("Exit     Alt + F4");
        
        // Add action listeners
        homeItem.addActionListener(e -> showContentPane("file"));
        openItem.addActionListener(e -> {
            EventQueue.invokeLater(() -> {
                try {
                    Main window = new Main(this);
                    window.getFrame().setVisible(true);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        });
        exitItem.addActionListener(e -> System.exit(0));
        
        // Help Menu
        JMenu helpMenu = new JMenu("Help");
        JMenuItem helpMenuItem = new JMenuItem("Help Options");
        helpMenuItem.addActionListener(e -> showContentPane("help"));
        
        // Add menu items
        helpMenu.add(helpMenuItem);
        fileMenu.add(homeItem);
        fileMenu.add(openItem);
        fileMenu.add(exitItem);
        
        // Add menus to menu bar
        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
    }
    
    public void showContentPane(String destination) {
        mainPanel.removeAll();
        
        switch(destination.toLowerCase()) {
            case "file":
                fileMenu = new HomeMenu(this);  // Pass 'this' to HomeMenu
                mainPanel.setLayout(null);
                for (Component comp : fileMenu.getContentPane().getComponents()) {
                    try {
                        mainPanel.add(comp);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
                
            case "help":
                helpMenu = new HelpMenu();
                mainPanel.setLayout(null);
                for (Component comp : helpMenu.getContentPane().getComponents()) {
                    try {
                        mainPanel.add(comp);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
                
            default:
                System.out.println("Invalid destination");
                return;
        }
        
        mainPanel.revalidate();
        mainPanel.repaint();
        validate();
    }
}