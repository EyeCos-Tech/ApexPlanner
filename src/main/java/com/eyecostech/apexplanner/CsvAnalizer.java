package com.eyecostech.apexplanner;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.global.opencv_highgui;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_highgui.MouseCallback;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.indexer.UByteIndexer;
import static org.bytedeco.opencv.global.opencv_core.CV_8UC1;
import static org.bytedeco.opencv.global.opencv_core.CV_8UC3;
import static org.bytedeco.opencv.global.opencv_core.CV_8UC4;
import static org.bytedeco.opencv.global.opencv_imgproc.medianBlur;
import org.bytedeco.opencv.opencv_core.MatVector;
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.opencv.core.CvType;

/**
 * CARREGAR I ANALITZAR UNA MATRIU CSV EXTRETA DEL THREE_I_TECH AMB EL MAPA
 * TOPOGRAFIC D'ABLACIO EXTREURE'N ELS SHOOTS NECESSARIS PER LA CORRECCIO
 *
 * @author Pau Savall
 */
public class CsvAnalizer {

    public Calculador calc = new Calculador();
    public Mat image = null;
    public String nombre;
    public ImageConverter converter = new ImageConverter();

    public CsvAnalizer(String nombre) {
        this.nombre = nombre;
    }

    public CsvAnalizer() {
        this.nombre = nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public void setImage(Mat image) {

    }

    /*crea una matriu*/
    public List<List<Double>> crearMatriz(String pathCsv) {
        System.out.println("path recibido: " + pathCsv);

        File csvFile = new File(pathCsv);

        // Verificar que el archivo existe
        if (!csvFile.exists()) {
            System.err.println("ERROR: El archivo no existe: " + pathCsv);
            return null;
        }

        List<List<Double>> matrix = loadCSV(pathCsv);
        if (matrix == null) {
            System.out.println("No se pudo cargar el archivo.");
        } else {
            System.out.println("CSV " + pathCsv + " cargado con éxito");
            System.out.println("Filas: " + matrix.size() + ", Columnas: "
                    + (matrix.isEmpty() ? 0 : matrix.get(0).size()));
        }
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
    public Mat prepararImagen(List<List<Double>> matrix, Paciente paciente) {

        Mat image = new ImageService().crearImagenColor(matrix, paciente);

        /*si necessito girar la imatge*/
//        BufferedImage imageBuff= converter.matToBufferedImage(image);
//        imageBuff= converter.rotarImagen(imageBuff, 180.0, false);
//        image= converter.bufferedImageToMat(imageBuff);
        return image;
    }

    public Mat prepararImagen(List<List<Double>> matrix) {

        //Mat image = new ImageService().crearImagenColor(matrix, paciente);

        /*si necessito girar la imatge*/
//        BufferedImage imageBuff= converter.matToBufferedImage(image);
//        imageBuff= converter.rotarImagen(imageBuff, 180.0, false);
//        image= converter.bufferedImageToMat(imageBuff);
        return image;
    }

    /*mostra la imatge i imprimeix a consola la cantitat de disparos que has de fer en el punt on clickes */
    public void imprimirImagen(Mat imagen, List<List<Double>> matrix) {

        String windowName = "Mapa de Ablacion Corneal: " + getNombre();
        opencv_highgui.namedWindow(windowName, opencv_highgui.WINDOW_NORMAL);

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
}
