package com.eyecostech.apexplanner;

/**
 * ENCARREGAT DE CREAR I SERVIR LES IMATGES
 *
 * @author Pau Savall
 */
import static com.eyecostech.apexplanner.Apexplanner.calc;
import static com.eyecostech.apexplanner.Apexplanner.csv;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import javax.swing.ImageIcon;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.bytedeco.opencv.global.opencv_core;
import static org.bytedeco.opencv.global.opencv_core.CV_8UC1;
import static org.bytedeco.opencv.global.opencv_core.CV_8UC3;
import static org.bytedeco.opencv.global.opencv_core.CV_8UC4;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.global.opencv_imgcodecs;
//import org.bytedeco.opencv.helper.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.MatVector;
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_core.Scalar;


public class ImageService {

    public List<ImageIcon> cargarImagenes(String path) {
        List<ImageIcon> imagenes = new ArrayList<>();
        File folder = new File(path);

        if (!folder.exists()) {
            return imagenes; // Retorna lista vacía si no existe
        }

        // Lista temporal para ordenar
        List<File> archivos = new ArrayList<>();
        for (File file : folder.listFiles((dir, name)
                -> name.toLowerCase().endsWith(".png"))) {
            archivos.add(file);
        }

        // Ordenar por número
        archivos.sort((f1, f2) -> {
            int num1 = obtenerNumero(f1.getName());
            int num2 = obtenerNumero(f2.getName());
            return Integer.compare(num1, num2);
        });

        // Cargar imágenes
        for (File file : archivos) {
            ImageIcon icon = new ImageIcon(file.getAbsolutePath());
            imagenes.add(icon);
        }

        return imagenes;
    }

    private int obtenerNumero(String nombreArchivo) {
        String nombreSinExtension = nombreArchivo.split("\\.")[0];
        String numeroStr = nombreSinExtension.replaceAll("\\D+", "");
        return numeroStr.isEmpty() ? 0 : Integer.parseInt(numeroStr);
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

    public void pintarPunto(int x, int y, double valor, BufferedImage image, int index) {

        //double shoots = calc.numeroShoots(valor, calc.mmXShoot(193));
        double shoots = calc.numeroShoots(valor, calc.mmXShoot(new Laser()));
        //if (shoots > 0.0 && shoots <= index) { //pinta el fondo exterior del cercle blanc => te un pixel blanc al centre
        if (shoots >= 0.0 && shoots <= index) { //pinta el fondo exterior del cercle negre => no te un pixel blanc al centre
            image.setRGB(y, x, Color.BLACK.getRGB());
            //image.setRGB(x, y, Color.BLACK.getRGB());
            //System.out.println("blanc");
        } else {
            image.setRGB(y, x, Color.WHITE.getRGB());
            //System.out.println("negre");
        }
    }

    public BufferedImage crearImagen(List<List<Double>> matriz, int indice) {

        int rows = matriz.size();
        int cols = matriz.get(0).size();

        BufferedImage imagen = new BufferedImage(rows, cols, BufferedImage.TYPE_INT_ARGB);
        for (int i = 0; i <= cols - 1; i++) {

            for (int j = 0; j < rows - 1; j++) {

                double valor = csv.leerPunto(i, j, matriz);
                pintarPunto(i, j, valor, imagen, indice);

            }

        }

        /*forçar a negre el punt del centre*/
        int centroX = imagen.getWidth() / 2;
        int centroY = imagen.getHeight() / 2;
        // Si el punto del centro sigue siendo blanco, lo pintamos manualmente
        //imagen.setRGB(centroX, centroY, Color.WHITE.getRGB());
        //imagen.setRGB(centroX, centroY, Color.BLACK.getRGB());

        //pintarContorno(imagen, matriz); //necessari si es pinte l'exterior del cercle en blanc
        //lista.add(imagen);//guardar imatge 
        //            /*comprobar el colore del punt del centre*/
//            int centroX = imagen.getWidth() / 2;
//            int centroY = imagen.getHeight() / 2;
//            int rgb = imagen.getRGB(centroX, centroY);
//            System.out.println("Color en el centro (frame " + indice + "): " + Integer.toHexString(rgb));
        return imagen;

    }

    /*pintar el contorn de l'ull*/
    public void pintarContorno(BufferedImage image, List<List<Double>> matriz) {
        int altura = image.getHeight();
        int amplada = image.getWidth();
        int radio = 124; // ==========> Com extreure el radi de l'ull de la imatge?????????????
        //int radio = (int)  calcularRadio(matriz); 

        int centroX = amplada / 2;
        int centroY = altura / 2;

        // Obtener el contexto gráfico
        Graphics2D g2d = image.createGraphics();
        // Activar antialiasing para suavizar bordes
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Configurar color del borde del círculo
        g2d.setColor(Color.BLACK);
        //g2d.setColor(Color.WHITE);

        // Dibujar un óvalo (círculo) vacío
        g2d.drawOval(centroX - radio, centroY - radio, radio * 2, radio * 2);

        // Liberar recursos gráficos
        g2d.dispose();

    }

    public Mat crearImagenColor(List<List<Double>> matrix, Paciente paciente, Laser laser) {

        int rows = matrix.size();
        int cols = matrix.get(0).size();
        Mat image = new Mat(rows, cols, opencv_core.CV_8UC1);

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
        double maxValue = calc.maxShoots(matrix, laser);
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                int grayValue = (int) (255.0 * calc.numeroShoots(matrix.get(y).get(x), calc.mmXShoot(laser)) / maxValue);
                grayValue = Math.min(255, Math.max(0, grayValue));
                image.ptr(y, x).put((byte) grayValue);
            }
        }
        System.out.println("el maxim de shoots que te algun punt de la matriu es : " + maxValue);

        // Colormap:c pasar la image de grisos a color
        Mat colorMap = new Mat();
        opencv_imgproc.applyColorMap(image, colorMap, opencv_imgproc.COLORMAP_JET);
        //addIsobaras(image, colorMap, matrix, 20); // 10 isobaras
        image = colorMap;
        
        String rutaSalida = paciente.getDirectorioImg(paciente.getNombre()) +"/imgcolor_"+paciente.getNombre()+".jpg";
        System.out.println(rutaSalida);
        boolean resultado = opencv_imgcodecs.imwrite(rutaSalida, image);

        if (resultado) {
            System.out.println("✅ Imagen creada y guardada en: " + rutaSalida);
        } else {
            System.out.println("❌ No se pudo guardar la imagen.");
        }

        return image;
    }

    public void addIsobaras(Mat grayImage, Mat colorImage, List<List<Double>> matrix, int numIsobaras, Laser laser) {
//        int rows = matrix.size();
//        int cols = matrix.get(0).size();

        // Obtindre valor maxim dels elements de la matriu
//        double maxValue = matrix.stream()
//                .flatMap(List::stream)
//                .mapToDouble(Double::doubleValue)
//                .max().orElse(1.0);

        /*passar aixo a disparos-> maxValue ha de ser maxShoots*/
        double maxValue = calc.maxShoots(matrix, laser);
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

