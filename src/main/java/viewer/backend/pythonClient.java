package viewer.backend;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

public class pythonClient {
    private String host = "127.0.0.1";  // Server IP address
    private int port = 1234;            // Server port

    private Socket clientSocket;
    private OutputStream outStream;
    private BufferedReader inStream;
    private Process pythonProcess;

    public boolean connectToServer() {
        int maxRetries = 5;
        int retryDelay = 1000; // 1 second
        int attempts = 0;
        
        while (attempts < maxRetries) {
            try {
                clientSocket = new Socket(host, port);
                outStream = clientSocket.getOutputStream();
                inStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                System.out.println("Connected to Python server at " + host + ":" + port);
                return true;
            } catch (IOException e) {
                attempts++;
                System.out.println("Connection attempt " + attempts + " failed. Retrying in 1 second...");
                try {
                    Thread.sleep(retryDelay);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        System.err.println("Failed to connect after " + maxRetries + " attempts.");
        return false;
    }

    public void sendFile(String filePath) {
        if (clientSocket == null || !clientSocket.isConnected()) {
            System.out.println("Not connected to server. Cannot send file.");
            return;
        }

        try {
            // Read PDF file into byte array
            byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));
            System.out.println("File size: " + fileBytes.length);

            // Send the file size first
            PrintWriter writer = new PrintWriter(outStream, true);
            writer.println(fileBytes.length);
            writer.flush();

            // Send the PDF file bytes
            outStream.write(fileBytes);
            outStream.flush();
            System.out.println("Sent PDF to server.");

            // Receive response from the server
         // In sendFile method after sending file
            String response = inStream.readLine();
            System.out.println("Server response: " + response);
            if (!response.contains("successfully")) {
                System.out.println("Error processing PDF");
                return;
            }
            System.out.println("Received from server: " + response);

        } catch (IOException e) {
            System.err.println("Error sending file.");
            e.printStackTrace();
        }
    }

    // New method to send query and get response
    public String sendQuery(String query) {
        if (clientSocket == null || !clientSocket.isConnected()) {
            return "Error: Not connected to server";
        }

        try {
            // Send query
            PrintWriter writer = new PrintWriter(outStream, true);
            writer.println(query);
            writer.flush();
            System.out.println("Query sent: " + query);

            // Read response length first
            String lengthStr = inStream.readLine();
            int responseLength = Integer.parseInt(lengthStr);

            // Read exact number of bytes for the response
            char[] buffer = new char[responseLength];
            int totalRead = 0;
            while (totalRead < responseLength) {
                int currentRead = inStream.read(buffer, totalRead, responseLength - totalRead);
                if (currentRead == -1) {
                    break;
                }
                totalRead += currentRead;
            }

            String response = new String(buffer, 0, totalRead);
            System.out.println("Response received from server");
            return response;

        } catch (IOException e) {
            System.err.println("Error in query-response communication");
            e.printStackTrace();
            return "Error: Failed to get response from server";
        }
    }

    public void disconnectFromServer() {
        try {
            if (clientSocket != null && !clientSocket.isClosed()) {
                inStream.close();
                outStream.close();
                clientSocket.close();
                System.out.println("Disconnected from Python server.");
            }
        } catch (IOException e) {
            System.err.println("Error disconnecting from server.");
            e.printStackTrace();
        }
    }

    public void stopPythonProcess() {
        if (pythonProcess != null) {
            pythonProcess.destroy();
            System.out.println("Python server process terminated.");
        }
    }
}