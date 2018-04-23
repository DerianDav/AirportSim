package Client;

/**
 	-------------------------------------------------------------------------------------
 	|Header			|Types Recieved			|											|
 	-------------------------------------------------------------------------------------
 	|NEWCLIENT		|INETADDRESS, INT		|INETADDRESS = client address, int = port #	|
	|				|						|											|
	|				|						|											|
	|				|						|											|
	|				|						|											|
	|				|						|											|
	|				|						|											|
	|				|						|											|
	|				|						|											|
	|				|						|											|
	|				|						|											|
	|				|						|											|
	|				|						|											|
	|				|						|											|
	|				|						|											|
	|				|						|											|
	-------------------------------------------------------------------------------------
 
 
 */

public enum MessageHeader {
	NEWCLIENT, CONNECT, ENDCLIENT,
	PLANE,RUNWAY,TERMINAL,ONRUNWAY0,ONRUNWAY1, NEXTTICK,CLIENTOUTPUT, CLIENTINPUT;
}
