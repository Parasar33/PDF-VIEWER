package viewer.backend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

public class pythonClient {

    private String host = "127.0.0.1";  // Server IP address
    private int port = 1234;            // Server port

    private Socket clientSocket;
    private OutputStream outStream;
    private BufferedReader inStream;

    // Method to connect to the server
    public boolean connectToServer() {
        try {
            clientSocket = new Socket(host, port);
            outStream = clientSocket.getOutputStream();
            inStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            System.out.println("Connected to Python server at " + host + ":" + port);
            return true;
        } catch (IOException e) {
            System.err.println("Error connecting to server.");
            e.printStackTrace();
            return false;
        }
    }

    // Method to send the PDF file to the server
    public void sendFile(String filePath) {
        if (clientSocket == null || !clientSocket.isConnected()) {
            System.out.println("Not connected to server. Cannot send file.");
            return;
        }

        try {
            // Read PDF file into byte array
            byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));

            // Send the file size first
            PrintWriter writer = new PrintWriter(outStream, true);
            writer.println(fileBytes.length);  // Send file size as a string
            writer.flush();

            // Send the PDF file bytes
            outStream.write(fileBytes);
            outStream.flush();
            System.out.println("Sent PDF to server.");

            // Receive response from the server
            String response = inStream.readLine();
            System.out.println("Received from server: " + response);

        } catch (IOException e) {
            System.err.println("Error sending file.");
            e.printStackTrace();
        }
    }

    // Method to send question to the server and receive the answer
    public String sendQuestion(String question) {
        if (clientSocket == null || !clientSocket.isConnected()) {
            System.out.println("Not connected to server. Cannot send question.");
            return null;
        }

        try {
            PrintWriter writer = new PrintWriter(outStream, true);
            writer.println(question);  // Send the question

            // Receive the response from the server
            String response = inStream.readLine();  // Receive the answer
            return response;  // Return the answer from server

        } catch (IOException e) {
            System.err.println("Error sending question.");
            e.printStackTrace();
            return null;
        }
    }

    // Method to close the connection
    public void closeConnection() {
        try {
            if (clientSocket != null) {
                inStream.close();
                outStream.close();
                clientSocket.close();
                System.out.println("Connection closed.");
            }
        } catch (IOException e) {
            System.err.println("Error closing connection.");
            e.printStackTrace();
        }
    }
}
