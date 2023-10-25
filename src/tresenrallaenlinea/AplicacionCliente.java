/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tresenrallaenlinea;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author JONA
 */
public class AplicacionCliente {

    private static final String DIRECCION_SERVIDOR = "127.0.0.1"; // Dirección IP del servidor central
    private static final int PUERTO_SERVIDOR = 7879; // Puerto del servidor central
    private static final int PUERTO_MINIMO = 8000; // Puerto mínimo para el servidor TCP del cliente
    private static final int PUERTO_MAXIMO = 8999; // Puerto máximo para el servidor TCP del cliente

    public static void main(String[] args) throws Exception {

        AplicacionCliente AplicacionCliente = new AplicacionCliente();

        // Interfaz de usuario
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Bienvenido a la aplicación cliente.");
        System.out.println("Elija una opción:");
        System.out.println("1. Crear una partida nueva");
        System.out.println("2. Unirse a una partida existente");
        int opcion = Integer.parseInt(br.readLine());

        if (opcion == 1) {
            AplicacionCliente.crearPartidaNueva();

            // Iniciar el juego
        } else if (opcion == 2) {
            AplicacionCliente.unirsePartida();
        }
    }

    public void crearPartidaNueva() throws IOException {
        // Crear un socket UDP y enviar una petición al servidor central
        DatagramSocket socket = new DatagramSocket();
        enviarPeticion(socket, "NUEVA_PARTIDA");

        // Esperar respuesta del servidor central con el puerto asignado al cliente
        int puertoCliente = recibirPuerto(socket);

        // Convertir el cliente en un servidor TCP y esperar a que se conecte un segundo jugador
        ServerSocket servidorTCP = iniciarServidorTCP(puertoCliente);
        esperarJugador(servidorTCP);
    }

    private void enviarPeticion(DatagramSocket socket, String tipoMensaje) throws IOException {
        Scanner scanner = new Scanner(System.in);
        int puertoPartida = 0;

        while (puertoPartida == 0) {
            System.out.print("Ingresa el puerto en el que deseas jugar la partida: ");
            try {
                puertoPartida = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("El puerto ingresado no es válido. Inténtalo de nuevo.");
            }
        }

        String mensaje = tipoMensaje + " " + puertoPartida;
        byte[] buffer = mensaje.getBytes();
        DatagramPacket peticion = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(DIRECCION_SERVIDOR), PUERTO_SERVIDOR);
        socket.send(peticion);

        System.out.println("Petición enviada al servidor central para crear una nueva partida.");
    }

    private int recibirPuerto(DatagramSocket socket) throws IOException {
        byte[] respuesta = new byte[1024];
        DatagramPacket paqueteRespuesta = new DatagramPacket(respuesta, respuesta.length);
        socket.receive(paqueteRespuesta);
        int puertoCliente = Integer.parseInt(new String(paqueteRespuesta.getData()).trim());
        System.out.println("Servidor TCP escuchando en el puerto " + puertoCliente + ".");

        socket.close();
        return puertoCliente;
    }

    private ServerSocket iniciarServidorTCP(int puerto) throws IOException {
        ServerSocket servidorTCP = new ServerSocket(puerto);
        System.out.println("Servidor TCP iniciado. Esperando a que se conecte un segundo jugador...");
        return servidorTCP;
    }

    private void esperarJugador(ServerSocket servidorTCP) throws IOException {

        Socket socket1 = servidorTCP.accept();
        System.out.println("Segundo jugador conectado.\nComenzando el juego...");
        TresEnRalla tresEnRalla = new TresEnRalla();

        // Hilo para aceptar nuevas conexiones
        boolean continuar = true;
        Thread hiloNuevasConexiones = new Thread(() -> {
            while (continuar) {
                try {
                    Socket clientSocket = servidorTCP.accept();
                    // Enviar mensaje de error al cliente
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                    out.println("Se rechazó la conexión del cliente.\nNo se puede conectar. Ya hay un jugador conectado.");
                    // Cerrar conexión
                    clientSocket.close();
                } catch (IOException e) {
                    System.out.println("Error al rechazar nueva conexión");
                }
            }
        });
        hiloNuevasConexiones.start();

        while (true) {
            ObjectOutputStream out = new ObjectOutputStream(socket1.getOutputStream());
            out.writeObject(tresEnRalla);
            out.flush();
            if (tresEnRalla.ganador == true) {
                tresEnRalla.imprimirTableroGanador();
                break;
            }
            if (tresEnRalla.getTurnoPlayer().equals("Cliente")) {
                tresEnRalla.imprimirTableroVista();
                ObjectInputStream ois = new ObjectInputStream(socket1.getInputStream());
                try {
                    tresEnRalla = (TresEnRalla) ois.readObject();
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(AplicacionCliente.class.getName()).log(Level.SEVERE, null, ex);
                }

            } else {
                tresEnRalla.imprimirTablero();
            }
        }

        hiloNuevasConexiones.stop();
        socket1.close();
        servidorTCP.close();

    }

    public void unirsePartida() {
        try {
            // Enviar una petición UDP al servidor central para unirse a una partida existente
            DatagramSocket socket = enviarSolicitudPartida();

            // Esperar respuesta del servidor con la dirección IP y puerto de la partida
            String direccionIP = "";
            int puerto = 0;
            String respuesta = recibirRespuestaServidor(socket);
            String[] datosPartida = respuesta.split(":");
            direccionIP = datosPartida[0];
            puerto = Integer.parseInt(datosPartida[1]);

            // Conectar al servidor TCP de la partida
            Socket socket2 = conectarServidorTCP(direccionIP, puerto);

            System.out.println("!Que empieze el juego...!");
            while (true) {
                ObjectInputStream ois = new ObjectInputStream(socket2.getInputStream());
                try {
                    TresEnRalla tresRalla = (TresEnRalla) ois.readObject();
                    if (tresRalla.ganador == true) {
                        tresRalla.imprimirTableroGanador();
                        break;
                    }
                    if (tresRalla.getTurnoPlayer().equals("Servidor")) {
                        tresRalla.imprimirTableroVista();
                    } else {
                        tresRalla.imprimirTablero();
                        ObjectOutputStream out = new ObjectOutputStream(socket2.getOutputStream());

                        out.writeObject(tresRalla);

                        out.flush();
                    }
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(AplicacionCliente.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (IOException e) {
            System.out.println("Error al enviar solicitud de unirse a partida existente: " + e.getMessage());
        }
    }

    private DatagramSocket enviarSolicitudPartida() throws IOException {
        // Crear mensaje de solicitud de partida
        String mensaje = "UNIRSE_PARTIDA";
        byte[] buffer = mensaje.getBytes();
        InetAddress direccionServidor = InetAddress.getLocalHost();
        int puertoServidor = 7879;

        // Crear paquete y enviar solicitud de partida
        DatagramPacket paquete = new DatagramPacket(buffer, buffer.length, direccionServidor, puertoServidor);
        DatagramSocket socket = new DatagramSocket();
        socket.send(paquete);
        System.out.println("Enviando solicitud de unirse a partida existente...");
        return socket;
    }

    private String recibirRespuestaServidor(DatagramSocket socket) throws IOException {
        byte[] bufferRespuesta = new byte[1024];
        DatagramPacket paqueteRespuesta = new DatagramPacket(bufferRespuesta, bufferRespuesta.length);
        socket.receive(paqueteRespuesta);
        String respuesta = new String(paqueteRespuesta.getData(), 0, paqueteRespuesta.getLength());
        return respuesta;
    }

    private Socket conectarServidorTCP(String direccionIP, int puerto) throws IOException {
        Socket socket = new Socket(direccionIP, puerto);
        System.out.println("Conectado al servidor TCP de la partida en " + direccionIP + ":" + puerto);
        return socket;
    }

}
