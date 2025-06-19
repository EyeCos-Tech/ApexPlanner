

package com.eyecostech.apexplanner;

/**
 *
 * @author Pau Savall
 */
// Nueva clase ImagenesService.java
import javax.swing.ImageIcon;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImagenesService {
    
    public List<ImageIcon> cargarImagenes(String path) {
        List<ImageIcon> imagenes = new ArrayList<>();
        File folder = new File(path);
        
        if (!folder.exists()) {
            return imagenes; // Retorna lista vacía si no existe
        }
        
        // Lista temporal para ordenar
        List<File> archivos = new ArrayList<>();
        for (File file : folder.listFiles((dir, name) -> 
            name.toLowerCase().endsWith(".png"))) {
            archivos.add(file);
        }
        
        // Ordenar por número
        archivos.sort((f1, f2) -> {
            int num1 = obtenerNumero(f1.getName());
            int num2 = obtenerNumero(f2.getName());
            return Integer.compare(num1, num2);
        });
        
        // Cargar imágenes
        for (File file : archivos) {
            ImageIcon icon = new ImageIcon(file.getAbsolutePath());
            imagenes.add(icon);
        }
        
        return imagenes;
    }
    
    private int obtenerNumero(String nombreArchivo) {
        String nombreSinExtension = nombreArchivo.split("\\.")[0];
        String numeroStr = nombreSinExtension.replaceAll("\\D+", "");
        return numeroStr.isEmpty() ? 0 : Integer.parseInt(numeroStr);
    }
}