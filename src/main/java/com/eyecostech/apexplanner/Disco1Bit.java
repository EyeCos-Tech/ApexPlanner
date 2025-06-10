//
//package com.eyecostech.apexplanner;
//import javax.imageio.ImageIO;
//import java.awt.image.BufferedImage;
//import java.awt.Color;
//import java.io.File;
//import java.io.IOException;
//
//public class Disco1Bit {
//
//    public static void main(String[] args) {
//        int size = 200; // Tamaño de la imagen
//        int radius = 80; // Radio del disco
//        int center = size / 2;
//
//        // Crear imagen en blanco y negro (1 bit por píxel no es soportado directamente, se usa escala de grises)
//        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_BYTE_BINARY);
//
//        for (int y = 0; y < size; y++) {
//            for (int x = 0; x < size; x++) {
//                int dx = x - center;
//                int dy = y - center;
//                if (dx * dx + dy * dy <= radius * radius) {
//                    image.setRGB(x, y, Color.WHITE.getRGB());
//                } else {
//                    image.setRGB(x, y, Color.BLACK.getRGB());
//                }
//            }
//        }
//
//        // Guardar la imagen como PNG
//        File outputFile = new File("disco_1bit.png");
//        try {
//            ImageIO.write(image, "png", outputFile);
//            System.out.println("Imagen guardada en: " + outputFile.getAbsolutePath());
//        } catch (IOException e) {
//            System.err.println("Error al guardar la imagen: " + e.getMessage());
//        }
//    }
//}
