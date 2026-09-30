package com.ejemplo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class ClienteApp {
    // Configuración por defecto basada en tu túnel de Pinggy
    private static final String DEFAULT_HOST = "trqwb-45-237-52-49.run.pinggy-free.link";
    private static final int DEFAULT_PUERTO = 46007;

    private JTextArea areaChat;
    private JTextField campoMensaje;
    private PrintWriter out;
    private String nombreUsuario;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ClienteApp().iniciar());
    }

    public void iniciar() {
        // 1. Pedir Host / Servidor
        Object hostInput = JOptionPane.showInputDialog(
            null, 
            "Ingresa el Host del Servidor:", 
            "Configuración del Host",
            JOptionPane.QUESTION_MESSAGE,
            null,
            null,
            DEFAULT_HOST
        );
        
        if (hostInput == null) System.exit(0); // Si presiona Cancelar, cierra la app
        String host = hostInput.toString().trim();
        if (host.isEmpty()) host = DEFAULT_HOST;

        // 2. Pedir Puerto
        Object puertoInput = JOptionPane.showInputDialog(
            null, 
            "Ingresa el Puerto:", 
            "Configuración del Puerto",
            JOptionPane.QUESTION_MESSAGE,
            null,
            null,
            String.valueOf(DEFAULT_PUERTO)
        );

        if (puertoInput == null) System.exit(0);
        String puertoStr = puertoInput.toString().trim();

        int puerto = DEFAULT_PUERTO;
        try {
            if (!puertoStr.isEmpty()) {
                puerto = Integer.parseInt(puertoStr);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Puerto no válido, usando " + DEFAULT_PUERTO + " por defecto.");
        }

        // 3. Pedir Nombre de Usuario
        nombreUsuario = JOptionPane.showInputDialog(
            null, 
            "Ingresa tu nombre de usuario:", 
            "Identificación", 
            JOptionPane.PLAIN_MESSAGE
        );

        if (nombreUsuario == null || nombreUsuario.trim().isEmpty()) {
            nombreUsuario = "Usuario_" + (int)(Math.random() * 1000);
        }

        crearInterfaz();
        conectarServidor(host, puerto);
    }

    private void crearInterfaz() {
        JFrame frame = new JFrame("Chat - " + nombreUsuario);
        frame.setSize(450, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        areaChat = new JTextArea();
        areaChat.setEditable(false);
        areaChat.setFont(new Font("Arial", Font.PLAIN, 14));
        areaChat.setLineWrap(true);
        areaChat.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(areaChat);

        campoMensaje = new JTextField(22);
        campoMensaje.setFont(new Font("Arial", Font.PLAIN, 14));
        
        JButton botonEnviar = new JButton("Enviar");
        botonEnviar.setBackground(new Color(70, 130, 180));
        botonEnviar.setForeground(Color.WHITE);

        botonEnviar.addActionListener(e -> enviarMensaje());
        campoMensaje.addActionListener(e -> enviarMensaje());

        JPanel panelInferior = new JPanel();
        panelInferior.add(campoMensaje);
        panelInferior.add(botonEnviar);

        frame.setLayout(new BorderLayout());
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(panelInferior, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private void conectarServidor(String host, int puerto) {
        new Thread(() -> {
            try {
                Socket socket = new Socket(host, puerto);
                out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                SwingUtilities.invokeLater(() -> 
                    areaChat.append(">>> Conectado exitosamente a " + host + ":" + puerto + " <<<\n\n")
                );

                out.println("*** " + nombreUsuario + " se ha unido al chat ***");

                String mensajeServidor;
                while ((mensajeServidor = in.readLine()) != null) {
                    String msg = mensajeServidor;
                    SwingUtilities.invokeLater(() -> {
                        areaChat.append(msg + "\n");
                        areaChat.setCaretPosition(areaChat.getDocument().getLength());
                    });
                }
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> 
                    areaChat.append(">>> Error: No se pudo conectar a " + host + ":" + puerto + " <<<\n")
                );
            }
        }).start();
    }

    private void enviarMensaje() {
        String texto = campoMensaje.getText().trim();
        if (!texto.isEmpty() && out != null) {
            out.println("[" + nombreUsuario + "]: " + texto);
            campoMensaje.setText("");
        }
    }
}