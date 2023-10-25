/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tresenrallaenlinea;

import java.io.Serializable;
import java.util.Random;
import java.util.Scanner;

/**
 *
 * @author JONA
 */
public class TresEnRalla implements Serializable {

    private char[][] tablero;
    private char turno;
    String turnoPlayer = "";
    int fila;
    int columna;
    boolean ganador = false;

    public TresEnRalla() {
        tablero = new char[3][3];
        for (int fila = 0; fila < 3; fila++) {
            for (int columna = 0; columna < 3; columna++) {
                tablero[fila][columna] = ' ';
            }
        }
        turno = 'X'; // El jugador 'X' empieza siempre la partida
        Random random = new Random();
        int randomNumber = random.nextInt(2); // 0 o 1
        if (randomNumber == 0) {
            turnoPlayer = "Servidor";
        } else {
            turnoPlayer = "Cliente";
        }

    }

    public String getTurnoPlayer() {
        return turnoPlayer;
    }

    public void imprimirTablero() {
        System.out.println("Jugador " + turnoPlayer + "(" + turno + ")" + ", introduce la fila y la columna\nde la casilla en la que quieres jugar.");
        System.out.println("-------------");
        for (int i = 0; i < 3; i++) {
            System.out.print("| ");
            for (int j = 0; j < 3; j++) {
                System.out.print(tablero[i][j] + " | ");
            }
            System.out.println();
            System.out.println("-------------");
        }
        pedirCadena();
    }

    public boolean comprobarGanador() {
        // Comprobamos filas
        for (int i = 0; i < 3; i++) {
            if (tablero[i][0] == turno && tablero[i][1] == turno && tablero[i][2] == turno) {
                return true;
            }
        }

        // Comprobamos columnas
        for (int j = 0; j < 3; j++) {
            if (tablero[0][j] == turno && tablero[1][j] == turno && tablero[2][j] == turno) {
                return true;
            }
        }

        // Comprobamos diagonales
        if (tablero[0][0] == turno && tablero[1][1] == turno && tablero[2][2] == turno) {
            return true;
        }

        if (tablero[0][2] == turno && tablero[1][1] == turno && tablero[2][0] == turno) {
            return true;
        }

        return false;
    }

    public void jugar(int fila, int columna) {

        if (tablero[fila][columna] != ' ') {
            System.out.println("Esa casilla ya está ocupada, elige otra.");
            pedirCadena();
        } else {
            tablero[fila][columna] = turno;
            if (comprobarGanador()) {
                ganador = true;
            } else {
                turno = (turno == 'X') ? 'O' : 'X';
                if (turnoPlayer.equals("Servidor")) {
                    turnoPlayer = "Cliente";
                } else {
                    turnoPlayer = "Servidor";
                }
            }

        }
    }

    public char getTurno() {
        return turno;
    }

    public void setTurno(char turno) {
        this.turno = turno;
    }

    public boolean esFormatoValido(String cadena) {
        String[] coordenadas = cadena.trim().split(" ");

        if (coordenadas.length != 2) {
            return false;
        }

        try {
            fila = Integer.parseInt(coordenadas[0]);
            columna = Integer.parseInt(coordenadas[1]);

            if (fila < 1 || fila > 3 || columna < 1 || columna > 3) {
                return false;
            }

            fila--;
            columna--;
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }

    public void pedirCadena() {
        String cadena;
        do {
            System.out.println("Ingresa una cadena con formato válido (por ejemplo, '2 3'): ");
            Scanner scanner = new Scanner(System.in);
            cadena = scanner.nextLine();
        } while (!esFormatoValido(cadena));

        System.out.println("La cadena ingresada es válida: " + cadena);
        jugar(fila, columna);

    }

    public void imprimirTableroVista() {
        System.out.println("Espara porfavor, es el turno del Jugador " + turnoPlayer + "(" + turno + ")");
        System.out.println("-------------");
        for (int i = 0; i < 3; i++) {
            System.out.print("| ");
            for (int j = 0; j < 3; j++) {
                System.out.print(tablero[i][j] + " | ");
            }
            System.out.println();
            System.out.println("-------------");
        }

    }

    public void imprimirTableroGanador() {
        System.out.println("El Jugador " + turnoPlayer + "(" + turno + ")" + " ha ganado!");
        System.out.println("-------------");
        for (int i = 0; i < 3; i++) {
            System.out.print("| ");
            for (int j = 0; j < 3; j++) {
                System.out.print(tablero[i][j] + " | ");
            }
            System.out.println();
            System.out.println("-------------");
        }

    }

}
