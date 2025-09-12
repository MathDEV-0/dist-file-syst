package network;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import entities.Peer;
import filesync.FileManager;

public class PeerServer implements Runnable {
    private Peer peer;
    private DatagramSocket socket;
    private FileManager fileManager;
    
    public PeerServer(Peer peer, int port, FileManager fileManager) {
        super();
        this.peer = peer;
        this.fileManager = fileManager;
        try {
            this.socket = new DatagramSocket(port);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
    
    public void run() {
        byte[] buffer = new byte[65507]; //udp max size
        while(true) {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            try {
                socket.receive(packet);
                String msg = new String(packet.getData(), 0, packet.getLength());
                System.out.println("[" + peer.getAddress() + "] received: " + msg.substring(0, Math.min(50, msg.length())) + "...");
                
                
                if (msg.startsWith("FILE:")) {
                    processFileMessage(packet.getData(), packet.getLength());
                } else if (msg.startsWith("DELETE:")) {
                    processDeleteMessage(msg);
                } else if (msg.startsWith("RENAME:")) {
                    processRenameMessage(msg);
                }
                
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    private void processFileMessage(byte[] data, int length) {
        try {
            String message = new String(data, 0, length);
            String[] parts = message.split(":", 3);
            
            if (parts.length == 3) {
                String fileName = parts[1];
                byte[] fileContent = parts[2].getBytes();
                
                System.out.println("Recebendo arquivo: " + fileName);
                fileManager.saveFile(fileName, fileContent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void processDeleteMessage(String message) {
        try {
            String[] parts = message.split(":");
            if (parts.length == 2) {
                String fileName = parts[1];
                System.out.println("Recebendo comando DELETE: " + fileName);
                fileManager.deleteReceivedFile(fileName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void processRenameMessage(String message) {
        try {
            String[] parts = message.split(":");
            if (parts.length == 3) {
                String oldName = parts[1];
                String newName = parts[2];
                System.out.println("Recebendo comando RENAME: " + oldName + " -> " + newName);
                fileManager.renameReceivedFile(oldName, newName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}