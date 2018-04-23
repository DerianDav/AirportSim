package Client;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javafx.concurrent.Task;


public class ClientServer {
	private static final String startingAddress = "localhost";
	private static final int startServerSocket = 7212;
	
	private Socket socket;
	private ObjectOutputStream outputToServer;
	private ObjectInputStream inputFromServer;
	private static ServerSocket serverSocket;
	private static ArrayList<ObjectOutputStream> streams = new ArrayList<ObjectOutputStream>();
	
	Airport airport;
	public ClientServer(Airport airport) {
		this.airport = airport;
		try {
			serverSocket = new ServerSocket(0);
		} catch (IOException e) {}
		openConnection();
		localServerListener lsl = new localServerListener();
		Thread thread = new Thread(lsl);
		thread.start();
	}

	private void openConnection() {
		// Our server is on our computer, but make sure to use the same port.
		try {
			socket = new Socket(startingAddress, startServerSocket);
			System.out.println("This port == " + socket.getLocalPort());
			System.out.println("Server Local Port == " + serverSocket.getLocalPort());
			outputToServer = new ObjectOutputStream(socket.getOutputStream());
			inputFromServer = new ObjectInputStream(socket.getInputStream());

			outputToServer.writeObject(MessageHeader.CONNECT);
			outputToServer.writeInt(serverSocket.getLocalPort());
			outputToServer.flush();
			System.out.println("finished connection output");
			//SeverListener will have a while(true) loop
			ServerListener listener = new ServerListener(inputFromServer);
			
			// Note: Need setDaemon when started with a JavaFX App, or it crashes.
			Thread thread = new Thread(listener);
			thread.setDaemon(true);
			thread.start();

			

		} catch (IOException e) {e.printStackTrace();
		}
	}
	
	//listens for address coming from the server
	private class ServerListener extends Task<Object>{
		private ObjectInputStream input;
		
		public ServerListener(ObjectInputStream input) {
			this.input = input;
		}
		
		@Override
		public void run() {
			try {
				while(true) {
					MessageHeader header = (MessageHeader) input.readObject();
					switch(header){
						case NEWCLIENT:
							System.out.println("recv");
							InetAddress addr = (InetAddress)input.readObject();
							System.out.println("got addr");
							int port = input.readInt();
							System.out.println(port);
							
							newClient(addr,port);
							System.out.println("continue");
							continue;
						case ENDCLIENT:
							System.out.println("break");
							return;
					default:
						continue;
					}
				}
			}catch(Exception e) {e.printStackTrace();}
		}

		@Override
		protected Object call() throws Exception {
			return null;}
	}
	
	/**
	 * establishes connection with the new client
	 * @param addr - address of the client
	 * @param port - port number of the client
	 */
	private void newClient(InetAddress addr, int port) throws IOException {
		Socket newSock = new Socket(addr, port);
		ObjectOutputStream output = new ObjectOutputStream(newSock.getOutputStream());
		ObjectInputStream input = new ObjectInputStream(newSock.getInputStream());
		streams.add(output);
		ServerHandler serverHandler = new ServerHandler(input, output);
		Thread thread = new Thread(serverHandler);
		thread.start();
		
	}
	
	//-------------------------------------SERVER--------------------------------------------------
	// handles messages from other clients
	
	private static class localServerListener implements Runnable {
		@Override
		public void run() {
			try {
				while(true) {
					Socket socket = serverSocket.accept();
					System.out.println("client connected to server");
					ObjectInputStream inputFromClient = new ObjectInputStream(socket.getInputStream());
					ObjectOutputStream outputToClient = new ObjectOutputStream(socket.getOutputStream());
					streams.add(outputToClient);
					
					ServerHandler serverHandler = new ServerHandler(inputFromClient, outputToClient);
					Thread thread = new Thread(serverHandler);
					thread.start();
				}
			} catch (IOException e) {}
			
			
		}
	}
	private static class ServerHandler implements Runnable {

			private ObjectInputStream input;
			private ObjectOutputStream output;
			private String connectedTo = null;
			
			public ServerHandler(ObjectInputStream input, ObjectOutputStream output) {
				this.input = input;
				this.output = output;
			}

			@Override
			public void run(){
				while(true) {
					MessageHeader header = null;
					Boolean temp;
					String usernameTemp, password;
					try {
						header = (MessageHeader) input.readObject();
						switch(header) {
							case PLANE:
								String str = (String) input.readObject();
								System.out.println(str);
								break;
							default:
								break;
						}
					} catch (Exception e) {};
					
				}
			}
		
		
	}
	
	
	//-----------------------------------------------------------------------------------------------
	

	public void newPlane(Plane plane) throws IOException{
		for(ObjectOutputStream stream : streams) {
			stream.writeObject(MessageHeader.PLANE);
			stream.writeObject("test");
			stream.flush();
		}
		return;
	}
}
