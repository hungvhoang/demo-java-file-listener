package com.hunghoang;

import io.reactivex.rxjava3.disposables.Disposable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        //path of file to listen, use 'pwd' command in mac or linux to get
        String file = "/Users/hunghoang/Documents/project/reactive/text.txt";
        Path path = Paths.get(file);

        FileConsumer fileConsumer = new FileConsumer(path);
        Disposable disposable = fileConsumer.watch().subscribe(changedPath -> {
            try {
                String content = Files.readString(changedPath);
                System.out.println("File content:\n" + content);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        Thread listenThread = new Thread(disposable::dispose);

        Runtime.getRuntime().addShutdownHook(listenThread);

        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
}
