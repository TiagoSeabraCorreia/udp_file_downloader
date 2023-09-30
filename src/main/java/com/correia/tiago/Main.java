package com.correia.tiago;

import com.correia.tiago.data.FileDownloader;
import java.io.IOException;
public class Main {
    public static void main(String[] args) throws IOException {
        //Args: <server> <port> <file> <folder> <chunk>
        if (args.length != 5) {
            System.out.println("Usage: java Main <server> <port> <file> <folder>");
            return;
        }
        FileDownloader client = new FileDownloader(args[0], Integer.parseInt(args[1]), args[2], args[3], args[4]);
        client.sendRequest();
        client.sendChunkSize();
        client.waitForResponse();
        client.downloadFile();
    }
}