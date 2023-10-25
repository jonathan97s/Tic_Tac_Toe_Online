/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tresenrallaenlinea;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * @author JONA
 */
public class ServidorCentral {

    private static final int PUERTO = 7879; // Puerto en el que escucha el servidor
    private static DatagramSocket socket; // Socket UDP
    private static Queue<InetSocketAddress> partidasNuevas; // Cola de partidas nuevas

    public static void main(String[] args) throws Exception {

        // Crear el socket UDP y hacer que escuche en el puerto especificado
        socket = new DatagramSocket(PUERTO);

        // Inicializar la cola de partidas nuevas
        partidasNuevas = new LinkedList<>();

        // Bucle infinito para escuchar peticiones UDP
        while (true) {

            // Crear un datagrama vacío para recibir la petición
            byte[] buffer = new byte[1024];
            DatagramPacket peticion = new DatagramPacket(buffer, buffer.length);

            // Recibir la petición
            socket.receive(peticion);

            String mensaje = new String(peticion.getData()).trim(); // Obtener el mensaje de la petición
            // Obtener la dirección IP y puerto del cliente que envió la petición
            InetSocketAddress direccionCliente = new InetSocketAddress(peticion.getAddress(), peticion.getPort());
            System.out.println("Petición recibida de " + direccionCliente);

            // Procesar la petición en un hilo 
            Thread hiloProcesador = new Thread(new Procesador(mensaje, direccionCliente, partidasNuevas));
            hiloProcesador.start();
        }
    }
}
