package com.ejemplo;

import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class App {
    public static void main(String[] args) {
        // Ejecutar la interfaz en el hilo del evento de Swing
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Mi App sin Maven");
            frame.setSize(400, 200);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null); // Centrar en pantalla

            JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
            panel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
            JLabel label = new JLabel("¡Hola desde VS Code!");
            JButton button = new JButton("Presióname");

            button.addActionListener(e -> label.setText("¡Funciona perfecto!"));

            panel.add(label);
            panel.add(button);
            frame.add(panel);

            frame.setVisible(true);
        });
    }
}