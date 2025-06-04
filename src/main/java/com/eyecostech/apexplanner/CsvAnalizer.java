package com.eyecostech.apexplanner;

import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.global.opencv_highgui;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_highgui.MouseCallback;

import java.io.BufferedReader;
import java.io.FileReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import org.bytedeco.javacpp.Pointer;

/**
 * CARREGAR I ANALITZAR UNA MATRIU CSV EXTRETA DEL THREE_I_TECH AMB 
 * EL MAPA TOPOGRAFIC D'ABLACIO
 * EXTREURE'N ELS SHOOTS NECESSARIS PER LA CORRECCIO
 * 
 * @author Pau Savall
 */
public class CsvAnalizer {
    public Calculador calc= new Calculador();

    /*crea una matriu*/
    public List<List<Double>> cerarMatriz(String path) {
        String csvPath = path;
        List<List<Double>> matrix = loadCSV(csvPath);
        if (matrix == null) {
            System.out.println("No se pudo cargar el archivo.");

        }

//        int rows = matrix.size();
//        int cols = matrix.get(0).size();
        return matrix;
    }

    /*carrega un csv i el transforma en matriu*/
    private static List<List<Double>> loadCSV(String filePath) {
        List<List<Double>> data = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String headerLine = br.readLine(); // Saltar encabezados
            String line;
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(",");
                List<Double> row = new ArrayList<>();
                for (String t : tokens) {
                    row.add(Double.parseDouble(t.trim()));
                }
                data.add(row);
            }
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public double getMaxValue(List<List<Double>> matrix){
        double maxValue = 0.0;
        int rows = matrix.size();
        int cols = matrix.get(0).size();
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                double value= matrix.get(i).get(j);
                System.out.println(value);
                
                if(value>maxValue){
                    maxValue= value;
                    //calc.numeroShoots(String.valueOf(maxValue), 197);
                }
            }
        }
        
        return maxValue;
        
    }

    /*Llegir un punt segons coordenada- dona lo mateix que capturant punt amb mouse a la imatge*/
    public double leerPunto(int x, int y, List<List<Double>> matrix) {
        // Leer ablación en X=0.5, Y=fila 100
        int filaY = y;
        int columnaX = x; // por ejemplo, X=0.5 está en columna 5

        double ablacion = matrix.get(filaY).get(columnaX);
        System.out.println("Ablacion en x= " + x + " y=  " + y + ": " + ablacion + " micras");
        return ablacion;
    }

    /*Mostra una imatge en color representant la matriu del csv i retorna la cantitat de disparos que has de fer en el punt on clickes cvu*/
    public void mostrarImagen(List<List<Double>> matrix) {
        //double ablacion;

        int rows = matrix.size();
        int cols = matrix.get(0).size();
        Mat image = new Mat(rows, cols, opencv_core.CV_8UC1);

        // Escalado
        double maxValue = matrix.stream()
                .flatMap(List::stream)
                .mapToDouble(Double::doubleValue)
                .max().orElse(1.0);

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                int grayValue = (int) (255.0 * matrix.get(y).get(x) / maxValue);
                grayValue = Math.min(255, Math.max(0, grayValue));
                image.ptr(y, x).put((byte) grayValue);
            }
        }

        // Colormap:pasar al image de grisos a color
        Mat colorMap = new Mat();
        opencv_imgproc.applyColorMap(image, colorMap, opencv_imgproc.COLORMAP_JET);

        String windowName = "Mapa de Ablacion Corneal";
        opencv_highgui.namedWindow(windowName, opencv_highgui.WINDOW_NORMAL);
        //opencv_highgui.resizeWindow(windowName, 800, 600);

        /*capturar punt de la imatge x/y i retorna valor. Dona lo mateix que el metode leerPunto()*/
        // Callback del mouse 
        opencv_highgui.setMouseCallback(windowName, new MouseCallback() {
            @Override
            public void call(int event, int x, int y, int flags, Pointer userdata) {
                if (event == opencv_highgui.EVENT_LBUTTONDOWN) {
                    if (y < matrix.size() && x < matrix.get(0).size()) {
                        double valor = matrix.get(y).get(x);
                        
                        // Redondea a 4 decimales
                        BigDecimal bd = new BigDecimal(valor);
                        bd = bd.setScale(4, RoundingMode.HALF_DOWN); 
                        double valorRedondo= bd.doubleValue();
                        
                        DecimalFormat df = new DecimalFormat("#.####");
                        System.out.println("Click en: X=" + x + ", Y=" + y + " -> Valor: " +df.format(valorRedondo) + " mm");
                        
                        double mm= valorRedondo; //????????????
                        
                        String numero= String.valueOf(mm);
                        calc.numeroShoots(numero, calc.mmXShoot(193));//calcula el numero de disparos que necessita per un laser de potencia estandard (193 de longitud d'ona)
                                                
                    }
                }
            }
        });

        opencv_highgui.imshow(windowName, colorMap);
        opencv_highgui.waitKey(0);
        opencv_highgui.destroyAllWindows();
    }
}

//
// ¿Qué hace este código?
//Lee el archivo CSV ignorando encabezados.
//
//Convierte la matriz de ablación en una imagen en escala de grises.
//
//Aplica un colormap (COLORMAP_JET) para mejor visualización.
//
//Muestra la imagen con OpenCV.
//
//Permite leer la ablación en una coordenada específica.
//
//✅ Requisitos
//Java 11 o superior.
//
//Archivo CSV con formato como el que me compartiste.
//
//Una IDE (como IntelliJ o Eclipse) con soporte para Maven.
