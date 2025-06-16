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
import org.bytedeco.opencv.opencv_core.MatVector;
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_core.Scalar;

/**
 * CARREGAR I ANALITZAR UNA MATRIU CSV EXTRETA DEL THREE_I_TECH AMB EL MAPA
 * TOPOGRAFIC D'ABLACIO EXTREURE'N ELS SHOOTS NECESSARIS PER LA CORRECCIO
 *
 * @author Pau Savall
 */
public class CsvAnalizer {

    public Calculador calc = new Calculador();
    public Mat image = null;

    public Mat getImage() {

        return image;
    }

    public void setImage(Mat image) {

    }
    

    /*crea una matriu*/
    public List<List<Double>> cerarMatriz(String path) {
        String csvPath = path;
        List<List<Double>> matrix = loadCSV(csvPath);
        if (matrix == null) {
            System.out.println("No se pudo cargar el archivo.");

        } else {
            System.out.println("Csv "+csvPath+ " carreget amb exit");
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

    public double getMaxValue(List<List<Double>> matrix) {
        double maxValue = 0.0;
        int rows = matrix.size();
        int cols = matrix.get(0).size();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                double value = matrix.get(i).get(j);
                System.out.println(value);

                if (value > maxValue) {
                    maxValue = value;
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
        //System.out.println("Ablacion en x= " + x + " y=  " + y + ": " + ablacion + " micras");
        return ablacion;
    }

    /*prepara una imatge en color representant la matriu del csv i retorna la  imatge*/
    public Mat prepararImagen(List<List<Double>> matrix) {
        //double ablacion;

        int rows = matrix.size();
        int cols = matrix.get(0).size();
        Mat image = new Mat(rows, cols, opencv_core.CV_8UC1);

//        // Escalar todos los valores (por ejemplo, de mm a µm) per traballar mes comodament
//        for (int y = 0; y < rows; y++) {
//            for (int x = 0; x < cols; x++) {
//                double valor = matrix.get(y).get(x);
//                matrix.get(y).set(x, valor * 1000); // Escala ×1000
//            }
//        }

        /*FET A PARTIR DELS VALORS (ablacio) DE CADA PUNT DE LA MATRIU*/
        // Escalado
//        double maxValue = matrix.stream()
//                .flatMap(List::stream)
//                .mapToDouble(Double::doubleValue)
//                .max().orElse(1.0);
//        for (int y = 0; y < rows; y++) {
//            for (int x = 0; x < cols; x++) {
//                int grayValue = (int) (255.0 * matrix.get(y).get(x) / maxValue);
//                grayValue = Math.min(255, Math.max(0, grayValue));
//                image.ptr(y, x).put((byte) grayValue);
//            }
//        }

        /*FET A PARTIR DELS NUMEROS DE SHOOT DE CADA PUNT DE LA MATRIU*/
        // Escalado
        double maxValue = calc.maxShoots(matrix);
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                int grayValue = (int) (255.0 * calc.numeroShoots(matrix.get(y).get(x), calc.mmXShoot(193)) / maxValue);
                grayValue = Math.min(255, Math.max(0, grayValue));
                image.ptr(y, x).put((byte) grayValue);
            }
        }
        System.out.println("el maxim de shoots que te algun punt de la matriu es : " + maxValue);

        // Colormap:c pasar la image de grisos a color
        Mat colorMap = new Mat();
        opencv_imgproc.applyColorMap(image, colorMap, opencv_imgproc.COLORMAP_JET);
        addIsobaras(image, colorMap, matrix, 20); // 10 isobaras
        image = colorMap;

//        double[] niveles = {0.0003,0.0008, 0.001,0.005,0.009,0.01,0.05,0.09,0.1,0.5,0.9,1.0}; // mm u otra unidad
//        //Scalar rojo = new Scalar(0.0, 0.0, 255.0, 0.0);  // B, G, R, Alpha
//        Scalar[] colores = {
//            
//            new Scalar(255.0, 0.0, 0.0,0.0), // Azul
//            new Scalar(0.0, 255.0, 0.0,0.0), // Verde
//            new Scalar(0.0, 0.0, 255.0,0.0), // Rojo
//            new Scalar(255.0, 255.0, 0.0,0.0), // Cyan
//            new Scalar(255.0, 0.0, 255.0,0.0) // Magenta
//        };
//        double alpha = 0.5;
        //addIsobarasPersonalizadas(image, colorMap, matrix, niveles, colores, alpha);
//        String windowName = "Mapa de Ablacion Corneal";
//        opencv_highgui.namedWindow(windowName, opencv_highgui.WINDOW_NORMAL);
        //opencv_highgui.resizeWindow(windowName, 800, 600);

        /*capturar punt de la imatge x/y i retorna valor. Dona lo mateix que el metode leerPunto()*/
        // Callback del mouse 
//        opencv_highgui.setMouseCallback(windowName, new MouseCallback() {
//            @Override
//            public void call(int event, int x, int y, int flags, Pointer userdata) {
//                if (event == opencv_highgui.EVENT_LBUTTONDOWN) {
//                    if (y < matrix.size() && x < matrix.get(0).size()) {
//                        double valor = matrix.get(y).get(x);
//
//                        // Redondea a 4 decimales
//                        BigDecimal bd = new BigDecimal(valor);
//                        bd = bd.setScale(4, RoundingMode.HALF_DOWN);
//                        double valorRedondo = bd.doubleValue();
//
//                        DecimalFormat df = new DecimalFormat("#.####");
//                        System.out.println("Click en: X=" + x + ", Y=" + y + " -> Valor: " + df.format(valorRedondo) + " mm");
//
//                        double mm = valorRedondo; //????????????
//
//                        String numero = String.valueOf(mm);
//                        calc.numeroShoots(numero, calc.mmXShoot(193));//calcula el numero de disparos que necessita per un laser de potencia estandard (193 de longitud d'ona)
//
//                    }
//                }
//            }
//        });
//        opencv_highgui.imshow(windowName, colorMap);
//        opencv_highgui.waitKey(0);
//        opencv_highgui.destroyAllWindows();
        this.setImage(image);

        return image;
    }

    /*mostra la imatge i imprimeix a consola la cantitat de disparos que has de fer en el punt on clickes */
    public void imprimirImagen(Mat imagen, List<List<Double>> matrix) {

        String windowName = "Mapa de Ablacion Corneal";
        opencv_highgui.namedWindow(windowName, opencv_highgui.WINDOW_NORMAL);
        imagen = prepararImagen(matrix);
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
                        double valorRedondo = bd.doubleValue();

                        DecimalFormat df = new DecimalFormat("#.####");
                        System.out.println("Click en: X=" + x + ", Y=" + y + " -> Valor: " + df.format(valorRedondo) + " mm");

                        double mm = valorRedondo; //????????????

                        String numero = String.valueOf(mm);
                        calc.numeroShoots(numero, calc.mmXShoot(193));//calcula el numero de disparos que necessita per un laser de potencia estandard (193 de longitud d'ona)

                    }
                }
            }
        });

        opencv_highgui.imshow(windowName, imagen);
        opencv_highgui.waitKey(0);
        opencv_highgui.destroyAllWindows();

    }

    public void addIsobaras(Mat grayImage, Mat colorImage, List<List<Double>> matrix, int numIsobaras) {
//        int rows = matrix.size();
//        int cols = matrix.get(0).size();

        // Obtindre valor maxim dels elements de la matriu
//        double maxValue = matrix.stream()
//                .flatMap(List::stream)
//                .mapToDouble(Double::doubleValue)
//                .max().orElse(1.0);

        /*passar aixo a disparos-> maxValue ha de ser maxShoots*/
        double maxValue = calc.maxShoots(matrix);
//                .flatMap(List::stream)
//                .mapToDouble(Double::doubleValue)
//                .max().orElse(1.0);

        for (int i = 1; i < numIsobaras; i++) {
            double thresholdValue = maxValue * i / numIsobaras;
            //System.out.println("maxValue: "+maxValue+"thresholdValue= "+thresholdValue+" i= "+i+" numeroIsobaras: "+numIsobaras);

            // Convertimos el valor de isobara a escala de grises [0-255]
            double thresholdGray = thresholdValue * 255.0 / maxValue;

            // Umbralizar imagen -> pasar els pixels a blanc o negre
            Mat thresholded = new Mat();
            opencv_imgproc.threshold(grayImage, thresholded, thresholdGray, 255, opencv_imgproc.THRESH_BINARY);

            // Encontrar contornos
            MatVector contours = new MatVector();
            Mat hierarchy = new Mat();
            opencv_imgproc.findContours(thresholded, contours, hierarchy, opencv_imgproc.RETR_EXTERNAL, opencv_imgproc.CHAIN_APPROX_SIMPLE);

            // Dibujar todos los contornos encontrados
            for (int j = 0; j < contours.size(); j++) {
                Scalar color = new Scalar(0, 0, 0, 0); //colo isobara(Negro)

                // Creamos una capa temporal (overlay)
                Mat overlay = colorImage.clone();

                // Dibujamos el contorno sobre la capa
                opencv_imgproc.drawContours(overlay, contours, j, color, 1, opencv_imgproc.LINE_8, hierarchy, 0, new Point());

                // Mezclamos (blend) la capa con la imagen original para simular línea más fina
                double alpha = 0.3;
                opencv_core.addWeighted(overlay, alpha, colorImage, 1.0 - alpha, 0, colorImage);
            }

        }
    }

    public void addIsobarasPersonalizadas(Mat grayImage, Mat colorImage, List<List<Double>> matrix, double[] niveles, Scalar[] colores, double alpha) {
        int rows = matrix.size();
        int cols = matrix.get(0).size();

        // Calcular valor máximo de la matriz
        double maxValue = matrix.stream()
                .flatMap(List::stream)
                .mapToDouble(Double::doubleValue)
                .max().orElse(1.0);

        for (int i = 0; i < niveles.length; i++) {
            double thresholdValue = niveles[i];

            // Convertir a escala de grises (0-255)
            double thresholdGray = thresholdValue * 255.0 / maxValue;

            // Umbralizar imagen
            Mat thresholded = new Mat();
            opencv_imgproc.threshold(grayImage, thresholded, thresholdGray, 255, opencv_imgproc.THRESH_BINARY);

            // Buscar contornos
            MatVector contours = new MatVector();
            Mat hierarchy = new Mat();
            opencv_imgproc.findContours(thresholded, contours, hierarchy,
                    opencv_imgproc.RETR_EXTERNAL, opencv_imgproc.CHAIN_APPROX_SIMPLE);

            // Obtener color correspondiente
            Scalar color = colores[i];

            // Dibujar cada contorno con alpha blending
            for (int j = 0; j < contours.size(); j++) {
                Mat overlay = colorImage.clone();

                opencv_imgproc.drawContours(overlay, contours, j, color, 1, opencv_imgproc.LINE_8, hierarchy, 0, new Point());

                opencv_core.addWeighted(overlay, alpha, colorImage, 1.0 - alpha, 0, colorImage);
            }
        }
    }

}
