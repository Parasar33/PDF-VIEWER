package viewer.frontend;

import java.awt.EventQueue;
import java.awt.Color;
import java.io.File;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.Timer;
import viewer.backend.pythonClient;  // Import the pythonClient class
import java.util.Stack;

public class ViewerWithInput extends JFrame {

    private static final long serialVersionUID = 1L;
    private final JPanel javaPanel = new JPanel();
    private JLabel imageLabel;

    private String stopPath = "G:\\workspace\\Java-PDF-Viewer\\src\\res\\stop.png";
    private String loadingPath = "G:\\workspace\\Java-PDF-Viewer\\src\\res\\loading.png";
    private String searchPath = "G:\\workspace\\Java-PDF-Viewer\\src\\res\\search.png";

    private pythonClient client;  // Declare a pythonClient instance
    private Stack<String> undoStack = new Stack<>();  // Stack for undo
    private Stack<String> redoStack = new Stack<>();  // Stack for redo

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                File testFile = new File("G:\\workspace\\Java-PDF-Viewer\\src\\res\\test.pdf");  // Adjust the path as needed
                ViewerWithInput frame = new ViewerWithInput(testFile);
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public ViewerWithInput(File pdfFile) {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 892, 825);
        setLocationRelativeTo(null);
        getContentPane().setLayout(null);
        setTitle("PDF Opener");

        // Create an instance of PdfViewer and add it to javaPanel
        try {
            javaPanel.setBorder(new LineBorder(new Color(0, 0, 0), 2));
            javaPanel.setLayout(null);
            PdfViewer pdfViewer = new PdfViewer(pdfFile);  // Use the passed File object
            pdfViewer.setBounds(4, 4, 504, 774);
            pdfViewer.setPreferredSize(new java.awt.Dimension(510, 773));  // Set preferred size
            javaPanel.add(pdfViewer);
        } catch (Exception e) {
            e.printStackTrace();
        }

        javaPanel.setBounds(4, 4, 514, 780);
        getContentPane().add(javaPanel);

        // Create the pythonPanel
        JPanel pythonPanel = new JPanel();
        pythonPanel.setBorder(new LineBorder(new Color(0, 0, 0), 2));
        pythonPanel.setBackground(new Color(255, 215, 215));
        pythonPanel.setBounds(520, 4, 354, 780);
        getContentPane().add(pythonPanel);
        pythonPanel.setLayout(null);

        // Add Question Area
        JLabel questionLabel = new JLabel("Question Here:");
        questionLabel.setFont(new Font("Bradley Hand ITC", Font.BOLD, 32));
        questionLabel.setBounds(10, 317, 225, 20);
        pythonPanel.add(questionLabel);

        JTextArea questionArea = new JTextArea();
        questionArea.setLineWrap(true);
        questionArea.setWrapStyleWord(true);
        questionArea.setBounds(10, 347, 330, 91);  // Position and size
        questionArea.setBorder(new LineBorder(new Color(0, 0, 0), 2));  // Add border
        pythonPanel.add(questionArea);

        // Add Answer Area
        JLabel answerLabel = new JLabel("RAG Output:");
        answerLabel.setFont(new Font("Bradley Hand ITC", Font.BOLD, 32));
        answerLabel.setBounds(10, 491, 195, 25);
        pythonPanel.add(answerLabel);

        JTextArea answerArea = new JTextArea();
        answerArea.setLineWrap(true);
        answerArea.setWrapStyleWord(true);
        answerArea.setBounds(10, 518, 330, 253);  // Position and size
        answerArea.setBorder(new LineBorder(new Color(0, 0, 0), 2));  // Add border
        pythonPanel.add(answerArea);

        JPanel imagePanel = new JPanel();
        imagePanel.setBackground(new Color(255, 215, 215));
        imagePanel.setBorder(null);
        imagePanel.setBounds(10, 10, 330, 247);
        pythonPanel.add(imagePanel);
        imagePanel.setLayout(null);

        JLabel askAiLabel = new JLabel("<html> &nbsp;&nbsp;ASK <br> RAG AI</html>");
        askAiLabel.setHorizontalAlignment(SwingConstants.CENTER);
        askAiLabel.setFont(new Font("Times New Roman", Font.BOLD, 70));
        askAiLabel.setBounds(10, 10, 310, 227);
        imagePanel.add(askAiLabel);

        // Replaced JButton 'replace' with JLabel for images
        imageLabel = new JLabel();
        imageLabel.setBounds(290, 267, 50, 41);  // Set appropriate bounds for image display
        imageLabel.setIcon(resizeImage(stopPath, 50, 41));  // Default image is "stop", resized
        pythonPanel.add(imageLabel);

        JButton connectButton = new JButton("CONNECT TO SERVER");
        connectButton.setBackground(new Color(255, 255, 255));
        connectButton.setFont(new Font("Bradley Hand ITC", Font.BOLD, 25));
        connectButton.setBounds(10, 267, 277, 41);
        connectButton.setBorder(new LineBorder(new Color(0, 0, 0), 2));  // Add border
        pythonPanel.add(connectButton);

        // Initialize pythonClient instance
        client = new pythonClient();

        // Add logic for connecting to server on connectButton press
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                imageLabel.setIcon(resizeImage(loadingPath, 50, 41));  // Resized loading image

                // Connect to the server using the pythonClient instance
                if (client.connectToServer()) {
                    System.out.println("Connected to the Python server successfully.");
                    imageLabel.setIcon(resizeImage(searchPath, 50, 41));  // Set search icon on successful connection

                    // Send the PDF file to the server after connecting
                    client.sendFile(pdfFile.getPath());
                } else {
                    imageLabel.setIcon(resizeImage(stopPath, 50, 41));  // Revert to stop icon on failure
                }
            }
        });

        // Add clear, undo, and redo buttons
        JButton btnClear = new JButton("CLEAR");
        btnClear.setFont(new Font("Bradley Hand ITC", Font.BOLD, 25));
        btnClear.setBorder(new LineBorder(new Color(0, 0, 0), 2));
        btnClear.setBackground(Color.WHITE);
        btnClear.setBounds(231, 440, 105, 41);
        pythonPanel.add(btnClear);
        
        JButton btnUndo = new JButton("UNDO");
        btnUndo.setFont(new Font("Bradley Hand ITC", Font.BOLD, 25));
        btnUndo.setBorder(new LineBorder(new Color(0, 0, 0), 2));
        btnUndo.setBackground(Color.WHITE);
        btnUndo.setBounds(120, 440, 110, 41);
        pythonPanel.add(btnUndo);
        
        JButton btnSend = new JButton("SEND");
        btnSend.setFont(new Font("Bradley Hand ITC", Font.BOLD, 25));
        btnSend.setBorder(new LineBorder(new Color(0, 0, 0), 2));
        btnSend.setBackground(Color.WHITE);
        btnSend.setBounds(14, 440, 105, 41);
        pythonPanel.add(btnSend);
        
     // send button action
//        btnSend.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                String question = questionArea.getText();  // Get the question text
//                if (!question.isEmpty()) {
//                    String response = client.sendQuestion(question);  // Send the question to the server
//                    if (response != null) {
//                        answerArea.setText(response);  // Display the response in the answer area
//                    }
//                } else {
//                    answerArea.setText("Please enter a question.");  // Prompt for input
//                }
//            }
//        });


        // Action listener for the question area to track changes
        questionArea.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                saveState(questionArea.getText());
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                saveState(questionArea.getText());
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                saveState(questionArea.getText());
            }
        });

        // Clear button action
        btnClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                questionArea.setText("");  // Clear question area
            }
        });

        // Undo button action
     // Undo button action
        btnUndo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!undoStack.isEmpty()) {
                    String previousText = undoStack.pop();
                    questionArea.setText(previousText);
                    System.out.println("Undo to: " + previousText); // Debug line
                }
            }
        });


       
    }

    private void saveState(String currentText) {
        System.out.println("Current Text: " + currentText); // Debug line
        if (undoStack.isEmpty() || !undoStack.peek().equals(currentText)) {
            undoStack.push(currentText);
            System.out.println("Saved State: " + currentText); // Debug line
        }
    }



    private ImageIcon resizeImage(String imagePath, int width, int height) {
        ImageIcon imageIcon = new ImageIcon(imagePath);
        Image image = imageIcon.getImage();
        Image newImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(newImage);
    }
}