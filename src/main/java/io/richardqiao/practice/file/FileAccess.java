package io.richardqiao.practice.file;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class FileAccess {

    public static void main(String[] args) throws IOException {
        readFile();
        writeFile();
        writeFileNIO();
    }

    // 1. Read file line by line
    private static void readFile() throws IOException {
        String filePath = "src/main/resources/messages.json";
        // load file
        Path path = Paths.get(filePath);
        BufferedReader reader = Files.newBufferedReader(path);
        String line = null;
        // read line by line
        while((line = reader.readLine()) != null){
            System.out.println(line);
        }
    }

    // 2.0 Write file
    private static void writeFile() throws IOException {
        File fout = new File("src/main/resources/tmp.txt");
        FileOutputStream fos = new FileOutputStream(fout);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
        for (int i = 0; i < 10; i++) {
            bw.write("something");
            bw.newLine();
        }
        bw.close();
    }

    // 2.1 Write file NIO
    private static void writeFileNIO() throws IOException {
        String fileName = "src/main/resources/tmpNIO.txt";
        File file = new File(fileName);
        FileOutputStream fileOutputStream = new FileOutputStream(file, true);
        FileChannel fileChannel = fileOutputStream.getChannel();
        ByteBuffer byteBuffer = null;
        String messageToWrite = null;
        for (int i = 1; i < 100; i++) {
            messageToWrite = "This is a test üüüüüüööööö";
            byteBuffer = ByteBuffer.wrap(messageToWrite.getBytes(StandardCharsets.ISO_8859_1));
            fileChannel.write(byteBuffer);
            fileChannel.write(ByteBuffer.wrap("\n".getBytes()));
        }
    }

}
