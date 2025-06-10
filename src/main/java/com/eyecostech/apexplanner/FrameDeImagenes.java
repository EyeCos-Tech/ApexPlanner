package com.eyecostech.apexplanner;

import javax.swing.JFrame;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Pau Savall
 */
public class FrameDeImagenes extends JFrame {

    private List<ImageIcon> imagenes = new ArrayList<>();
    private JSlider slider;
    private PanelImagen panelImagen;

    public FrameDeImagenes(String path) {
        // Configuración básica de la ventana
        setTitle("Visor de Imágenes Superpuestas");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Cargar imágenes desde la carpeta "imagenes"
        cargarImagenes(path);

        // Panel para mostrar las imágenes
        panelImagen = new PanelImagen(imagenes);
        add(panelImagen, BorderLayout.CENTER);

        // Slider para cambiar entre imágenes
        slider = new JSlider(0, imagenes.size() - 1, 0);
        slider.setMajorTickSpacing(1);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.addChangeListener(e -> {
            int index = slider.getValue();
            panelImagen.setImagenIndex(index);
        });

        add(slider, BorderLayout.SOUTH);

        setLocationRelativeTo(null); // Centrar ventana
        setVisible(true);
    }

    private void cargarImagenes(String path) {
        File folder = new File(path); // Carpeta con tus .png
        if (!folder.exists()) {
            JOptionPane.showMessageDialog(this, "Carpeta 'imagenes' no encontrada.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Lista temporal para ordenar
        List<File> archivos = new ArrayList<>();
        for (File file : folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".png"))) {
            archivos.add(file);
        }

        // Ordenar por el número en el nombre: imagen0.png, imagen1.png, ...
        archivos.sort((f1, f2) -> {
            int num1 = obtenerNumero(f1.getName());
            int num2 = obtenerNumero(f2.getName());
            return Integer.compare(num1, num2);
        });

        // Cargar imágenes ya ordenadas
        for (File file : archivos) {
            ImageIcon icon = new ImageIcon(file.getAbsolutePath());
            imagenes.add(icon);
        }

        if (imagenes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No se encontraron imágenes PNG en la carpeta.", "Advertencia", JOptionPane.WARNING_MESSAGE);
        }
    }

// Método auxiliar para extraer el número del nombre del archivo
    private int obtenerNumero(String nombreArchivo) {
        String nombreSinExtension = nombreArchivo.split("\\.")[0]; // Ej: "imagen12"
        String numeroStr = nombreSinExtension.replaceAll("\\D+", ""); // Quita todo lo que no sea dígito
        return numeroStr.isEmpty() ? 0 : Integer.parseInt(numeroStr);
    }

}

class PanelImagen extends JPanel {

    private List<ImageIcon> imagenes;
    private int imagenIndex = 0;

    public PanelImagen(List<ImageIcon> imagenes) {
        this.imagenes = imagenes;
        setPreferredSize(new Dimension(500, 400));
        setBackground(Color.BLACK);
    }

    public void setImagenIndex(int index) {
        this.imagenIndex = index;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (imagenIndex >= 0 && imagenIndex < imagenes.size()) {
            Image image = imagenes.get(imagenIndex).getImage();
            int x = (getWidth() - image.getWidth(null)) / 2;
            int y = (getHeight() - image.getHeight(null)) / 2;
            g.drawImage(image, x, y, this);
        }
    }
}
