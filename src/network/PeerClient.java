package network;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


public class PeerClient {
	
	public static void sendMessage(String msg, String host, int port) throws Exception {
        DatagramSocket socket = new DatagramSocket();
        byte[] data = msg.getBytes();
        DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName(host), port);
        socket.send(packet);
        socket.close();
    }
	
}