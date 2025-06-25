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

    public ImageListCreator(String path) {  //constructor
        this.path = path;
        this.calc = new Calculador();

        matriz = csv.crearMatriz(path);
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

            indice++;//pintar

            lista.add(girarImagen90Izquierda(imagen)); //guardar imatge rotada
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
            Mat imagenMat = new ImageConverter().bufferedImageToMat(girarImagen90Izquierda(imagen));

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
            //System.out.println(outputFile.getAbsolutePath());
            //recortarFondoImagen(outputFile.getAbsolutePath(), indice); /*LA BONA*/
            //recortarSoloFondoBlancoExteriorRobusto(outputFile.getAbsolutePath(), indice);

        }
        crearTXTInstrucciones(dir.getAbsolutePath(), "¡Disparar en las áreas en blanco!\nLongitud de onda del laser 193");

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

}
//   public void pintarPunto(int x, int y, double valor, BufferedImage image, int index) {
//
//        double shoots = calc.numeroShoots(valor, calc.mmXShoot(193));
//        //if (shoots > 0.0 && shoots <= index) { //pinta el fondo exterior del cercle blanc => te un pixel blanc al centre
//        if (shoots >= 0.0 && shoots <= index) { //pinta el fondo exterior del cercle negre => no te un pixel blanc al centre
//            image.setRGB(y, x, Color.BLACK.getRGB());
//            //image.setRGB(x, y, Color.BLACK.getRGB());
//            //System.out.println("blanc");
//        } else {
//            image.setRGB(y, x, Color.WHITE.getRGB());
//            //System.out.println("negre");
//        }
//    }
//    public ArrayList<BufferedImage> crearListaImagenes(List<List<Double>> matriz) {
//    //        int rows = matriz.get(0).size();
////        int cols = matriz.size();
//        int rows = matriz.size();
//        int cols = matriz.get(0).size();
//        double max = calc.maxShoots(matriz);
//        System.out.println("max= " + max);
//        ArrayList<BufferedImage> lista = new ArrayList<BufferedImage>();
//        int indice = 0;
//        int ind = 0;
//
//        for (int k = 0; k < max; k++) { //repeteix fins al maxim dde shots que te el punt que te mes shoots
//            //BufferedImage imagen = new BufferedImage(rows, cols, BufferedImage.TYPE_BYTE_BINARY);
//            BufferedImage imagen = new BufferedImage(rows, cols, BufferedImage.TYPE_INT_ARGB);
//            //BufferedImage imagen = new BufferedImage( cols, rows,BufferedImage.TYPE_BYTE_BINARY);
//            //System.out.println("BREAK");
//
//            for (int i = 0; i <= cols - 1; i++) {
//
//                for (int j = 0; j < rows - 1; j++) {
//
//                    double valor = csv.leerPunto(i, j, matriz);
//                    pintarPunto(i, j, valor, imagen, indice);
//
//                    //pintarPunto(i, j, csv.leerPunto(i, j, matriz), imagen);
////                BufferedImage imagen= BufferedImage();
////                lista.add(imagen);
//                }
//            }
//
//            indice++;//pintar
//
//            /*forçar a negre el punt del centre*/
//            int centroX = imagen.getWidth() / 2;
//            int centroY = imagen.getHeight() / 2;
//            // Si el punto del centro sigue siendo blanco, lo pintamos manualmente
//            //imagen.setRGB(centroX, centroY, Color.WHITE.getRGB());
//            //imagen.setRGB(centroX, centroY, Color.BLACK.getRGB());
//
//            //pintarContorno(imagen, matriz); //necessari si es pinte l'exterior del cercle en blanc
//
//            //lista.add(imagen);//guardar imatge 
//            lista.add(girarImagen90Izquierda(imagen)); //guardar imatge rotada
//            //guardarImagen(imagen, ind);
//            ind++;
////            /*comprobar el colore del punt del centre*/
////            int centroX = imagen.getWidth() / 2;
////            int centroY = imagen.getHeight() / 2;
////            int rgb = imagen.getRGB(centroX, centroY);
////            System.out.println("Color en el centro (frame " + indice + "): " + Integer.toHexString(rgb));
//        }
//
//        return lista;
//    }
    
    /*EN TEORIA PER PINTAR NOMES EL CERCLE I LO DE FORA PINTAR-HO TRANSPARENT
    NO FUNCIONA*/
//public ArrayList<BufferedImage> crearListaImagenes(List<List<Double>> matriz) {
//        int rows = matriz.size();
//        int cols = matriz.get(0).size();
//        double max = calc.maxShoots(matriz);
//        ArrayList<BufferedImage> lista = new ArrayList<>();
//
//        int centroX = cols / 2;
//        int centroY = rows / 2;
//        //double radio = calc.radioCirculo(); // Define el radio del círculo
//        double radio = 193; // Define el radio del círculo
//
//        for (int k = 0; k < max; k++) {
//            BufferedImage imagen = new BufferedImage(cols, rows, BufferedImage.TYPE_INT_ARGB);
//
//            // Fondo transparente
//            Graphics2D g = imagen.createGraphics();
//            g.setComposite(AlphaComposite.Clear);
//            g.fillRect(0, 0, cols, rows);
//            g.setComposite(AlphaComposite.SrcOver);
//            g.dispose();
//
//            for (int y = 0; y < rows; y++) {
//                for (int x = 0; x < cols; x++) {
//                    double distancia = Math.sqrt(Math.pow(x - centroX, 2) + Math.pow(y - centroY, 2));
//                    if (distancia <= radio) {
//                        double valor = csv.leerPunto(y, x, matriz);
//                        double shoots = calc.numeroShoots(valor, calc.mmXShoot(193));
//                        if (shoots > 0.0 && shoots <= k) {
//                            imagen.setRGB(x, y, new Color(0, 0, 0, 255).getRGB()); // negro opaco
//                        } else {
//                            imagen.setRGB(x, y, new Color(255, 255, 255, 255).getRGB()); // blanco opaco dentro círculo
//                        }
//                    } else {
//                        imagen.setRGB(x, y, new Color(0, 0, 0, 0).getRGB()); // transparente fuera círculo
//                    }
//                }
//            }
//
//            // Punto central transparente (si quieres)
//            imagen.setRGB(centroX, centroY, new Color(0, 0, 0, 0).getRGB());
//
//            pintarContorno(imagen, matriz);
//
//            lista.add(imagen);
//        }
//
//        return lista;
//    }
//    public void pintarPunto(int x, int y, double valor, BufferedImage image, int index) {
//        double shoots = calc.numeroShoots(valor, calc.mmXShoot(193));
//        if (shoots > 0.0 && shoots <= index) {
//            image.setRGB(x, y, new Color(0, 0, 0, 255).getRGB()); // negro opaco
//        } else {
//            image.setRGB(x, y, new Color(0, 0, 0, 0).getRGB()); // transparente en vez de blanco
//        }
//    }
    
    
    
    
    
    
    
    

//    public void recortarSoloFondoBlancoExterior(String path, int indice) {
//        MarvinImage image = MarvinImageIO.loadImage(path);
//        int width = image.getWidth();
//        int height = image.getHeight();
//
//        boolean[][] isBackground = new boolean[width][height];
//        Queue<int[]> queue = new LinkedList<>();
//
//        // Umbral más bajo (fondo blanco "sucio")
//        int threshold = 240;
//
//        // Agrega bordes a la cola si son suficientemente claros
//        for (int x = 0; x < width; x++) {
//            if (esBlanco(image, x, 0, threshold)) {
//                queue.add(new int[]{x, 0});
//            }
//            if (esBlanco(image, x, height - 1, threshold)) {
//                queue.add(new int[]{x, height - 1});
//            }
//        }
//        for (int y = 0; y < height; y++) {
//            if (esBlanco(image, 0, y, threshold)) {
//                queue.add(new int[]{0, y});
//            }
//            if (esBlanco(image, width - 1, y, threshold)) {
//                queue.add(new int[]{width - 1, y});
//            }
//        }
//
//        // Flood fill
//        while (!queue.isEmpty()) {
//            int[] p = queue.poll();
//            int x = p[0], y = p[1];
//            if (x < 0 || x >= width || y < 0 || y >= height) {
//                continue;
//            }
//            if (isBackground[x][y]) {
//                continue;
//            }
//            if (!esBlanco(image, x, y, threshold)) {
//                continue;
//            }
//
//            isBackground[x][y] = true;
//
//            queue.add(new int[]{x + 1, y});
//            queue.add(new int[]{x - 1, y});
//            queue.add(new int[]{x, y + 1});
//            queue.add(new int[]{x, y - 1});
//        }
//
//        // Bounding box del contenido
//        int minX = width, minY = height, maxX = 0, maxY = 0;
//        for (int y = 0; y < height; y++) {
//            for (int x = 0; x < width; x++) {
//                if (!isBackground[x][y]) {
//                    if (x < minX) {
//                        minX = x;
//                    }
//                    if (y < minY) {
//                        minY = y;
//                    }
//                    if (x > maxX) {
//                        maxX = x;
//                    }
//                    if (y > maxY) {
//                        maxY = y;
//                    }
//                }
//            }
//        }
//
//        if (maxX < minX || maxY < minY) {
//            System.out.println("No se encontró contenido para recortar.");
//            return;
//        }
//
//        int cropWidth = maxX - minX + 1;
//        int cropHeight = maxY - minY + 1;
//        MarvinImage cropped = new MarvinImage(cropWidth, cropHeight);
//
//        for (int y = 0; y < cropHeight; y++) {
//            for (int x = 0; x < cropWidth; x++) {
//                int origX = x + minX;
//                int origY = y + minY;
//
//                int r = image.getIntComponent0(origX, origY);
//                int g = image.getIntComponent1(origX, origY);
//                int b = image.getIntComponent2(origX, origY);
//
//                int alpha = isBackground[origX][origY] ? 0 : 255;
//                cropped.setIntColor(x, y, alpha, r, g, b);
//            }
//        }
//
//        File imgFile = new File(path);
//        File parentDir = imgFile.getParentFile();
//        File dir = new File(parentDir, "recortado");
//
//        if (!dir.exists()) {
//            dir.mkdirs();
//        }
//
//        File outputFile = new File(dir, "imagen_recortada_sinfondo_" + indice + ".png");
//        MarvinImageIO.saveImage(cropped, outputFile.getAbsolutePath());
//        System.out.println("Imagen guardada en: " + outputFile.getAbsolutePath());
//    }
//
//    // Función auxiliar para detectar blancos "sucios"
//    private boolean esBlanco(MarvinImage image, int x, int y, int threshold) {
//        int r = image.getIntComponent0(x, y);
//        int g = image.getIntComponent1(x, y);
//        int b = image.getIntComponent2(x, y);
//        return r > threshold && g > threshold && b > threshold;
//    }


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
