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

// Não é mais requisitado, mas mostra de forma simples como enviar uma mensagem UDP
// Pode ser usado para enviar mensagens para servidores externos
// Apenas para abstrair o envio de mensagens UDP