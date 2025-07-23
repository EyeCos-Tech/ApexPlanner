package com.eyecostech.apexplanner;

import java.util.Scanner;
import javax.swing.JFrame;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

/**
 * @author Pau Savall
 */
//@SpringBootApplication // ← SPRING BOOT
//@Component
public class Apexplanner {

    private static Apexplanner instancia;

    static int pow = 193; //potencia del laser
    static double ab = 0.00025; //ablacio del laser per disparo
    static double numeroShoots = 1;
    static BufferedImage imagen;
    //public static Calculador calc = new Calculador();
    public static Calculador calc;
    //static ImageAnalizer imageAnalizer = new ImageAnalizer();
    static ImageAnalizer imageAnalizer;
    static int[] pixel;
    static JFrame frame;
    //static CsvAnalizer csv = new CsvAnalizer();
    static CsvAnalizer csv;
    static ImageListCreator imageList;
    static FrameDeImagenes frameImg;
    //static Vista3DSimulada ventata3d;
    static Paciente paciente;
    static String path;
    //private ImageService imagenesService = new ImageService();
    private ImageService imagenesService;
    public static Laser laser;
    //List<List<Double>> matriz;

    //private Nomograma nomograma;
    public Apexplanner() {
        this.laser= new Laser();
        this.csv = new CsvAnalizer();
        this.imageAnalizer = new ImageAnalizer();
        this.calc = new Calculador();
        this.imagenesService = new ImageService();

    }

    public Apexplanner(String nombre) {
        System.out.println("=== CREANDO INSTANCIA DE APEXPLANNER ===");

        this.paciente = new Paciente(nombre);
        this.path = paciente.getPath();
        this.imagenesService = new ImageService();
        this.calc = new Calculador();
        this.imageAnalizer = new ImageAnalizer();
        this.csv = new CsvAnalizer();
        //matriz = csv.crearMatriz(path);
        this.laser= new Laser();
        System.out.println(path);

    }

/**
 * GETTERS Y SETTERES
 * */
    
    
    public static String getImagePath() {
        return paciente.getDirectorioImageList(paciente.getNombre());
    }

    public List<ImageIcon> getArrayImg() {
        String directorio = getImagePath();

        return imagenesService.cargarImagenes(directorio);
    }
    public static Apexplanner getInstance() {
        if (instancia == null) {
            instancia = new Apexplanner();
        }
        return instancia;
    }

    public static Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(String nombre) {
        paciente = new Paciente(nombre);
        path = paciente.getPath();
    }
    public static CsvAnalizer getCsv() {
        return csv;
    }

    public static String getPath() {
        return path;
    }

    public static FrameDeImagenes getFrameImg() {
        return frameImg;
    }

    public static void setFrameImg(FrameDeImagenes frameImg) {
        Apexplanner.frameImg = frameImg;
    }
    
    public static Laser getLaser() {
        return laser;
    }

    public static void setLaser(Laser laser) {
        Apexplanner.laser = laser;
    }

    public static ImageAnalizer getImageAnalizer() {
        return imageAnalizer;
    }

    public static void setImageAnalizer(ImageAnalizer imageAnalizer) {
        Apexplanner.imageAnalizer = imageAnalizer;
    }

    public static Calculador getCalc() {
        return calc;
    }

    public static void setCalc(Calculador calc) {
        Apexplanner.calc = calc;
    }

    public static int getPow() {
        return pow;
    }

    public static void setPow(int pow) {
        Apexplanner.pow = pow;
    }

    public void solicitudDeClaculos(Scanner sc,Laser laser) {

        System.out.println("Dame la cantidad de mm a ablacionar: ");
        String ablacionNecesaria = sc.nextLine();

            
            //String ablacionNecesaria= "0.90";//mm
            System.out.println("tegido a ablacionar: "+ablacionNecesaria);

            pow = calc.setPotenciaLaser(laser);
            System.out.println("potencia laser: "+pow);
            
            ab = calc.mmXShoot(laser);            
            System.out.println("ablacion x shot: "+ab);
            
            numeroShoots = calc.numeroShoots(ablacionNecesaria, ab);
            System.out.println("numero de shots: "+numeroShoots);

    
    }


    public void iniciarSpringboot(String[] args) {
        System.out.println("\n INICIANDO APLICACIÓN SPRING BOOT...");
        System.out.println("=====================================");
        /**
         * INICIAR SPRING BOOT *
         */
        SpringApplication.run(Apexplanner.class, args);
        System.out.println("\n APLICACIoN INICIADA CORRECTAMENTE");
        System.out.println("=====================================");
        System.out.println(" URL Principal: http://localhost:8080");
        System.out.println(" Documentacion: http://localhost:8080/info");
        System.out.println(" Para parar: Ctrl+C en consola");
        System.out.println("=====================================\n");

    }

    public FrameDeImagenes iniciarFrameSliderImagenes(String ruta) {
        String directorio = paciente.getDirectorioImageList(paciente.getNombre());
        frameImg = new FrameDeImagenes(directorio);
        return frameImg;
    }

    /*INICIA EL FRAME DE IMAGENES PERO SENSE TREURE EL FRAME A PANTALLA NOMER RETORNA UNA LISRT<iMAGEiCON>*/
    public FrameDeImagenes iniciarImagenes(String ruta) {
        String directorio = paciente.getDirectorioImageList(paciente.getNombre());
        frameImg = new FrameDeImagenes(directorio, true);
        return frameImg;
    }

    public static void crearLlistaImatges(String ruta) {
        try {

            System.out.println("CREATROR: " + ruta);
            imageList = new ImageListCreator(paciente.getDirectorioImageList(paciente.getNombre()), paciente);
            ArrayList<BufferedImage> lista = imageList.crearListaImagenes(imageList.getMatriz(), laser);
            imageList.guardarImagenes(lista);

        } catch (Exception e) {
            e.printStackTrace(); // mostra la linea on ha saltat l'error
        }
    }
}
