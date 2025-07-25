package com.eyecostech.apexplanner.local;

import com.eyecostech.apexplanner.Apexplanner;
import com.eyecostech.apexplanner.ApexplannerApp;
import com.eyecostech.apexplanner.FrameDeImagenes;
import com.eyecostech.apexplanner.JFrameFront;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Scanner;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

/**
 *
 * @author Tests Savall
 */
public class Tests {

    public static Apexplanner apex;
    public static String paciente = "ari";

    public static void main(String[] args) {

        apex = new Apexplanner(paciente);
        apex.getCsv().setNombre(apex.getPaciente().getNombre());
        
        //JFrameFront front = new JFrameFront();

        System.out.println("path imagenes: " + apex.getImagePath());

        /*FRAME Y ARRAY DE IMAGENES*/
//        FrameDeImagenes frame = apex.iniciarImagenes(apex.getImagePath());
//        frame.cargarImagenes(apex.getImagePath());
//        List<ImageIcon> array = new FrameDeImagenes(apex.getImagePath(), true).getArrayImagenes(apex.getImagePath());
//        /*PROBA PER COMPROBAR EL CONTINGUT DE L'ARRAY*/
//        for (ImageIcon elemento : array) {
//            System.out.println(elemento+" 0 ");
//        }
//        System.out.println("numero elemetos array "+array.size());
//
//
        /*iniciar SPRINGBOOT*/
        //ApexplannerApp.main(args);
        //apex.iniciarSpringboot(args);
//        
//        /*CALCULATOR*/
//        Scanner sc = new Scanner(System.in);
//        apex.solicitudDeClaculos(sc, apex.getLaser());
//        sc.close();
//        
//        /*IMAGE ANALIZER*/
//          JFrame  frame = new JFrame();
//          frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//          BufferedImage imagen = apex.getImageAnalizer().cargarImagen("C:\\Users\\Usuario\\WORCKSPACES\\NetBeans26\\Apexplanner\\apexplanner\\resources\\img\\gris.png"); // Cargar imagen
//          int[] pixel= apex.getImageAnalizer().colorPixel(100, 150, imagen); //Diu el color en RGB del pixel 100/150
//          apex.getImageAnalizer().mostrarImagen(imagen, frame); //mostrar imatge i dir el color en RGB del pixel pulsat pel mouse
//          System.out.println("Codigo color pixel " + 100 + " " + 150 + ": \nRGB:" + pixel[0] + "/" + pixel[1] + "/" + pixel[2]);
//          System.out.println("per corretgir 4 dioptries: " + apex.getCalc().mmXDioptria(5) + " mm d'ablacio"+"\nper tant: "+apex.getCalc().numeroShoots(apex.getCalc().mmXDioptria(5), apex.getCalc().mmXShoot(apex.getLaser()))+" shoots");
//          System.out.println("per corretgir 4 dioptries: " + apex.getCalc().mmXDioptriaFormulaMunnerlyn(-4, 6.5) + " mm d'ablacio sxegons la formula de Munnerlyn");
//        

//        /*CSV ANALIZER*/
// 
//        /*CREAR MATRIZ*/
//        //List<List<Double>> matriz = apex.getCsv().crearMatriz(paciente.getDirectorioCsv());
//        List<List<Double>> matriz = apex.getCsv().crearMatriz(apex.getPaciente().getPath());
//        apex.getCsv().leerPunto(150, 265, matriz);
//        //List<Double> rutaLaser= sPlanner.ordenarXDistancia(matriz); /*CALCUL DE RUTA LASER NO IMPLEMENTAT ENCARA*/
//        double valorMaximo = apex.getCsv().getMaxValue(matriz);
//        System.out.println("Valor maximo: " + valorMaximo);
//        System.out.println("Numero total de disparos: " + apex.getCalc().calcularNumeroShootsTotal(matriz, apex.getLaser()));
//        System.out.println("total: " + apex.getCalc().calcularNumeroShootsTotal(matriz, apex.getLaser()));
//        System.out.println("Maxiomo numero de disparos: " + apex.getCalc().numeroShoots(String.valueOf(valorMaximo), apex.getCalc().mmXShoot(apex.getLaser())));
//        apex.getCalc().maxShoots(matriz, apex.getLaser());
//        
//        /*IMAGEN EN COLOR*/
//        apex.getCsv().imprimirImagen(apex.getCsv().prepararImagen(matriz, apex.getPaciente(), apex.getLaser()), matriz, apex.getLaser());
//        //apex.getCsv().imprimirImagen(apex.getCsv().prepararImagen(matriz,apex.getPaciente(),apex.getLaser()), matriz,apex.getLaser());

        /*IMAGE LIST CREATOR: CREAR LLISTA D'IMATGES D'1 BIT AMB EL TRACTAMENT*/
         apex.crearLlistaImatges(apex.getPath());
        
        /*FRAME AMB SLIDE*/
        apex.iniciarFrameSliderImagenes(apex.getPath());
    }

}
