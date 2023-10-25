/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tresenrallaenlinea;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.Queue;

/**
 *
 * @author JONA
 */
public class Procesador implements Runnable {

    private final String mensaje;
    private final InetSocketAddress direccionCliente;
    private final Queue<InetSocketAddress> partidasNuevas;

    public Procesador(String mensaje, InetSocketAddress direccionCliente, Queue<InetSocketAddress> partidasNuevas) {
        this.mensaje = mensaje;
        this.direccionCliente = direccionCliente;
        this.partidasNuevas = partidasNuevas;
    }

    @Override
    public void run() {
        try (DatagramSocket socket = new DatagramSocket()) {
            String[] partesMensaje = mensaje.split(" ");
            String tipoMensaje = partesMensaje[0];

            switch (tipoMensaje) {

                case "NUEVA_PARTIDA":
                    int puertoJuego = Integer.parseInt(partesMensaje[1]);
                    InetSocketAddress direccionJuego = new InetSocketAddress(direccionCliente.getAddress(), puertoJuego);

                    synchronized (partidasNuevas) {
                        if (partidasNuevas.contains(direccionCliente)) {
                            enviarRespuesta(socket, direccionCliente, "DIRECCION_YA_EXISTE");
                            return;
                        }
                        partidasNuevas.add(direccionJuego);
                        partidasNuevas.notifyAll();
                    }
                    enviarRespuesta(socket, direccionCliente, partesMensaje[1]);
                    System.out.println("PARTIDA AGREGADA A LA COLA");
                    break;

                case "UNIRSE_PARTIDA":
                    synchronized (partidasNuevas) {
                        while (partidasNuevas.isEmpty()) {
                            try {
                                System.out.println("No hay partidas disponibles. Esperando...");
                                partidasNuevas.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        InetSocketAddress direccionPartida = partidasNuevas.remove();
                        System.out.println("Cliente conectado a partida en " + direccionPartida);
                        enviarRespuesta(socket, direccionCliente, direccionPartida.toString().substring(1));
                    }
                    break;
                default:
                    System.out.println("Mensaje no reconocido.");
                    break;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void enviarRespuesta(DatagramSocket socket, InetSocketAddress direccion, String respuesta) throws IOException {
        byte[] bufferRespuesta = respuesta.getBytes();
        DatagramPacket paqueteRespuesta = new DatagramPacket(bufferRespuesta, bufferRespuesta.length, direccion);
        socket.send(paqueteRespuesta);
    }
}
