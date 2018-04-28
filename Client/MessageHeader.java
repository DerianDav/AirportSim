

/**
 	Header				Message
 	CLIENT-SERVER
 	NewClient		-	Object (InetAddr), Int(port#)
 	Connect			-	Int(Local server port of the client)
 	ENDCLIENT		- 	NONE
 	HEARTBEAT		-	Int(Doesn't matter what)
 	
 	CLIENT-CLIENT
 	GETTIME			-	Int(Current Global Time)
 	TERMIANL		-	Int (terminal) , Boolean (status of the terminal)
 	RUNWAY			-	Int (runway) , Boolean (status of the runway)
 	PLANE			-	PlaneShell (shell of the plane)
 	REQTIME			-	NONE
 	REQPLANE		-	NONE	
 	REQRUNWAY		-	NONE	
 	REQTERM			-	NONE
 */
 
public enum MessageHeader {
	NEWCLIENT, CONNECT, ENDCLIENT, HEARTBEAT, 
	GETTIME,TERMINAL, RUNWAY,PLANE, 
	REQTIME, REQPLANES, REQRUNWAY, REQTERM;
}