package application;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import entities.Peer;
import filesync.DirectoryWatcher;
import filesync.FileManager;
import network.PeerServer;

public class Main {

    public static void main(String[] args) throws FileNotFoundException, IOException {
        if (args.length < 3) {
            System.out.println("Uso: java -jar app.jar <porta> <peers.txt> <diretorio>");
            return;
        }

        int port = Integer.parseInt(args[0]);
        String peersFile = args[1];
        String dir = args[2];

        Set<String> knownPeers = new HashSet<>();
        try (BufferedReader br = new BufferedReader(new FileReader(peersFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    knownPeers.add(line.trim());
                }
            }
        }

        Peer peer = new Peer("127.0.0.1:" + port, knownPeers, dir);

        // Cria FileManager passando o peer como parâmetro
        FileManager fileManager = new FileManager(dir, peer);

        // Cria PeerServer passando o fileManager como parâmetro
        PeerServer server = new PeerServer(peer, port, fileManager);
        new Thread(server).start();

        DirectoryWatcher watcher = new DirectoryWatcher(dir, fileManager);
        new Thread(watcher).start();

        System.out.println("Peer rodando na porta " + port + " e diretório " + dir);
        System.out.println("Known peers: " + knownPeers);
    }
}