package Server_Client;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import Client.MessageHeader;

public class Server {
	private static ServerSocket serverSocket;
	private static ArrayList<Socket> streams;
	private static ArrayList<Integer> ports;
	
	public static void main(String[] args) throws IOException {
		serverSocket = new ServerSocket(7212);
		streams = new ArrayList<Socket>();
		ports = new ArrayList<Integer>();
		while(true) {
			Socket socket = serverSocket.accept();
			System.out.println("incoming client");
			ObjectInputStream inputFromClient = new ObjectInputStream(socket.getInputStream());
			ObjectOutputStream outputToClient = new ObjectOutputStream(socket.getOutputStream());

			streams.add(socket);
			ClientHandler clientHandler = new ClientHandler(inputFromClient, outputToClient, socket);
			Thread thread = new Thread(clientHandler);
			thread.start();
		}
	}

	private static class ClientHandler implements Runnable {

		private ObjectInputStream input;
		private ObjectOutputStream output;
		private String connectedTo = null;
		private Socket thisSock;
		
		public ClientHandler(ObjectInputStream input, ObjectOutputStream output, Socket socket) {
			this.input = input;
			this.output = output;
			thisSock = socket;
		}

		@Override
		public void run(){
			int currentSize = ports.size();
			try {
				while(true) {
					MessageHeader header = (MessageHeader) input.readObject();
					if(header != MessageHeader.CONNECT)
						continue;
					System.out.println("passed connect message");
					int sPort = input.readInt();
					ports.add(sPort);
			//		input.close();
					System.out.println("sPort = " + sPort);
					for(int i = 0; i < streams.size(); i++) {
						if(streams.get(i).getPort() != thisSock.getPort() && streams.get(i).getInetAddress() != thisSock.getInetAddress()) {
							System.out.println(i);
							output.writeObject(MessageHeader.NEWCLIENT);
							output.writeObject(streams.get(i).getInetAddress());
							output.writeInt(ports.get(i));
							output.flush();
						}
					}
					System.out.println("before ENDCLIENT");
					output.writeObject(MessageHeader.ENDCLIENT);
					output.flush();
					output.close();
					System.out.println("closing stream");
					break;
				}
				return;
			} catch (Exception e) {e.printStackTrace();
				streams.remove(currentSize);
				ports.remove(currentSize);
				try {
					output.close();
				} catch (IOException e1) {}
			};
				
			
		}
	}
}
