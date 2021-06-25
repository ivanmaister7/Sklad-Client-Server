package server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Random;

public class StoreClientUDP {
    public static void main(String[] args) throws Exception {

        if (args.length != 1) {
            System.out.println("Usage: java Client <hostname>");
            return;
        }

        for (int i = 0; i < 10; i++) {
            // get a datagram socket
            DatagramSocket socket = new DatagramSocket();

            // send request
            byte[] buf = new byte[256];
            InetAddress address = InetAddress.getByName(args[0]);
            Packet pack = new Packet((byte)1,1L,new Message(new Random().nextInt(5),i,"UDP Packet"));
            buf = pack.toByte();
            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4445);
            socket.send(packet);

            // get response
            packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);

            // display response
            Packet pa = new Packet(packet.getData());
            String received = pa.getBMsg().getMessage();
            System.out.println("response: " + received);

            socket.close();
        }
    }
}