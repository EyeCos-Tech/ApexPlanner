package com.eyecostech.apexplanner;

import java.util.Scanner;
import javax.swing.JFrame;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * CLASE PRINCIPAL I MAIN CLASSE PRINCIPAL DEL SPRINGBOOT
 *
 * @author Pau Savall
 */
@SpringBootApplication // ← SPRING BOOT
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
    static FrameDeImagenes frameImg;
    static Vista3DSimulada ventata3d;
    //static String path= "C:/Users/Usuario/Documents/Topografias/OPD Scan III/Mica/csv/mica.csv";
    //static String path= "C:/Users/Usuario/Documents/Topografias/OPD Scan III/Sara/csv/sara.csv";
    static String path = "C:/Users/Usuario/Documents/Topografias/OPD Scan III/Ari/csv/ari.csv";
    //static String path= "C:/Users/Usuario/Documents/Topografias/OPD Scan III/Ari/fichero de Three/ablacion.csv";

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
                System.out.println("Error: El texto no es un número válido.");
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

    public void iniciarSpringboot(String[] args) {
        System.out.println("\n INICIANDO APLICACIÓN SPRING BOOT...");
        System.out.println("=====================================");
         /** INICIAR SPRING BOOT **/
        SpringApplication.run(Apexplanner.class, args);
        System.out.println("\n APLICACIoN INICIADA CORRECTAMENTE");
        System.out.println("=====================================");
        System.out.println(" URL Principal: http://localhost:8080");
        System.out.println(" Documentacion: http://localhost:8080/info");
        System.out.println(" Para parar: Ctrl+C en consola");
        System.out.println("=====================================\n");

    }
    public void iniciarFrameSliderImagenes(String ruta){
        File file = new File(path);
        String directorio = file.getParent() + "/imgList/";
        frameImg = new FrameDeImagenes(directorio);
    }

//    public void escribirLog() throws IOException {
//        try {
//            // Ruta relativa: se crea en la raíz del proyecto
//            FileWriter writer = new FileWriter("log.txt");
//            writer.write("Este es un mensaje de log.");
//            writer.close();
//            System.out.println("Log creado correctamente.");
//        } catch (IOException e) {
//            System.out.println("Error al escribir el archivo:");
//            e.printStackTrace();
//        }
//    }

    public static void main(String[] args) {
        Apexplanner obj = new Apexplanner();
        //obj.iniciarSpringboot(args);

        Scanner sc = new Scanner(System.in);
        
        


        /*CALCULATOR*/
//        obj.solicitudDeClaculos(sc);
//        /*IMAGE ANALIZER*/
//        frame = new JFrame();
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        imagen = imageAnalizer.cargarImagen("C:/Users/Usuario/WORCKSPACES/NetBeans26/apexplanner/img/gris.png"); // Cargar imagen
//        pixel= imageAnalizer.colorPixel(100, 150, imagen); //Diu el color en RGB del pixel 100/150
//        imageAnalizer.mostrarImagen(imagen, frame); //mostrar imatge i dir el color en RGB del pixel pulsat pel mouse
//        System.out.println("Codigo color pixel " + 100 + " " + 150 + ": \nRGB:" + pixel[0] + "/" + pixel[1] + "/" + pixel[2]);
        //System.out.println("per corretgir 4 dioptries: " + calc.mmXDioptria(5) + " mm d'ablacio"+"\nper tant: "+calc.numeroShoots(calc.mmXDioptria(5), calc.mmXShoot(pow))+" shoots");
        //System.out.println("per corretgir 4 dioptries: " + calc.mmXDioptriaFormulaMunnerlyn(-4, 6.5) + " mm d'ablacio sxegons la formula de Munnerlyn");
//        
        /*CSV ANALIZER*/
        //List<List<Double>> matriz = csv.cerarMatriz(path);
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
        //csv.imprimirImagen(csv.prepararImagen(matriz), matriz);
        /*IMAGE LIST CREATOR: CREAR LLISTA D'IMATGES D'1 BIT AMB EL TRACTAMENT*/
//        try {
//            
//            imageList = new ImageListCreator(path);
//            ArrayList<BufferedImage> lista = imageList.crearListaImagenes(imageList.getMatriz());
//            imageList.guardarImagenes(lista);
//            
//        } catch (Exception e) {
//            e.printStackTrace(); // mostra la linea on ha saltat l'error
//        }


        /*JFRAME CON SLIDER PARA MOSTRAR LAS IMAGENES*/
        obj.iniciarFrameSliderImagenes(path);
//      

        System.out.println("A ver si escribe");

        sc.close();

    }
}
