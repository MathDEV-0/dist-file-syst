package filesync;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import entities.Peer;

public class FileManager {
    private final String baseDir;
    private final Peer peer;
    private boolean isReceivingOperation = false;

    public FileManager(String baseDir, Peer peer) {
        this.baseDir = baseDir;
        this.peer = peer;
    }

    // Método para enviar arquivo (CREATE/MODIFY)
    public void sendFile(String filePath) {
        if (isReceivingOperation) return;
        
        System.out.println("Preparando envio do arquivo: " + filePath);
        try {
            byte[] content = Files.readAllBytes(Paths.get(filePath));
            String fileName = Paths.get(filePath).getFileName().toString();
            
            String header = "FILE:" + fileName + ":";
            byte[] headerBytes = header.getBytes();
            byte[] message = new byte[headerBytes.length + content.length];
            
            System.arraycopy(headerBytes, 0, message, 0, headerBytes.length);
            System.arraycopy(content, 0, message, headerBytes.length, content.length);
            
            sendToAllPeers(message);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Método para deletar arquivo
    public void deleteFile(String fileName) {
        if (isReceivingOperation) return;
        
        System.out.println("Solicitando deleção do arquivo: " + fileName);
        String message = "DELETE:" + fileName;
        sendToAllPeers(message.getBytes());
    }

    // Método para renomear arquivo
    public void renameFile(String oldName, String newName) {
        if (isReceivingOperation) return;
        
        System.out.println("Solicitando renomeação: " + oldName + " -> " + newName);
        String message = "RENAME:" + oldName + ":" + newName;
        sendToAllPeers(message.getBytes());
    }

    private void sendToAllPeers(byte[] message) {
        Set<String> peers = peer.getKnownPeers();
        System.out.println("Tentando enviar para peers: " + peers);
        
        for (String peerAddress : peers) {
            try {
                String[] parts = peerAddress.split(":");
                String host = parts[0];
                int port = Integer.parseInt(parts[1]);
                
                System.out.println("Tentando conectar em: " + host + ":" + port);
                
                // Tenta resolver primeiro normalmente
                InetAddress address;
                try {
                    address = InetAddress.getByName(host);
                    System.out.println("Resolvido via DNS: " + address.getHostAddress());
                } catch (Exception e) {
                    // Se falhar, tenta resolução manual para Docker
                    System.out.println("DNS falhou, tentando resolução manual...");
                    address = resolveDockerHost(host);
                }
                
                DatagramPacket packet = new DatagramPacket(
                    message, message.length, address, port
                );
                
                DatagramSocket socket = new DatagramSocket();
                socket.send(packet);
                socket.close();
                
                System.out.println("Operação enviada com SUCESSO para: " + peerAddress);
                
            } catch (Exception e) {
                System.err.println("ERRO ao enviar para " + peerAddress + ": " + e.getMessage());
            }
        }
    }

    private InetAddress resolveDockerHost(String hostname) throws Exception {
        // Mapeamento para containers Docker
        Map<String, String> dockerIps = new HashMap<>();
        dockerIps.put("peer1", "127.0.0.1"); // Localhost para teste local
        dockerIps.put("peer2", "127.0.0.1");
        dockerIps.put("peer3", "127.0.0.1");
        
        // Se estiver no Docker, usa os IPs da rede Docker
        if (System.getenv("DOCKER_ENV") != null) {
            dockerIps.put("peer1", "172.18.0.4");
            dockerIps.put("peer2", "172.18.0.2");
            dockerIps.put("peer3", "172.18.0.3");
        }
        
        String ip = dockerIps.get(hostname);
        if (ip != null) {
            return InetAddress.getByName(ip);
        }
        
        throw new Exception("Host não encontrado: " + hostname);
    }



    public void saveFile(String fileName, byte[] content) {
        isReceivingOperation = true;
        try {
            Path dest = Paths.get(baseDir, fileName);
            Files.write(dest, content);
            System.out.println("Arquivo salvo em: " + dest);
            Thread.sleep(100);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            isReceivingOperation = false;
        }
    }

    public void deleteReceivedFile(String fileName) {
        isReceivingOperation = true;
        try {
            Path filePath = Paths.get(baseDir, fileName);
            Files.deleteIfExists(filePath);
            System.out.println("Arquivo deletado: " + fileName);
            Thread.sleep(100);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            isReceivingOperation = false;
        }
    }

    public void renameReceivedFile(String oldName, String newName) {
        isReceivingOperation = true;
        try {
            Path oldPath = Paths.get(baseDir, oldName);
            Path newPath = Paths.get(baseDir, newName);
            Files.move(oldPath, newPath);
            System.out.println("Arquivo renomeado: " + oldName + " -> " + newName);
            Thread.sleep(100);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            isReceivingOperation = false;
        }
    }
}