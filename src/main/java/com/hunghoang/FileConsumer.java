package com.hunghoang;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.io.IOException;
import java.nio.file.*;

public class FileConsumer {
    private final Path path;

    public FileConsumer(Path path) {
        this.path = path;
    }

    public Observable<Path> watch() {
        return Observable.<Path>create(emitter -> {
            try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
                path.getParent().register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
                while (!emitter.isDisposed()) {
                    WatchKey key = watchService.take();
                    for (WatchEvent<?> event : key.pollEvents()) {
                        if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                            Path changed = (Path) event.context();
                            if (changed.endsWith(path.getFileName())) {
                                emitter.onNext(path);
                            }
                        }
                    }
                    key.reset();
                }
            } catch (IOException | InterruptedException e) {
                emitter.onError(e);
            }
        }).subscribeOn(Schedulers.io());
    }
}