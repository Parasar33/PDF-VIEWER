package viewer.frontend;

import java.awt.EventQueue;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.LineBorder;

import javax.swing.JTextArea;
import javax.swing.JLabel;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.JButton;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import viewer.backend.pythonClient;
import viewer.navbar.UIConstants;

import java.util.Stack;

@SuppressWarnings("unused")
public class ViewerWithInput extends JFrame {
	private static final long serialVersionUID = 1L;

	// Panels
	public final JPanel javaPanel = new JPanel();
	public static JPanel pythonPanel;
	public static JPanel imagePanel;

	// Labels
	public static JLabel imageLabel;
	public static JLabel questionLabel;
	public static JLabel answerLabel;
	public static JLabel askAiLabel;

	// Buttons
	public static JButton connectButton;
	public static JButton btnSend;
	public static JButton btnUndo;
	public static JButton btnClear;

	// Text Areas
	public static JTextArea questionArea;
	public static JTextArea answerArea;
	public static JTextArea processesArea;

	// Paths
	// Change from file system paths to resource paths
	private String stopPath = "/stop.png";
	private String loadingPath = "/loading.png";
	private String searchPath = "/search.png";

	// Other components
	private static pythonClient client;
	private static Stack<String> undoStack = new Stack<>();
	private static File currentPdfFile; // Add this line
	public static PdfViewer pdfViewer;

	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			try {
				// Create a temporary file for the test PDF
				java.net.URL pdfResource = ViewerWithInput.class.getResource("/test.pdf");
				if (pdfResource == null) {
					throw new RuntimeException("Could not find test.pdf in resources");
				}

				File tempPdfFile = File.createTempFile("test", ".pdf");
				tempPdfFile.deleteOnExit();

				try (java.io.InputStream in = pdfResource.openStream();
						java.io.FileOutputStream out = new java.io.FileOutputStream(tempPdfFile)) {
					byte[] buffer = new byte[1024];
					int bytesRead;
					while ((bytesRead = in.read(buffer)) != -1) {
						out.write(buffer, 0, bytesRead);
					}
				}

				ViewerWithInput frame = new ViewerWithInput(tempPdfFile);
				frame.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	public ViewerWithInput(File pdfFile) {
		currentPdfFile = pdfFile;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(0, 0, 1366, 768); // Full size
		setLocationRelativeTo(null);
		getContentPane().setLayout(null);
		setTitle("PDF Opener");
		setResizable(false);

		// Initialize the GUI components
		initializeGUI(pdfFile);
		client = new pythonClient();
	}

	private void initializeGUI(File pdfFile) {
		try {
			// JavaPanel OLD setup
//            javaPanel.setBorder(new LineBorder(new Color(0, 0, 0), 2));
//            javaPanel.setLayout(null);
//            PdfViewer pdfViewer = new PdfViewer(pdfFile);
//            pdfViewer.setBounds(4, 4, 520, 690);
//            pdfViewer.setPreferredSize(new Dimension(520, 690));
//            javaPanel.add(pdfViewer);
//            javaPanel.setBounds(4, 4, 530, 700);
//            getContentPane().add(javaPanel);

			// JavaPanel NEW setup
			javaPanel.setBorder(new LineBorder(new Color(0, 0, 0), 2));
			javaPanel.setLayout(null);

			PdfViewer pdfViewer = new PdfViewer(pdfFile);
			pdfViewer.setBounds(4, 4, 520, 690);
			javaPanel.add(pdfViewer);
			javaPanel.setBounds(4, 4, 530, 700);
			getContentPane().add(javaPanel);

			// PythonPanel setup
			pythonPanel = new JPanel();
			pythonPanel.setBorder(new LineBorder(new Color(0, 0, 0), 2));
			pythonPanel.setBackground(new Color(255, 240, 245)); // Light pink background
			pythonPanel.setBounds(538, 4, 810, 700);
			pythonPanel.setLayout(null);
			getContentPane().add(pythonPanel);

			// Title with modern styling
			askAiLabel = new JLabel("ASK RAG AI");
			askAiLabel.setHorizontalAlignment(SwingConstants.CENTER);
			askAiLabel.setFont(new Font("Segoe UI", Font.BOLD, 48));
			askAiLabel.setForeground(new Color(51, 51, 51));
			askAiLabel.setBounds(30, 20, 750, 60);
			pythonPanel.add(askAiLabel);

			// Connect to Server section
			JPanel connectionPanel = new JPanel();
			connectionPanel.setLayout(null);
			connectionPanel.setBackground(new Color(255, 240, 245));
			connectionPanel.setBounds(30, 90, 750, 50);

			connectButton = createStyledButton("CONNECT TO SERVER", 300, 40);
			connectButton.setBounds(225, 5, 300, 40);
			connectButton.addActionListener(e -> {
				imageLabel.setIcon(resizeImage(loadingPath, 35, 35));
				new Thread(() -> {
					startPythonServer();
					if (client.connectToServer()) {
						SwingUtilities.invokeLater(() -> {
							imageLabel.setIcon(resizeImage(searchPath, 35, 35));
							client.sendFile(pdfFile.getPath());
							connectButton.setText("DISCONNECT");
							btnSend.setEnabled(true);
							btnUndo.setEnabled(true);
							btnClear.setEnabled(true);
							connectButton.removeActionListener(connectButton.getActionListeners()[0]);
							connectButton.addActionListener(e1 -> disconnectFromServer());
						});
					} else {
						SwingUtilities.invokeLater(() -> {
							imageLabel.setIcon(resizeImage(stopPath, 35, 35));
							btnSend.setEnabled(false);
							btnUndo.setEnabled(false);
							btnClear.setEnabled(false);
						});
					}
				}).start();
			});
			connectionPanel.add(connectButton);

			imageLabel = new JLabel(resizeImage(stopPath, 35, 35));
			imageLabel.setBounds(535, 7, 35, 35);
			connectionPanel.add(imageLabel);
			pythonPanel.add(connectionPanel);

			// Question Section
			JLabel questionLabel = new JLabel("Question Here:");
			questionLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
			questionLabel.setForeground(new Color(51, 51, 51));
			questionLabel.setBounds(30, 150, 750, 30);
			pythonPanel.add(questionLabel);

			// Question Text Area
			questionArea = new JTextArea();
			questionArea.setLineWrap(true);
			questionArea.setWrapStyleWord(true);
			questionArea.setFont(new Font("Segoe UI", Font.PLAIN, 16));
			questionArea.setMargin(new Insets(10, 10, 10, 10));

			JScrollPane questionScrollPane = new JScrollPane(questionArea);
			questionScrollPane.setBounds(30, 185, 750, 100);
			questionScrollPane.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(51, 51, 51), 1),
					BorderFactory.createEmptyBorder(5, 5, 5, 5)));
			pythonPanel.add(questionScrollPane);

			// Action Buttons Panel
			JPanel buttonPanel = new JPanel();
			buttonPanel.setLayout(null);
			buttonPanel.setBackground(new Color(255, 240, 245));
			buttonPanel.setBounds(30, 295, 750, 50);

			int buttonWidth = 180;
			int spacing = 100;
			int startX = (750 - (3 * buttonWidth + 2 * spacing)) / 2;

			btnSend = createStyledButton("SEND", buttonWidth, 40);
			btnSend.setBounds(startX, 5, buttonWidth, 40);
			btnSend.addActionListener(e -> {
				String query = questionArea.getText().trim();
				if (query.isEmpty()) {
					processesArea.setText("Please enter a question first.");
					return;
				}
				btnSend.setEnabled(false);
				processesArea.setText("QUERY PROCESSING...");
				new Thread(() -> {
					try {
						String response = client.sendQuery(query);
						SwingUtilities.invokeLater(() -> {
							if (response != null && !response.startsWith("Error")) {
								answerArea.setText(response);
								processesArea.setText("QUERY PROCESSED");
							} else {
								answerArea.setText("Failed to get response. Please try again.");
								processesArea.setText("QUERY FAILED");
							}
							btnSend.setEnabled(true);
						});
					} catch (Exception ex) {
						SwingUtilities.invokeLater(() -> {
							answerArea.setText("An error occurred: " + ex.getMessage());
							processesArea.setText("ERROR OCCURRED");
							btnSend.setEnabled(true);
						});
					}
				}).start();
			});
			buttonPanel.add(btnSend);

			btnUndo = createStyledButton("UNDO", buttonWidth, 40);
			btnUndo.setBounds(startX + buttonWidth + spacing, 5, buttonWidth, 40);
			btnUndo.addActionListener(e -> {
				if (!undoStack.isEmpty()) {
					questionArea.setText(undoStack.pop());
				}
			});
			buttonPanel.add(btnUndo);

			btnClear = createStyledButton("CLEAR", buttonWidth, 40);
			btnClear.setBounds(startX + 2 * (buttonWidth + spacing), 5, buttonWidth, 40);
			btnClear.addActionListener(e -> questionArea.setText(""));
			buttonPanel.add(btnClear);
			pythonPanel.add(buttonPanel);

			// Process Status Area
			processesArea = new JTextArea();
			processesArea.setWrapStyleWord(true);
			processesArea.setLineWrap(true);
			processesArea.setFont(new Font("Segoe UI", Font.ITALIC, 14));
			processesArea.setEditable(false);
			processesArea.setBackground(new Color(255, 250, 250));
			processesArea.setMargin(new Insets(8, 10, 8, 10));

			JScrollPane processScrollPane = new JScrollPane(processesArea);
			processScrollPane.setBounds(30, 355, 750, 45);
			processScrollPane.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(51, 51, 51), 1),
					BorderFactory.createEmptyBorder(5, 5, 5, 5)));
			pythonPanel.add(processScrollPane);

			// RAG Output Section
			JLabel outputLabel = new JLabel("RAG Output:");
			outputLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
			outputLabel.setForeground(new Color(51, 51, 51));
			outputLabel.setBounds(30, 410, 750, 30);
			pythonPanel.add(outputLabel);

			answerArea = new JTextArea();
			answerArea.setLineWrap(true);
			answerArea.setWrapStyleWord(true);
			answerArea.setFont(new Font("Segoe UI", Font.PLAIN, 16));
			answerArea.setEditable(false);
			answerArea.setMargin(new Insets(10, 10, 10, 10));

			JScrollPane answerScrollPane = new JScrollPane(answerArea);
			answerScrollPane.setBounds(30, 445, 750, 235);
			answerScrollPane.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(51, 51, 51), 1),
					BorderFactory.createEmptyBorder(5, 5, 5, 5)));
			pythonPanel.add(answerScrollPane);

			// Add document listener for question area
			questionArea.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
				public void insertUpdate(javax.swing.event.DocumentEvent e) {
					saveState(questionArea.getText());
				}

				public void removeUpdate(javax.swing.event.DocumentEvent e) {
					saveState(questionArea.getText());
				}

				public void changedUpdate(javax.swing.event.DocumentEvent e) {
					saveState(questionArea.getText());
				}
			});

			// Initially disable buttons
			btnSend.setEnabled(false);
			btnUndo.setEnabled(false);
			btnClear.setEnabled(false);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private JButton createStyledButton(String text, int width, int height) {
		JButton button = new JButton(text) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				if (getModel().isPressed()) {
					g2.setColor(new Color(230, 230, 230));
				} else if (getModel().isRollover()) {
					g2.setColor(new Color(245, 245, 245));
				} else {
					g2.setColor(Color.WHITE);
				}

				g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

				g2.setColor(new Color(51, 51, 51));
				g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);

				FontMetrics metrics = g2.getFontMetrics(getFont());
				int x = (getWidth() - metrics.stringWidth(getText())) / 2;
				int y = ((getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();

				g2.setColor(new Color(51, 51, 51));
				g2.drawString(getText(), x, y);
				g2.dispose();
			}
		};

		button.setPreferredSize(new Dimension(width, height));
		button.setFont(new Font("Segoe UI", Font.BOLD, 14));
		button.setFocusPainted(false);
		button.setBorderPainted(false);
		button.setContentAreaFilled(false);
		button.setCursor(new Cursor(Cursor.HAND_CURSOR));

		return button;
	}

	private void initializeControlComponents(JPanel pythonPanel, File pdfFile) {
		imageLabel = new JLabel();
		imageLabel.setBounds(290, 227, 50, 41);
		imageLabel.setIcon(resizeImage(stopPath, 50, 41));
		pythonPanel.add(imageLabel);

		connectButton = new JButton("CONNECT TO SERVER");
		connectButton.setBackground(new Color(255, 255, 255));
		connectButton.setFont(new Font("Bradley Hand ITC", Font.BOLD, 25));
		connectButton.setBounds(10, 227, 277, 41);
		connectButton.setBorder(new LineBorder(new Color(0, 0, 0), 2));
		pythonPanel.add(connectButton);

		btnClear = new JButton("CLEAR");
		btnClear.setFont(new Font("Bradley Hand ITC", Font.BOLD, 25));
		btnClear.setBorder(new LineBorder(new Color(0, 0, 0), 2));
		btnClear.setBackground(Color.WHITE);
		btnClear.setBounds(231, 401, 105, 41);
		pythonPanel.add(btnClear);

		btnUndo = new JButton("UNDO");
		btnUndo.setFont(new Font("Bradley Hand ITC", Font.BOLD, 25));
		btnUndo.setBorder(new LineBorder(new Color(0, 0, 0), 2));
		btnUndo.setBackground(Color.WHITE);
		btnUndo.setBounds(120, 401, 110, 41);
		pythonPanel.add(btnUndo);

		btnSend = new JButton("SEND");
		btnSend.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String query = questionArea.getText().trim();
				if (query.isEmpty()) {
					processesArea.setText("Please enter a question first.");
					return;
				}

				// Disable send button while processing
				btnSend.setEnabled(false);
				processesArea.setText("QUERY PROCESSING...");

				// Create new thread for query processing
				new Thread(() -> {
					try {
						// Send query and get response
						String response = client.sendQuery(query);

						// Update UI on EDT
						SwingUtilities.invokeLater(() -> {
							if (response != null && !response.startsWith("Error")) {
								answerArea.setText(response);
								processesArea.setText("QUERY PROCESSED");
							} else {
								answerArea.setText("Failed to get response. Please try again.");
								processesArea.setText("QUERY FAILED");
							}
							btnSend.setEnabled(true);
						});

					} catch (Exception ex) {
						SwingUtilities.invokeLater(() -> {
							answerArea.setText("An error occurred: " + ex.getMessage());
							processesArea.setText("ERROR OCCURRED");
							btnSend.setEnabled(true);
						});
					}
				}).start();
			}
		});
		btnSend.setFont(new Font("Bradley Hand ITC", Font.BOLD, 25));
		btnSend.setBorder(new LineBorder(new Color(0, 0, 0), 2));
		btnSend.setBackground(Color.WHITE);
		btnSend.setBounds(14, 401, 105, 41);
		pythonPanel.add(btnSend);

		// Initially disable the buttons
		btnSend.setEnabled(false);
		btnUndo.setEnabled(false);
		btnClear.setEnabled(false);

		// Set up button actions
		setupButtonActions(pdfFile);
	}

	private void setupButtonActions(File pdfFile) {
		connectButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				imageLabel.setIcon(resizeImage(loadingPath, 50, 41));

				// Start the server in a separate thread to not block GUI
				new Thread(() -> {
					startPythonServer();

					// Connect to the server using the pythonClient instance
					if (client.connectToServer()) {
						SwingUtilities.invokeLater(() -> {
							System.out.println("Connected to the Python server successfully.");
							imageLabel.setIcon(resizeImage(searchPath, 50, 41));

							// Send the PDF file to the server after connecting
							client.sendFile(pdfFile.getPath());

							// Update UI elements
							connectButton.setText("DISCONNECT");
							btnSend.setEnabled(true);
							btnUndo.setEnabled(true);
							btnClear.setEnabled(true);

							// Update action listener for disconnect
							connectButton.removeActionListener(connectButton.getActionListeners()[0]);
							connectButton.addActionListener(e1 -> disconnectFromServer());
						});
					} else {
						SwingUtilities.invokeLater(() -> {
							imageLabel.setIcon(resizeImage(stopPath, 50, 41));
							btnSend.setEnabled(false);
							btnUndo.setEnabled(false);
							btnClear.setEnabled(false);
							System.out.println("Connection failed.");
						});
					}
				}).start();
			}
		});

		btnClear.addActionListener(e -> {
			questionArea.setText("");
		});

		btnUndo.addActionListener(e -> {
			if (!undoStack.isEmpty()) {
				String previousText = undoStack.pop();
				questionArea.setText(previousText);
				System.out.println("Undo to: " + previousText);
			}
		});
	}

	private void startPythonServer() {
		try {
			// First, kill any existing Python processes
			killPythonProcess();

			// Extract Python script to a temporary file
			java.net.URL pythonResource = getClass().getResource("/python/pythonServer.py");
			if (pythonResource == null) {
				throw new RuntimeException("Could not find pythonServer.py in resources");
			}

			File tempFile = File.createTempFile("pythonServer", ".py");
			tempFile.deleteOnExit();

			try (java.io.InputStream in = pythonResource.openStream();
					java.io.FileOutputStream out = new java.io.FileOutputStream(tempFile)) {
				byte[] buffer = new byte[1024];
				int bytesRead;
				while ((bytesRead = in.read(buffer)) != -1) {
					out.write(buffer, 0, bytesRead);
				}
			}

			// Start the Python server using the temporary file
			ProcessBuilder processBuilder = new ProcessBuilder("python", tempFile.getAbsolutePath());
			processBuilder.redirectErrorStream(true);

			Process process = processBuilder.start();

			// Create a thread to read the Python process output
			new Thread(() -> {
				try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
					String line;
					while ((line = reader.readLine()) != null) {
						System.out.println("Python Output: " + line);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}).start();

			// Wait for server to start
			Thread.sleep(10000);

			System.out.println("Python server started successfully.");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Failed to start Python server: " + e.getMessage());
		}
	}

	@SuppressWarnings("deprecation")
	private void killPythonProcess() {
		try {
			// For Windows
			Runtime.getRuntime().exec("taskkill /F /IM python.exe");
			// For Unix-like systems
			// Runtime.getRuntime().exec("pkill -f pythonServer.py");

			// Wait a moment for the process to be killed
			Thread.sleep(1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void disconnectFromServer() {
		// First, close the client connection
		if (client != null) {
			client.disconnectFromServer();
		}

		// Kill any running Python processes
		killPythonProcess();

		// Update UI
		connectButton.setText("CONNECT TO SERVER");
		imageLabel.setIcon(resizeImage(stopPath, 50, 41));

		// Disable buttons
		btnSend.setEnabled(false);
		btnUndo.setEnabled(false);
		btnClear.setEnabled(false);
		
		//clear the question, process & answer textAreas
		questionArea.setText(null);
		answerArea.setText(null);
		processesArea.setText(null);
		
		// Reset connect button action listener
		connectButton.removeActionListener(connectButton.getActionListeners()[0]);
		setupButtonActions(currentPdfFile); // Use currentPdfFile instead of pdfFile
	}

	private void saveState(String currentText) {
		undoStack.push(currentText);
	}

	private ImageIcon resizeImage(String resourcePath, int width, int height) {
		try {
			java.net.URL imageUrl = getClass().getResource(resourcePath);
			if (imageUrl == null) {
				System.err.println("Could not find resource: " + resourcePath);
				return null;
			}
			ImageIcon icon = new ImageIcon(imageUrl);
			Image img = icon.getImage();
			Image resizedImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
			return new ImageIcon(resizedImg);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}