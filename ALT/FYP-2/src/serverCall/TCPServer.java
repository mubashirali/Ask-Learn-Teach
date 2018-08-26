package serverCall;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

import fypALT.MainProgram;

public class TCPServer {

	private static MainProgram mainProgram;

	public static void main(String[] args) throws Exception {

		String clientMsg;
		Socket socket = null;
		DataInputStream dataInputStream = null;
		DataOutputStream dataOutputStream = null;
		ArrayList<String> summery = new ArrayList<String>();
		int topAns = 0;

		mainProgram = new MainProgram();

		try (ServerSocket serverSocket = new ServerSocket(8081)) {

			while (true) {
				try {
					
					clientMsg = null;
					summery.clear();
					topAns = 0;
					
					socket = serverSocket.accept();
					System.out.println("welcome client!"
							+ socket.getInetAddress());

					dataInputStream = new DataInputStream(
							socket.getInputStream());

					dataOutputStream = new DataOutputStream(
							socket.getOutputStream());

					clientMsg = dataInputStream.readUTF();

					if (mainProgram.startSearch(clientMsg)) {

						summery = mainProgram.getSummery();

						if (summery.size() > 1) {
							System.out.println(summery.size());
							dataOutputStream.write(summery.size());

							topAns = mainProgram.getTopIndex();

							dataOutputStream.writeUTF(summery.get(topAns)
									+ System.getProperty("line.separator"));

							for (int index = 0; index < summery.size() - 1; index++) {
								if (topAns != index)
									dataOutputStream
											.writeUTF(summery.get(index)
													+ System.getProperty("line.separator"));
							}
						} else
							dataOutputStream.write(0);

					} else
						dataOutputStream.write(0);

					mainProgram.clearData();

					System.out.println("done " + clientMsg);

					dataOutputStream.close();

					dataInputStream.close();

					socket.close();

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

	}
}
