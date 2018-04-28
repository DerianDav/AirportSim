COMMANDS:
	Template:
		java GUIMain <optional mode> <optional serverAddr>
		
		option mode: 1 = offline 0 = online	

	No Server:
		java GUIMain 1 
	
	With Server:
		java GUIMain 0 - assuming your server is on localhost
		java GUIMain 0 chow - connects to the server found on pc "chow"

	Server Commands:
		java Server 