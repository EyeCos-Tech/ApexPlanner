package com.eyecostech.apexplanner;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import marvin.image.MarvinImage;
import marvin.io.MarvinImageIO;
import org.bytedeco.opencv.opencv_core.Mat;

/**
 *
 * CREADOR DE UNA LISTA DE IMAGENES DE 1 BIT CON 0= NO SHOOT 1= SHOOT
 *
 * TAL I COM ESTA EL LASER HA DE DISPARAR ON ESTA PINTAT DE BLANC EN CADA IMATGE
 * DINS ARRIBAR AL FINAL ON TOT ES NEGRE I PER TANT NO HA DE DISPARAR ENLLOC
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
    public ImageConverter converter;
    public Paciente paciente;

    public ImageListCreator(String path, Paciente paciente) {  //constructor
        this.path = path;
        this.calc = new Calculador();
        this.paciente = paciente;

        matriz = csv.crearMatriz(paciente.getDirectorioCsv());
        converter = new ImageConverter();
    }

    public List<List<Double>> getMatriz() {
        return this.matriz;
    }

    public ArrayList<BufferedImage> crearListaImagenes(List<List<Double>> matriz) {

        int rows = matriz.size();
        int cols = matriz.get(0).size();
        double max = calc.maxShoots(matriz);
        System.out.println("max= " + max);
        ArrayList<BufferedImage> lista = new ArrayList<BufferedImage>();
        int indice = 0;
        int ind = 0;

        for (int k = 0; k < max; k++) { //repeteix fins al maxim dde shots que te el punt que te mes shoots
            BufferedImage imagen = new BufferedImage(rows, cols, BufferedImage.TYPE_INT_ARGB);
            imagen = new ImageService().crearImagen(matriz, indice);
            //imagen = new ImageConverter().aplicarTransparencia(imagen, indice);

            indice++;//pintar
            
            imagen= converter.invertirImagen(imagen);

            //lista.add(girarImagen90Izquierda(imagen)); //guardar imatge rotada
            lista.add(converter.rotarImagen(imagen, 90.0, true)); //guardar imatge rotada
            //guardarImagen(imagen, ind);
            ind++;

        }

        return lista;
    }

    public ArrayList<Mat> crearListaImagenesMat(List<List<Double>> matriz) {

        int rows = matriz.size();
        int cols = matriz.get(0).size();
        double max = calc.maxShoots(matriz);
        System.out.println("max= " + max);
        //ArrayList<BufferedImage> lista = new ArrayList<BufferedImage>();
        ArrayList<Mat> listaMat = new ArrayList<Mat>();
        int indice = 0;
        int ind = 0;

        for (int k = 0; k < max; k++) { //repeteix fins al maxim dde shots que te el punt que te mes shoots
            BufferedImage imagen = new BufferedImage(rows, cols, BufferedImage.TYPE_INT_ARGB);
            imagen = new ImageService().crearImagen(matriz, indice);
            //Mat imagenMat = new ImageService().bufferedImageToMat(girarImagen90Izquierda(imagen));
            //Mat imagenMat = new ImageConverter().bufferedImageToMat(converter.girarImagen90Izquierda(imagen));
            Mat imagenMat = new ImageConverter().bufferedImageToMat(converter.rotarImagen(imagen, 90.0, true));

            indice++;//pintar

            listaMat.add(imagenMat); //guardar imatge rotada
            //guardarImagen(imagen, ind);
            ind++;

        }

        return listaMat;
    }

    public void crearTXTInstrucciones(String path, String texto) {
        // Texto que quieres escribir
        String mensaje = texto;

        // Crear la carpeta si no existe
        File carpeta = new File(path);
        if (!carpeta.exists()) {
            carpeta.mkdirs();
        }

        // Crear archivo dentro de esa carpeta
        File archivo = new File(carpeta, "instrucciones.txt");

        try (FileWriter writer = new FileWriter(archivo)) {
            writer.write(mensaje);
            System.out.println("Archivo creado en: " + archivo.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error al escribir el archivo: " + e.getMessage());
        }

    }

    public void guardarImagenes(ArrayList<BufferedImage> lista) {
        System.out.println("BREAK guardar imagenes");
        BufferedImage img;

        // Usar directamente el path que ya es el directorio imgList
        File dir = new File(path);

        if (!dir.exists()) {
            dir.mkdirs();
        }

        int indice = 0;
        for (int i = 0; i < lista.size(); i++) {
            indice++;
            File outputFile = new File(dir, "imagen" + indice + ".png");
            img = lista.get(i);

            try {
                ImageIO.write(img, "png", outputFile);
            } catch (IOException ex) {
                System.getLogger(ImageListCreator.class.getName())
                        .log(System.Logger.Level.ERROR, "Error al guardar imagen" + indice, ex);
            }
        }
        crearTXTInstrucciones(dir.getAbsolutePath(),
                "¡Disparar en las áreas en blanco!\nLongitud de onda del laser 193");

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

    public void recortarFondoImagen(String path, int indice) {
        MarvinImage image = MarvinImageIO.loadImage(path);

        int width = image.getWidth();
        int height = image.getHeight();

        int minX = width, minY = height, maxX = 0, maxY = 0;

        // Usar umbral para detectar "contenido oscuro"
        int threshold = 128;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int gray = image.getIntComponent0(x, y);
                //if (gray < threshold) { // detectar contenido oscuro
                if (gray > threshold) { // detectar contenido claro
                    if (x < minX) {
                        minX = x;
                    }
                    if (y < minY) {
                        minY = y;
                    }
                    if (x > maxX) {
                        maxX = x;
                    }
                    if (y > maxY) {
                        maxY = y;
                    }
                }
            }
        }

        // Validar si se detectó contenido
        if (maxX < minX || maxY < minY) {
            System.out.println("No se encontró contenido oscuro para recortar.");
            return;
        }

        int cropWidth = maxX - minX + 1;
        int cropHeight = maxY - minY + 1;

        MarvinImage croppedImage = new MarvinImage(cropWidth, cropHeight);

        for (int y = 0; y < cropHeight; y++) {
            for (int x = 0; x < cropWidth; x++) {
                int gray = image.getIntComponent0(x + minX, y + minY);
                //int alpha = (gray > 240) ? 0 : 255; // blanco se vuelve transparente
                int alpha = (gray < 15) ? 0 : 255; // megro se vuelve transparente
                croppedImage.setIntColor(x, y, alpha, gray, gray, gray);
            }
        }

        // Guardar imagen recortada
        File csvFile = new File(path); // path apunta al .csv
        File parentDir = csvFile.getParentFile(); // obtiene la carpeta que contiene el .csv
        File dir = new File(parentDir, "negro_transparente"); // crea carpeta imgList en esa ruta

        if (!dir.exists()) {
            dir.mkdirs(); // Crea el directorio (y subdirectorios si es necesario)
        }
        MarvinImageIO.saveImage(croppedImage, dir + "/imagen_recortada" + indice + ".png");
    }

    public static double calcularRadio(List<List<Double>> matriz) {
        if (matriz == null || matriz.isEmpty()) {
            return 0;
        }

        double sumX = 0, sumY = 0;
        for (List<Double> p : matriz) {
            sumX += p.get(0);
            sumY += p.get(1);
        }

        double centroX = sumX / matriz.size();
        double centroY = sumY / matriz.size();

        double sumDist = 0;
        for (List<Double> p : matriz) {
            double dx = p.get(0) - centroX;
            double dy = p.get(1) - centroY;
            sumDist += Math.sqrt(dx * dx + dy * dy);
        }

        return sumDist / matriz.size(); // radio promedio
    }

    public BufferedImage girarImagen90Izquierda(BufferedImage original) {
        int width = original.getWidth();
        int height = original.getHeight();

        // Crear nueva imagen con dimensiones invertidas
        BufferedImage imagenGirada = new BufferedImage(height, width, original.getType());

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // Mover píxeles: columna -> fila inversa
                imagenGirada.setRGB(y, width - 1 - x, original.getRGB(x, y));
            }
        }

        return imagenGirada;

    }

}
