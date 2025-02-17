import socket
import os
import sys
import time
from PyPDF2 import PdfReader
from langchain.text_splitter import RecursiveCharacterTextSplitter
from langchain_community.vectorstores import FAISS  # Updated import
from langchain.chains.question_answering import load_qa_chain
import tempfile
from langchain_google_genai import GoogleGenerativeAIEmbeddings
from langchain_google_genai import ChatGoogleGenerativeAI
import google.generativeai as genai

# Google Gemini API Key
GOOGLE_API_KEY = "YOUR GEMINI API KEY HERE"
genai.configure(api_key=GOOGLE_API_KEY)

# RAG Processor Class
class RAGProcessor:
    def __init__(self):
        self.vector_store = None
        self.embeddings = None
        self.initialize_embeddings()

    def initialize_embeddings(self):
        self.embeddings = GoogleGenerativeAIEmbeddings(
            model="models/embedding-001",
            google_api_key=GOOGLE_API_KEY
        )

    def process_pdf(self):
        try:
            # Get temp directory path and construct full path to received_file.pdf
            temp_dir = tempfile.gettempdir()
            pdf_path = os.path.join(temp_dir, "received_file.pdf")
            
            if not os.path.exists(pdf_path):
                print(f"PDF not found at: {pdf_path}")
                return False

            print(f"Processing PDF from: {pdf_path}")
            
            # Read PDF
            pdf_reader = PdfReader(pdf_path)
            text = ""
            for page in pdf_reader.pages:
                text += page.extract_text()

            # Split text
            text_splitter = RecursiveCharacterTextSplitter(
                chunk_size=1000,
                chunk_overlap=200,
                length_function=len
            )
            chunks = text_splitter.split_text(text=text)

            # Create vector store
            self.vector_store = FAISS.from_texts(chunks, embedding=self.embeddings)
            return True
        except Exception as e:
            print(f"Error processing PDF: {str(e)}")
            return False

    def get_answer(self, query):
        try:
            if not self.vector_store:
                return "Error: Please process a PDF first."

            # Get relevant documents
            docs = self.vector_store.similarity_search(query=query, k=3)

            # Initialize Gemini model
            llm = ChatGoogleGenerativeAI(
                model="gemini-1.5-flash",
                google_api_key=GOOGLE_API_KEY,
                temperature=0,
            )

            # Create and run chain
            chain = load_qa_chain(llm=llm, chain_type="stuff")
            response = chain.run(input_documents=docs, question=query)

            return response
        except Exception as e:
            return f"Error generating response: {str(e)}"

# Server Functions
def handle_client(client_socket, rag_processor):
    try:
        # First receive the file size
        file_size = int(client_socket.recv(1024).decode('utf-8'))
        print(f"Python Server: Expecting file of size: {file_size} bytes", flush=True)

        # Receive the file in chunks
        file_data = b''
        total_received = 0
        while total_received < file_size:
            data = client_socket.recv(1024)
            if not data:
                print("Python Server: Connection lost or incomplete data received.", flush=True)
                break
            file_data += data
            total_received += len(data)
            print(f"Python Server: Received {total_received}/{file_size} bytes", flush=True)

        # Check if file size matches
        if total_received != file_size:
            print(f"Python Server Error: Expected {file_size} bytes but received {total_received} bytes.", flush=True)
            client_socket.sendall(b"Error: File size mismatch\n")
            return False

        # Save the received file
        temp_dir = os.path.join(tempfile.gettempdir(), "received_file.pdf")
        with open(temp_dir, 'wb') as f:
            f.write(file_data)
        print("Python Server: PDF saved successfully", flush=True)
        
        # Process PDF with RAG
        if rag_processor.process_pdf():
            response = "PDF processed successfully\n"
            client_socket.sendall(response.encode('utf-8'))
            print("Python Server: PDF processed by RAG", flush=True)
            return True
        else:
            client_socket.sendall(b"Error processing PDF\n")
            return False

    except Exception as e:
        print(f"Python Server Error: {str(e)}", flush=True)
        client_socket.sendall(f"Error: {str(e)}\n".encode('utf-8'))
        return False

def handle_queries(client_socket, rag_processor):
    try:
        while True:
            # Receive query
            query = client_socket.recv(1024).decode('utf-8').strip()
            if not query:
                break
                
            print(f"Python Server: Received query: {query}", flush=True)
            
            # Process query through RAG
            response = rag_processor.get_answer(query)
            
            # Send response length first
            response_bytes = response.encode('utf-8')
            length_prefix = f"{len(response_bytes)}\n".encode('utf-8')
            client_socket.sendall(length_prefix)
            
            # Send actual response
            client_socket.sendall(response_bytes)
            print("Python Server: Response sent", flush=True)
            
    except Exception as e:
        print(f"Python Server Error in query handling: {str(e)}", flush=True)

def start_server():
    server_socket = None
    client_socket = None
    
    try:
        # Initialize RAG processor
        rag_processor = RAGProcessor()
        
        # Set up socket
        server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        server_socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        server_socket.bind(('localhost', 1234))
        server_socket.listen(1)
        
        print("Python Server: Started and listening on port 1234", flush=True)
        
        while True:
            client_socket, addr = server_socket.accept()
            print(f"Python Server: Connected by {addr}", flush=True)
            
            # Handle PDF reception and processing
            if handle_client(client_socket, rag_processor):
                # If PDF processed successfully, start handling queries
                handle_queries(client_socket, rag_processor)
            
    except Exception as e:
        print(f"Python Server Error: {str(e)}", flush=True)
    finally:
        if client_socket:
            client_socket.close()
        if server_socket:
            server_socket.close()
        print("Python Server: Shutdown complete", flush=True)

if __name__ == '__main__':
    start_server()