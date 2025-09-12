package filesync;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashSet;
import java.util.Set;

public class DirectoryWatcher implements Runnable {
    private final Path dir;
    private final FileManager fileManager;
    private final Set<String> recentlyProcessed = new HashSet<>();

    public DirectoryWatcher(String dir, FileManager fileManager) {
        this.dir = Paths.get(dir);
        this.fileManager = fileManager;
    }

    @Override
    public void run() {
        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
            dir.register(watchService,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_MODIFY,
                    StandardWatchEventKinds.ENTRY_DELETE);

            while (true) {
                WatchKey key = watchService.take();
                for (WatchEvent<?> event : key.pollEvents()) {
                    Path filePath = dir.resolve((Path) event.context());
                    String fileName = filePath.getFileName().toString();
                    
                    // Ignora arquivos temporários e já processados recentemente
                    if (fileName.startsWith(".") || fileName.endsWith("~") || 
                        recentlyProcessed.contains(fileName)) {
                        continue;
                    }

                    System.out.println("Alteração detectada: " + event.kind() + " -> " + filePath);

                    // Adiciona à lista de processados recentemente
                    recentlyProcessed.add(fileName);
                    
                    // Remove após um tempo
                    new Thread(() -> {
                        try {
                            Thread.sleep(2000);
                            recentlyProcessed.remove(fileName);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }).start();

                    // Processa diferentes tipos de evento
                    if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE ||
                        event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                        Thread.sleep(100);
                        fileManager.sendFile(filePath.toString());
                    } else if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
                        fileManager.deleteFile(fileName);
                    }
                }
                key.reset();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}