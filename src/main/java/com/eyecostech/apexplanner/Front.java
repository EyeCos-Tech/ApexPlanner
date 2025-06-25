/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.eyecostech.apexplanner;

/**
 *
 * @author Pau Savall
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class Front extends JFrame {

    private JLabel imageLabel1;
    private JLabel imageLabel2;
    private JSlider slider;
    private JTextField potenciaField;
    private JComboBox<String> laserSelector;
    private JButton calcularButton;
    private JTextField clienteField;
    private ImageIcon isobaras;
    private ImageIcon imagenConSlider;
    private CsvAnalizer csv;
    private ImageConverter converter;
    

    public Front() {
        setTitle("Interfaz con imágenes y controles");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        isobaras = new ImageIcon();
        imagenConSlider= new ImageIcon();
        
        csv= new CsvAnalizer();
        converter= new ImageConverter();

        // Panel superior para inputs
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(4, 2));

        // Campo Cliente
        topPanel.add(new JLabel("Cliente:"));
        clienteField = new JTextField();
        topPanel.add(clienteField);

        // Selector de Láser
        topPanel.add(new JLabel("Láser:"));
        String[] lasers = {"CO2", "Fibra", "YAG", "Diodo"};
        laserSelector = new JComboBox<>(lasers);
        topPanel.add(laserSelector);

        // Potencia
        topPanel.add(new JLabel("Potencia:"));
        potenciaField = new JTextField();
        topPanel.add(potenciaField);

        // Botón Calcular
        calcularButton = new JButton("Calcular");
        calcularButton.addActionListener(this::calcularAction);
        topPanel.add(new JLabel(""));
        topPanel.add(calcularButton);

        // Añadir panel superior
        add(topPanel, BorderLayout.NORTH);

        // Panel central con dos imágenes
        JPanel centerPanel = new JPanel(new GridLayout(1, 2));

        // Imagen 1 + Slider
        JPanel panel1 = new JPanel(new BorderLayout());
        //imagenConSlider.setImage(Apexplanner.getFrameImg());
        imageLabel1 = new JLabel("Imagen 1", SwingConstants.CENTER);
        imageLabel1.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        //slider = new JSlider(0, 100, 50);
        panel1.add(imageLabel1, BorderLayout.CENTER);
        panel1.add(slider, BorderLayout.SOUTH);

        // Imagen 2 (sin slider)
        JPanel panel2 = new JPanel(new BorderLayout());
        isobaras.setImage((Image)converter.matToBufferedImage(csv.prepararImagen(csv.crearMatriz(Apexplanner.getPath()))));
        imageLabel2 = new JLabel(isobaras, SwingConstants.CENTER);
        imageLabel2.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        panel2.add(imageLabel2, BorderLayout.CENTER);

        // Añadir paneles de imagen al centro
        centerPanel.add(panel1);
        centerPanel.add(panel2);

        add(centerPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private void calcularAction(ActionEvent e) {
        String cliente = clienteField.getText();
        String laser = (String) laserSelector.getSelectedItem();
        int potencia = 0;

        try {
            potencia = Integer.parseInt(potenciaField.getText());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ingrese una potencia válida.");
            return;
        }

        // Aquí puedes implementar la lógica de cálculo
        JOptionPane.showMessageDialog(this,
                "Cliente: " + cliente +
                "\nLáser: " + laser +
                "\nPotencia: " + potencia +
                "\nCálculo realizado.");

        // Cargar imagen de ejemplo (opcional)
        loadImage(imageLabel1, "imagen1.jpg"); // Ruta relativa o absoluta
        loadImage(imageLabel2, "imagen2.jpg");
    }

    private void loadImage(JLabel label, String path) {
        File file = new File(path);
        if (file.exists()) {
            ImageIcon icon = new ImageIcon(path);
            Image img = icon.getImage().getScaledInstance(label.getWidth(), label.getHeight(), Image.SCALE_SMOOTH);
            label.setIcon(new ImageIcon(img));
        } else {
            label.setText("Archivo no encontrado: " + path);
            label.setIcon(null);
        }
    }

//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(Front::new);
//    }
}