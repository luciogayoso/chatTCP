package com.ejemplo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class ServidorChat {
    private static final int PUERTO = 5000;
    private static Set<PrintWriter> clientes = new HashSet<>();

    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("Servidor de Chat iniciado en el puerto " + PUERTO);
        System.out.println("Esperando conexiones de clientes...");
        System.out.println("==========================================");

        try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Cliente conectado desde: " + socket.getInetAddress().getHostAddress());
                
                // Crear un hilo por cada cliente que se conecte
                new Thread(new ManejadorCliente(socket)).start();
            }
        } catch (IOException e) {
            System.err.println("Error en el servidor: " + e.getMessage());
        }
    }

    private static class ManejadorCliente implements Runnable {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;

        public ManejadorCliente(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                synchronized (clientes) {
                    clientes.add(out);
                }

                String mensaje;
                while ((mensaje = in.readLine()) != null) {
                    System.out.println("Mensaje recibido: " + mensaje);
                    // Reenviar el mensaje a todos los clientes
                    synchronized (clientes) {
                        for (PrintWriter cliente : clientes) {
                            cliente.println(mensaje);
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("Un cliente se ha desconectado.");
            } finally {
                if (out != null) {
                    synchronized (clientes) {
                        clientes.remove(out);
                    }
                }
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}