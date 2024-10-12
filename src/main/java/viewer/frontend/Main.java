package viewer.frontend;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.security.Security;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

/*
 * Front end of the application
 * Author: RISHAV
 * Version: 1.0
 * Since: 2024-10-07
 */
public class Main {

    private JFrame frmDocumentSigner;
    private JTextField txtDocPath;
    private File selectedFile;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                Main window = new Main();
                window.frmDocumentSigner.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Create the application.
     */
    public Main() {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        Security.addProvider(new BouncyCastleProvider());

        frmDocumentSigner = new JFrame();
        frmDocumentSigner.setResizable(false);
        frmDocumentSigner.setTitle("PDF Opener");
        frmDocumentSigner.setBounds(100, 100, 608, 137);
        frmDocumentSigner.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frmDocumentSigner.setLocationRelativeTo(null);

        JPanel panelContainer = new JPanel();
        frmDocumentSigner.getContentPane().add(panelContainer, BorderLayout.CENTER);

        JButton btnOpenDocument = new JButton("Open Document");
        btnOpenDocument.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileFilter(new FileNameExtensionFilter("PDF document", "pdf"));
                if (fileChooser.showOpenDialog(frmDocumentSigner) == JFileChooser.APPROVE_OPTION) {
                    selectedFile = fileChooser.getSelectedFile();
                    txtDocPath.setText(selectedFile.getAbsolutePath());
                }
            }
        });

        JButton btnPreviewDocument = new JButton("Preview Document");
        btnPreviewDocument.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                if (selectedFile == null) {
                    JOptionPane.showMessageDialog(frmDocumentSigner, "Preview file not selected!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                EventQueue.invokeLater(() -> {
                    try {
                        new ViewerWithInput(selectedFile).setVisible(true); // Pass the selected PDF file
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        });

        txtDocPath = new JTextField();
        txtDocPath.setEditable(false);
        txtDocPath.setColumns(10);

        JLabel lblFullPath = new JLabel("Full Path:");
        GroupLayout gl_panelContainer = new GroupLayout(panelContainer);
        gl_panelContainer.setHorizontalGroup(gl_panelContainer.createParallelGroup(Alignment.TRAILING)
                .addGroup(Alignment.LEADING, gl_panelContainer.createSequentialGroup().addContainerGap()
                        .addGroup(gl_panelContainer.createParallelGroup(Alignment.LEADING)
                                .addComponent(btnPreviewDocument, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 263, Short.MAX_VALUE)
                                .addGroup(gl_panelContainer.createSequentialGroup().addComponent(lblFullPath)
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addComponent(txtDocPath, GroupLayout.DEFAULT_SIZE, 402, Short.MAX_VALUE)
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addComponent(btnOpenDocument, GroupLayout.PREFERRED_SIZE, 125, GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap()));
        gl_panelContainer.setVerticalGroup(gl_panelContainer.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_panelContainer.createSequentialGroup().addContainerGap()
                        .addGroup(gl_panelContainer.createParallelGroup(Alignment.BASELINE)
                                .addComponent(txtDocPath, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblFullPath).addComponent(btnOpenDocument))
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(btnPreviewDocument, GroupLayout.DEFAULT_SIZE, 103, Short.MAX_VALUE)
                        .addContainerGap()));
        panelContainer.setLayout(gl_panelContainer);
    }
}
