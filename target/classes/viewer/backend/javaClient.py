import socket
import subprocess
import os
import platform

def receive_file(file_name):
    # Set up socket
    server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server_socket.bind(('localhost', 1234))
    server_socket.listen(1)

    print("Waiting for connection...")
    client_socket, addr = server_socket.accept()

    print("Connected by", addr)

    # Receive the file size first
    file_size = int(client_socket.recv(1024).decode('utf-8'))
    print(f"Expecting file of size: {file_size} bytes")

    # Receive the file in chunks
    file_data = b''
    while len(file_data) < file_size:
        data = client_socket.recv(1024)
        if not data:
            break
        file_data += data

    # Write the received file to disk
    with open(file_name, 'wb') as f:
        f.write(file_data)

    print(f"File received and saved as {file_name}")

    # Open the PDF file
    open_file(file_name)

    # Send a response back to the client
    client_socket.sendall(b"File received and opened successfully")

    # Close the connection
    client_socket.close()
    server_socket.close()

def open_file(file_name):
    """ Open the PDF file using the default PDF viewer. """
    if platform.system() == "Windows":
        os.startfile(file_name)  # Windows
    elif platform.system() == "Darwin":
        subprocess.call(('open', file_name))  # macOS
    else:
        subprocess.call(('xdg-open', file_name))  # Linux

if __name__ == '__main__':
    receive_file("received_file.pdf")