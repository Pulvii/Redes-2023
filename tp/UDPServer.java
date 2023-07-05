import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class UDPServer {
    private static final int SERVER_PORT = 5000;
    private static final int BUFFER_SIZE = 1024;

    private List<ClientInfo> clients;
    private DatagramSocket socket;

    public UDPServer() throws IOException {
        clients = new ArrayList<>();
        socket = new DatagramSocket(SERVER_PORT);
    }

    public void start() throws IOException {
        System.out.println("Servidor iniciado");

        while (true) {
            byte[] buffer = new byte[BUFFER_SIZE];
            DatagramPacket packet = new DatagramPacket(buffer, BUFFER_SIZE);
            socket.receive(packet);

            InetAddress clientAddress = packet.getAddress();
            int clientPort = packet.getPort();

            ClientInfo clientInfo = new ClientInfo(clientAddress, clientPort);
            if (!clients.contains(clientInfo)) {
                clients.add(clientInfo);
                System.out.println("Nuevo cliente conectado: " + clientAddress.getHostAddress());
            }

            String message = new String(packet.getData(), 0, packet.getLength());
            System.out.println("Mensaje recibido de " + clientAddress.getHostAddress() + ": " + message);

            // Reenviar mensaje a todos los clientes excepto al cliente que lo envió
            enviarAClientes(message, clientInfo);
        }
    }

    private void enviarAClientes(String message, ClientInfo sender) throws IOException {
        byte[] buffer = message.getBytes();

        for (ClientInfo client : clients) {
            if (!client.equals(sender)) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, client.getAddress(), client.getPort());
                socket.send(packet);
                System.out.println("Mensaje enviado a " + client.getAddress().getHostAddress());
            }
        }
    }

    private static class ClientInfo {
        private InetAddress address;
        private int port;

        public ClientInfo(InetAddress address, int port) {
            this.address = address;
            this.port = port;
        }

        public InetAddress getAddress() {
            return address;
        }

        public int getPort() {
            return port;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            ClientInfo other = (ClientInfo) obj;
            return address.equals(other.address) && port == other.port;
        }

        @Override
        public int hashCode() {
            return address.hashCode() * 31 + port;
        }
    }

    public static void main(String[] args) {
        try {
            UDPServer server = new UDPServer();
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
