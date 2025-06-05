package com.eyecostech.apexplanner;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Scanner;
import javax.swing.JFrame;
import org.bytedeco.opencv.opencv_core.AbstractMat;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Pau Savall
 */
public class Apexplanner {

    static int pow = 193; //potencia del laser
    static double ab = 0.00025; //ablacio del laser per disparo
    static double numeroShoots = 1;
    static BufferedImage imagen;
    static Calculador calc = new Calculador();
    static ImageAnalizer imageAnalizer = new ImageAnalizer();
    static int[] pixel;
    static JFrame frame;
    static CsvAnalizer csv = new CsvAnalizer();
    static ShootPlanner sPlanner = new ShootPlanner();
    static ImageListCreator imageList;

    public void solicitudDeClaculos(Scanner sc) {
        System.out.println("Dame la longtud de onda del LASER: ");
        String potencia = sc.nextLine();
        System.out.println("Dame la cantidad de mm a ablacionar: ");
        String ablacionNecesaria = sc.nextLine();
        Double numero = null;

        while (numero == null) {
            try {
                numero = Double.parseDouble(ablacionNecesaria);
                System.out.println("Número convertido: " + numero);

            } catch (NumberFormatException e) {
                System.out.println("Error: la cadena no es un número válido.");
                System.out.println("Dame la cantidad de mm a ablacionar: ");
                ablacionNecesaria = sc.nextLine();
                numero = null;
                {
                }
            }

            pow = calc.setPotenciaLaser(potencia);
            ab = calc.mmXShoot(pow);
            numeroShoots = calc.numeroShoots(ablacionNecesaria, ab);

        }

    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        Apexplanner obj = new Apexplanner();

        /*CALCULATOR*/
        //obj.solicitudDeClaculos(sc);
//        /*IMAGE ANALIZER*/
//        frame = new JFrame();
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        imagen = imageAnalizer.cargarImagen("C:/Users/Usuario/WORCKSPACES/NetBeans26/apexplanner/img/gris.png"); // Cargar imagen
//        pixel= imageAnalizer.colorPixel(100, 150, imagen); //Diu el color en RGB del pixel 100/150
//        imageAnalizer.mostrarImagen(imagen, frame); //mostrar imatge i dir el color en RGB del pixel pulsat pel mouse
//        System.out.println("Codigo color pixel " + 100 + " " + 150 + ": \nRGB:" + pixel[0] + "/" + pixel[1] + "/" + pixel[2]);
//        
        /*CSV ANALIZER*/
        //List<List<Double>> matriz=  csv.cerarMatriz("C:/Users/Usuario/Documents/Topografias/OPD Scan III/Ari/fichero de Three/ablacion.csv");
        //List<List<Double>> matriz=  csv.cerarMatriz("C:/Users/Usuario/Documents/Topografias/OPD Scan III/Mica/csv/mica.csv");
        //List<List<Double>> matriz = csv.cerarMatriz("C:/Users/Usuario/Documents/Topografias/OPD Scan III/Sara/csv/sara.csv");
        //csv.leerPunto(150, 265, matriz);
//        List<Double> rutaLaser= sPlanner.ordenarXDistancia(matriz);
//        System.out.println("BREAK!");
//        System.out.println(rutaLaser.get(5).toString());
        //double valorMaximo = csv.getMaxValue(matriz);
        //System.out.println("Valor maximo: " + valorMaximo);
        //System.out.println("Numero total de disparos: "+calc.calcularNumeroShootsTotal(matriz));
        //System.out.println("total: "+calc.calcularNumeroShootsTotal(matriz));
        //System.out.println("Maxiomo numero de disparos: " +calc.numeroShoots(String.valueOf(valorMaximo), calc.mmXShoot(193)));
        //calc.maxShoots(matriz);
        //csv.mostrarImagen(matriz);
        //csv.imprimirImagen(csv.prepararImagen(matriz), matriz);
        sc.close();

        /*IMAGE LIST CREATOR: CREAR LLISTA D'IMATGES D'1 BIT AMB EL TRACTAMENT*/
        try {
            //imageList = new ImageListCreator("C:/Users/Usuario/Documents/Topografias/OPD Scan III/Ari/fichero de Three/ablacion.csv");
            imageList = new ImageListCreator("C:/Users/Usuario/Documents/Topografias/OPD Scan III/Mica/csv/mica.csv");
            ArrayList<BufferedImage> lista = imageList.crearListaimagenes(imageList.getMatriz());
            //System.out.println("break");
            imageList.guardarImagenes(lista);
            // Tu código aquí
        } catch (Exception e) {
            e.printStackTrace(); // Esto muestra en qué clase y línea ocurrió el error
        }

//        /*DISCO 1 BIT*/
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
//        // Guardar la imagen como PNG
//        File outputFile = new File("disco_1bit.png");
//        try {
//            ImageIO.write(image, "png", outputFile);
//            System.out.println("Imagen guardada en: " + outputFile.getAbsolutePath());
//        } catch (IOException e) {
//            System.err.println("Error al guardar la imagen: " + e.getMessage());
//        }
    }
}
