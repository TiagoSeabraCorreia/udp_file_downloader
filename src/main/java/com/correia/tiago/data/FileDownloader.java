package com.correia.tiago.data;

import java.io.*;
import java.net.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class FileDownloader {
    public String file;
    public String folder;
    public InetAddress ip;
    public Integer port;

    DatagramPacket sender_packet;
    DatagramSocket sender_socket;

    DatagramPacket receiver_packet;
    DatagramSocket receiver_socket;
    Integer chunk;
    Integer nChunks;

    Boolean requestSent = false;
    public FileDownloader(String ip, Integer port, String file, String folder, String chunk) throws UnknownHostException, SocketException {
        this.file = file;
        this.folder = folder;
        this.ip = InetAddress.getByName(ip);
        this.port = port;
        this.chunk = Integer.parseInt(chunk);
        this.sender_packet = new DatagramPacket(file.getBytes(), file.length(), this.ip, this.port);
        this.sender_socket = new DatagramSocket();
        this.receiver_packet = new DatagramPacket(new byte[this.chunk], this.chunk);
        this.receiver_socket = new DatagramSocket(2001);
    }

    public void sendRequest() throws IOException {
        sender_socket.send(sender_packet);
    }
    public void waitForResponse() throws IOException { //The response will be the number of chunks that will be sent
        sender_socket.receive(receiver_packet);
        if (sender_packet.getData()[0] == 0) {
            System.out.println("Mensagem recebida");
        }
        String chunks = new String(receiver_packet.getData(), 0, receiver_packet.getLength());
        System.out.println(chunks);
        this.nChunks = Integer.valueOf(chunks);
    }

    public void sendChunkSize() throws IOException {
        sender_packet.setData((String.valueOf(chunk).getBytes()));
        sender_packet.setLength(sender_packet.getData().length);
        sender_socket.send(sender_packet);
        System.out.println("Chunk size sent");
    }

    public void downloadFile() throws IOException {
        try (FileOutputStream fos = new FileOutputStream("./files/" + file, false)){
            for (Integer i = 1; i < nChunks; i++) {
                sender_packet = new DatagramPacket(new byte[chunk], chunk);
                sender_packet.setLength(chunk);
                sender_socket.receive(sender_packet);
                fos.write(sender_packet.getData());
                System.out.println(Arrays.toString(sender_packet.getData()));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
