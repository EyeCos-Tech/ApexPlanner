
package com.eyecostech.apexplanner;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.imageio.ImageIO;

/************NO FUNCIONA***************/
public class Vista3DSimulada extends JFrame {
    private JLabel labelImagen;
    private JSlider slider;

    private String rutaBase;
    public Vista3DSimulada(String path) {
        rutaBase = path+"imagen"; // Carpeta y nombre base de las imágenes
        setTitle("Simulación 3D con Slider");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel para la imagen
        labelImagen = new JLabel();
        labelImagen.setHorizontalAlignment(SwingConstants.CENTER);
        add(labelImagen, BorderLayout.CENTER);

        // Slider
        slider = new JSlider(JSlider.HORIZONTAL, 0, 360, 0);
        slider.setMajorTickSpacing(45);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.addChangeListener(e -> actualizarImagen());

        add(slider, BorderLayout.SOUTH);

        // Cargar primera imagen
        actualizarImagen();

        setVisible(true);
    }

    private void actualizarImagen() {
        int angulo = slider.getValue()+1;
        String rutaImagen = rutaBase + angulo + ".png";

        try {
            labelImagen.setIcon(new ImageIcon(ImageIO.read(new File(rutaImagen))));
        } catch (Exception e) {
            labelImagen.setText("Imagen no encontrada: " + rutaImagen);
            System.err.println("No se pudo cargar la imagen: " + rutaImagen);
        }
    }
}