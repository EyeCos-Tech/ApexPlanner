package com.eyecostech.apexplanner;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

/**
 *
 * CREADOR DE UNA LISTA DE IMAGENES DE 1 BIT CON 0= NO SHOOT 1= SHOOTLOS
 * DISPAROS
 *
 * TAL I COM ESTA EL LASER HA DE DISPARAR ON ESTA PINTAT DE BLANC EN CADA IMATGE
 * DINS ARRIBAR AL FINA ON TOT ES NEGRE I PER TANT NO HA DE DISPARAR ENLLOC
 *
 *
 * @author Pau Savall
 */
public class ImageListCreator {

    public String path;
    public CsvAnalizer csv = new CsvAnalizer();
    public ArrayList<BufferedImage> listaImagenes;
    public Calculador calc;
    List<List<Double>> matriz;

    public ImageListCreator(String path) {  //constructor
        this.path = path;
        this.calc = new Calculador();

        matriz = csv.cerarMatriz(path);
    }

//    int size = 200; // Tamaño de la imagen
    //BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_BYTE_BINARY);
    public List<List<Double>> getMatriz() {
        return this.matriz;
    }

    public ArrayList<BufferedImage> crearListaimagenes(List<List<Double>> matriz) {
        int rows = matriz.size();
        int cols = matriz.get(0).size();
        double max = calc.maxShoots(matriz);
        System.out.println("max= " + max);
        ArrayList<BufferedImage> lista = new ArrayList<BufferedImage>();
        int indice = 0;
        int ind = 0;

        for (int k = 0; k < max; k++) { //repeteix fins al maxim dde shots que te el punt que te mes shoots
            BufferedImage imagen = new BufferedImage(rows, cols, BufferedImage.TYPE_BYTE_BINARY);
            //System.out.println("BREAK");

            for (int i = 0; i <= cols - 1; i++) {

                for (int j = 0; j < rows - 1; j++) {

                    double valor = csv.leerPunto(i, j, matriz);
                    pintarPunto(i, j, valor, imagen, indice);

                    //pintarPunto(i, j, csv.leerPunto(i, j, matriz), imagen);
//                BufferedImage imagen= BufferedImage();
//                lista.add(imagen);
                }
            }

            indice++;//pintar
            pintarContorno(imagen);
            lista.add(imagen);
            //guardarImagen(imagen, ind);
            ind++;
        }
        return lista;
    }

    /*pintar el contorn de l'ull*/
    public void pintarContorno(BufferedImage image) {
        int altura = image.getHeight();
        int amplada = image.getWidth();
        int radio = 124; // ==========> Com extreure el radi de l'ull de la imatge?????????????

        int centroX = amplada / 2;
        int centroY = altura / 2;

        // Obtener el contexto gráfico
        Graphics2D g2d = image.createGraphics();

        // Configurar color del borde del círculo
        g2d.setColor(Color.BLACK);

        // Dibujar un óvalo (círculo) vacío
        g2d.drawOval(centroX - radio, centroY - radio, radio * 2, radio * 2);

        // Liberar recursos gráficos
        g2d.dispose();

    }

    public void pintarPunto(int x, int y, double valor, BufferedImage image, int index) {

        double shoots = calc.numeroShoots(valor, calc.mmXShoot(193));
        if (shoots > 0.0 && shoots <= index) {
            image.setRGB(x, y, Color.BLACK.getRGB());
            //System.out.println("blanc");
        } else {
            image.setRGB(y, x, Color.WHITE.getRGB());
            //System.out.println("negre");
        }
    }

    public void guardarImagenes(ArrayList<BufferedImage> lista) {
        System.out.println("BREAK guardar imagenes");
        BufferedImage img;

        File csvFile = new File(path); // path apunta al .csv
        File parentDir = csvFile.getParentFile(); // obtiene la carpeta que contiene el .csv
        File dir = new File(parentDir, "imgList"); // crea carpeta imgList en esa ruta

        if (!dir.exists()) {
            dir.mkdirs(); // Crea el directorio (y subdirectorios si es necesario)
        }

        int indice = 0;
        //for (BufferedImage img : lista) {
        for (int i = 0; i < lista.size(); i++) {
            indice++;
            File outputFile = new File(dir, "imagen" + indice + ".png");
            img = lista.get(i);

            try {
                ImageIO.write(img, "png", outputFile);
                //System.out.println("Imagen guardada en: " + outputFile.getAbsolutePath());
            } catch (IOException ex) {
                System.getLogger(ImageListCreator.class.getName())
                        .log(System.Logger.Level.ERROR, "Error al guardar imagen" + indice, ex);
            }
        }

    }

    public void guardarImagen(BufferedImage img, int indice) {
        System.out.println("BREAK guardar imagenes");

        File csvFile = new File(path); // path apunta al .csv
        File parentDir = csvFile.getParentFile(); // obtiene la carpeta que contiene el .csv
        File dir = new File(parentDir, "imgList"); // crea carpeta imgList en esa ruta
        if (!dir.exists()) {
            dir.mkdirs(); // Crea el directorio (y subdirectorios si es necesario)
        }

        //for (BufferedImage img : lista) {
        File outputFile = new File(dir, "imagen" + indice + ".png");
        indice++;

        try {
            ImageIO.write(img, "png", outputFile);
            System.out.println("Imagen guardada en: " + outputFile.getAbsolutePath());
        } catch (IOException ex) {
            System.getLogger(ImageListCreator.class.getName())
                    .log(System.Logger.Level.ERROR, "Error al guardar imagen " + indice, ex);
        }

    }

}


/*PINTAR EL CONTORNO*/
 /*
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class CirculoVacio {
    public static void main(String[] args) {
        int size = 200;
        int radius = 80;
        int centerX = size / 2;
        int centerY = size / 2;

        // Crear imagen
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);

        // Obtener el contexto gráfico
        Graphics2D g2d = image.createGraphics();

        // Rellenar fondo blanco
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, size, size);

        // Configurar color del borde del círculo
        g2d.setColor(Color.BLACK);

        // Dibujar un óvalo (círculo) vacío
        g2d.drawOval(centerX - radius, centerY - radius, radius * 2, radius * 2);

        // Liberar recursos gráficos
        g2d.dispose();

        // Guardar la imagen
        try {
            ImageIO.write(image, "png", new File("circulo_vacio.png"));
            System.out.println("Imagen guardada correctamente.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}*/
 /*
ArrayList<BufferedImage> imagenes = new ArrayList<>();

// Agregar imágenes dinámicamente
BufferedImage img1 = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
imagenes.add(img1);

BufferedImage img2 = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);
imagenes.add(img2);

// Acceder a una imagen
BufferedImage primera = imagenes.get(0);

// Tamaño actual
System.out.println("Cantidad de imágenes: " + imagenes.size());

 */
