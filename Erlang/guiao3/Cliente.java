import java.io.*;
import java.net.*;

public class Cliente {
	public static void main(String[] args) {
		try {
			String host = args[0];
			int port = Integer.parseInt(args[1]);
			Socket s = new Socket(host, port);
			BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
			PrintWriter out = new PrintWriter(s.getOutputStream());
			out.println("Hello!");
			out.flush();
		} catch(Exception e) {
		
		}
	}
}
