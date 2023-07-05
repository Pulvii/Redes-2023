import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class UDPClient {
    private static final int SERVER_PORT = 5000;
    private static final int BUFFER_SIZE = 1024;

    private DatagramSocket socket;
    private InetAddress serverAddress;

    public UDPClient(InetAddress serverAddress) throws IOException {
        socket = new DatagramSocket();
        this.serverAddress = serverAddress;
    }

    public void start() throws IOException {
        System.out.println("Cliente iniciado");

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Ingrese un mensaje:");
            String message = scanner.nextLine();

            if (message.equalsIgnoreCase("salir")) {
                break;
            }

            byte[] buffer = message.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverAddress, SERVER_PORT);
            socket.send(packet);
            System.out.println("Mensaje enviado al servidor.");

            recibirMensaje();
        }

        socket.close();
    }

    private void recibirMensaje() throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];
        DatagramPacket packet = new DatagramPacket(buffer, BUFFER_SIZE);
        socket.receive(packet);

        String receivedMessage = new String(packet.getData(), 0, packet.getLength());
        System.out.println("Mensaje recibido del servidor: " + receivedMessage);
    }


    public static void main(String[] args) {
        try {
            InetAddress serverAddress = InetAddress.getByName("127.0.0.1"); // Dirección IP del servidor
            UDPClient client = new UDPClient(serverAddress);
            client.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
