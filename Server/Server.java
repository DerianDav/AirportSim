
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;



public class Server {
	private static ServerSocket serverSocket;
	private static ArrayList<Socket> streams;
	private static ArrayList<Integer> ports;
	private static Server server;
	
	public static void main(String[] args) throws IOException {
		serverSocket = new ServerSocket(7212);
		streams = new ArrayList<Socket>();
		ports = new ArrayList<Integer>();
		server = new Server();
		server.startServer();
	}
	
	private void startServer() throws IOException {
		while(true) {
			Socket socket = serverSocket.accept();
			System.out.println("incoming client");
		
			streams.add(socket);
			ClientHandler clientHandler =  new ClientHandler(socket);
			Thread thread = new Thread(clientHandler);
			thread.start();
		}
		
	}

	public class ClientHandler implements Runnable {

		private ObjectInputStream input;
		private ObjectOutputStream output;
		private String connectedTo = null;
		private Socket thisSock;
		
		public ClientHandler(Socket socket) throws IOException {
			thisSock = socket;
			input = new ObjectInputStream(socket.getInputStream());
			output = new ObjectOutputStream(socket.getOutputStream());
		}

		@Override
		public void run(){
			try {
					MessageHeader header = (MessageHeader) input.readObject();
					int sPort = input.readInt();
					ports.add(sPort);
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
					Thread.sleep(3000);
					checkHeartbeat(thisSock);
					System.out.println("S: HB done");
				
			} catch (Exception e) {e.printStackTrace();
				ports.remove(streams.indexOf(thisSock));
				streams.remove(thisSock);
				try {
					output.close();
				} catch (IOException e1) {}
			};	
		}//end of run
		

		public void checkHeartbeat(Socket sock) throws Exception{
			int index;
			while(true) {
				index = streams.indexOf(sock);
				Thread.sleep(2500);
				System.out.println(input.available());
				if(input.available() >= 1) {
					while(input.available() >= 1)
						input.read(); 
				}//continue;
				
				else {
					System.out.println(input.available());
					synchronized(this) {
						index = streams.indexOf(sock);
						streams.remove(index);
						ports.remove(index);
					}
					try {
						thisSock.close();
					}catch(Exception e){}
					return;
				}
			}	
		}
		
	}
}