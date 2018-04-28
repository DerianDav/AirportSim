
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javafx.concurrent.Task;

/**
 * Connects to the designated server to retrieve the other clients
 * Once that is done this will constantly send a heart beat message to the server
 * This will also start a new process for the local server (takes messages from the other clients) for each client
 * @author Derian Davila Acuna
 *
 */
public class ClientServer {
	private static String startingAddress = "localhost";
	private static final int startServerSocket = 7212;
	
	private Socket socket;
	private ObjectOutputStream outputToServer;
	private ObjectInputStream inputFromServer;
	private static ServerSocket serverSocket;
	private static ArrayList<ObjectOutputStream> streams = new ArrayList<ObjectOutputStream>(); // arrays of output streams to the other clients
	
	static Airport airport;
	public ClientServer(Airport airport, String serverAddr) {
		startingAddress = serverAddr;
		this.airport = airport;
		try {
			serverSocket = new ServerSocket(0);
		} catch (IOException e) {}
		openConnection();
		localServerListener lsl = new localServerListener();
		Thread thread = new Thread(lsl);
		thread.start();
	}

	/**
	 * opens connection with the server
	 */
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
			ServerListener listener = new ServerListener(inputFromServer, outputToServer);
			
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
		private ObjectOutputStream output;
		
		public ServerListener(ObjectInputStream input, ObjectOutputStream output) {
			this.input = input;
			this.output = output;
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
							heartbeats();
							break;
					default:
						continue;
					}
				}
				
			}catch(Exception e) {e.printStackTrace();}
		} 
		
		/*
		 * Sends a heartbeat message to the server every second
		 * Tells the server the client is still alive
		 */
		public void heartbeats() throws IOException, InterruptedException {
			while(true) { 
				output.writeInt(1);
				//output.write(1);
				output.flush();
				Thread.sleep(2000);
			}
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
	
	/**
	 * Starts the local Server Handler for each incoming Client
	 * @author Derian Davila Acuna
	 *
	 */
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
						System.out.println(header);
						switch(header) {
							case PLANE:
								PlaneShell plane = (PlaneShell) input.readObject();
								System.out.println("new Plane");
								airport.newClientPlane(plane);
								break;
							case REQTIME:
								output.writeObject(MessageHeader.GETTIME);
								System.out.println("time req");
								output.writeInt(airport.globalTime);
								output.flush();
								System.out.println("wrote globalTime to ");
								break;
							case REQPLANES:
								for(int i = 0; i < airport.planesOnGround.size(); i++) {
									output.writeObject(MessageHeader.PLANE);
									output.writeObject(new PlaneShell(airport.planesOnGround.get(i)));
								}
								output.flush();
								break;
							case REQTERM:
								boolean[] terms = airport.getTerminals();
								for(int i = 0; i < terms.length; i++) {
									output.writeObject(MessageHeader.TERMINAL);
									output.writeInt(i);
									output.writeBoolean(terms[i]);
								}
								output.flush();
								break;
							case REQRUNWAY:
								output.writeObject(MessageHeader.RUNWAY);
								output.writeInt(0);
								output.writeBoolean(airport.runway0InUse);
								output.writeObject(MessageHeader.RUNWAY);
								output.writeInt(1);
								output.writeBoolean(airport.runway1InUse);
								output.flush();
								break;
							case GETTIME:
								int time = input.readInt();
								System.out.println("Time: " + time);
								airport.globalTime = time;
								break;
							case TERMINAL:
								int termNum = input.readInt();
								boolean status = input.readBoolean();
								airport.setTerminalStatus(termNum,status);
								break;
							case RUNWAY:
								int runway = input.readInt();
								status = input.readBoolean();
								airport.setRunwayStatus(runway,status);
								break;
							default:
								break;
						}
					} catch (Exception e) {
						streams.remove(this.output);
						
					};
					
				}
			}
		
		
	}
	
	
	//-----------------------------------------------------------------------------------------------
	
	/** 
	 * gets the time from the first client
	 */
	public void reqTime(){
		if(streams.size() == 0) {
			System.out.println("req time == 0");
			airport.globalTime = 0;
			return;
		}
		try {
			streams.get(0).writeObject(MessageHeader.REQTIME);
			streams.get(0).flush();
		}catch(IOException e) {streams.remove(0);}
	}
	
	/**
	 * gets the terminal status from the first client
	 */
	public void reqTerm(){
		if(streams.size() == 0) {
			airport.globalTime = 0;
			return;
		}
		try {
			streams.get(0).writeObject(MessageHeader.REQTERM);
			streams.get(0).flush();
		}catch(IOException e) {streams.remove(0);}
	}
	
	/**
	 * gets the runway status from the first client
	 */
	public void reqRunway(){
		if(streams.size() == 0) {
			airport.globalTime = 0;
			return;
		}
		try {
			streams.get(0).writeObject(MessageHeader.REQRUNWAY);
			streams.get(0).flush();
		}catch(IOException e) {streams.remove(0);}
	}
	
	/**
	 * gets the plane list from the first client
	 */
	public void planeList() {
		if(streams.size() == 0) {
			airport.globalTime = 0;
			return;
		}
		try {
			streams.get(0).writeObject(MessageHeader.REQPLANES);
			streams.get(0).flush();
		}catch(IOException e) {streams.remove(0);}		
	}
	
	/**
	 * sends a plane to all the currently connected clients
	 * @param plane - plane to be sent
	 */
	public void newPlane(PlaneShell plane){
		for(int i = 0; i < streams.size(); i++) {
			ObjectOutputStream stream = streams.get(i);
			try {
				stream.writeObject(MessageHeader.PLANE);
				stream.writeObject(plane);
				stream.flush();
				System.out.println("write plane");
			}catch(NotSerializableException e1) {e1.printStackTrace();}catch(IOException e) {streams.remove(stream);}
		}
		return;
	}
	
	/**
	 * sends a terminal status to all the connected clients
	 * @param termNum - current terminal needed to be change
	 * @param status - status of the terminal
	 */
	public void sendTerminal(int termNum, boolean status) {
		for(int i = 0; i < streams.size(); i++) {
			ObjectOutputStream stream = streams.get(i);
			try {
				stream.writeObject(MessageHeader.TERMINAL);
				stream.writeInt(termNum);
				stream.writeBoolean(status);;
				stream.flush();
				System.out.println("write plane");
			}catch(NotSerializableException e1) {e1.printStackTrace();}catch(IOException e) {streams.remove(stream);}
		}
		return;
	}
	
	/**
	 * sends the runway status to all the connected clients
	 * @param runway - runway that needs to be change
	 * @param status - status of the runway
	 */
	public void sendRunway(int runway, boolean status) {
		for(int i = 0; i < streams.size(); i++) {
			ObjectOutputStream stream = streams.get(i);
			try {
				stream.writeObject(MessageHeader.RUNWAY);
				stream.writeInt(runway);
				stream.writeBoolean(status);;
				stream.flush();
				System.out.println("write plane");
			}catch(NotSerializableException e1) {e1.printStackTrace();}catch(IOException e) {streams.remove(stream);}
		}
		return;
	}
}