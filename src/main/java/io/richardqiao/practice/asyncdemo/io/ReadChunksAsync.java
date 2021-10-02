package io.richardqiao.practice.asyncdemo.io;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.Future;

public class ReadChunksAsync {
    // Create 10 chunk tasks to read data
    // make sure these 10 tasks are async running

    static ByteBuffer buffer = ByteBuffer.wrap(new byte[1_000_000]);
    static Path filePath = Paths.get("src/main/resources/BQL_Fundamentals_Documentation.pdf");
    public static void main(String[] args) {
        // Create channel
        try (AsynchronousFileChannel asyncChannel = AsynchronousFileChannel.open(filePath, StandardOpenOption.READ)) {
            Future<Integer> result = asyncChannel.read(buffer, 0);
            // Immediately returns here
            while (!result.isDone()) {
                System.out.println("Waiting for the asynchronous file read operation ..... ");
                System.out.println("Do some other processing");
            }
            // Reset current position to 0 and limit
            // as current buffer position
            buffer.flip();
            String data = new String(buffer.array()).trim();
            System.out.println(data);
            buffer.clear();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
