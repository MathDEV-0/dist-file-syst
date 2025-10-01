package filesync;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

public class DirectoryWatcher implements Runnable {
    private final Path dir;
    private final FileManager fileManager;
    private final Set<String> recentlyProcessed = new HashSet<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    // Mutex
    private final Lock lock = new ReentrantLock();

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

                    lock.lock(); // garante exclusão mútua
                    try {
                        if (fileName.startsWith(".") || fileName.endsWith("~") ||
                            recentlyProcessed.contains(fileName)) {
                            continue;
                        }
                        recentlyProcessed.add(fileName);
                    } finally {
                        lock.unlock(); // libera mutex
                    }

                    System.out.println("Alteração detectada: " + event.kind() + " -> " + filePath);

                    // agenda a remoção em 2s com mutex
                    scheduler.schedule(() -> {
                        lock.lock();
                        try {
                            recentlyProcessed.remove(fileName);
                        } finally {
                            lock.unlock();
                        }
                    }, 2, TimeUnit.SECONDS);

                    if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE ||
                        event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
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
