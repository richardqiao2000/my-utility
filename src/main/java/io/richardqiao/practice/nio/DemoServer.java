package io.richardqiao.practice.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class DemoServer implements Runnable{
    private final int PORT = 8999;
    private final ServerSocketChannel ssc;
    private final Selector selector;
    public DemoServer() throws IOException {
        ssc = ServerSocketChannel.open();
        ssc.socket().bind(new InetSocketAddress(PORT));
        ssc.configureBlocking(false);
        selector = Selector.open();
        ssc.register(selector, SelectionKey.OP_ACCEPT);
    }

    private void handleConnect(SelectionKey key) throws IOException {
        SocketChannel client = ((ServerSocketChannel) key.channel()).accept();
        String address = client.socket().getInetAddress() + ":" + client.socket().getPort();
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ, address);
        ByteBuffer msg = ByteBuffer.wrap("Congrats. You've connected to server.".getBytes());
        client.write(msg);
        System.out.println("Accepted connection from " + address);
    }

    private void handleMsg(SelectionKey key) throws IOException {
        SocketChannel client = (SocketChannel) key.channel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(256);
        // read bytebuffer
        StringBuilder sb = new StringBuilder();
        int len = 0;
        while((len = client.read(byteBuffer)) != 0){
            byteBuffer.flip();
            byte[] bytes = new byte[byteBuffer.limit()];
            byteBuffer.get(bytes);
            sb.append(new String(bytes));
            byteBuffer.clear();
        }
        if(sb.length() <= 2){
            return;
        }
        String msg;
        if(len < 0){
            msg = key.attachment() + " has left the chat.";
        }else{
            msg = key.attachment() + ": " + sb;
        }
        // loop selector registered sockets and send message
        ByteBuffer msgBF = ByteBuffer.wrap(msg.getBytes());
        for(SelectionKey rKey: selector.keys()){
            if(rKey == key){
                continue;
            }
            if(rKey.isValid() && rKey.channel() instanceof SocketChannel) {
                SocketChannel c = (SocketChannel) rKey.channel();
                c.write(msgBF);
                msgBF.rewind();
            }
        }
    }

    @Override
    public void run() {
        // using selector to do event loop
        System.out.println("Server started!");
        while(ssc.isOpen()){
            try {
                selector.select();
                for (SelectionKey key : selector.selectedKeys()) {
                    if (key.isAcceptable()) {
                        handleConnect(key);
                    } else if (key.isReadable()) {
                        handleMsg(key);
                    }
                }
                selector.selectedKeys().clear();
            } catch (IOException e) {
                System.out.println("Service terminated with error.");
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        DemoServer demoServer = new DemoServer();
        new Thread(demoServer).start();
    }
}
