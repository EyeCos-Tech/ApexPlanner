/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.eyecostech.apexplanner;

/**
 *
 * @author Pau Savall
 */
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import org.bytedeco.opencv.opencv_core.Mat;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import static org.bytedeco.opencv.global.opencv_core.CV_8UC1;
import static org.bytedeco.opencv.global.opencv_core.CV_8UC3;
import static org.bytedeco.opencv.global.opencv_core.CV_8UC4;

public class ImageConverter {

    public BufferedImage matToBufferedImage(Mat mat) {
        OpenCVFrameConverter.ToMat converterToMat = new OpenCVFrameConverter.ToMat();
        Java2DFrameConverter converterToBufferedImage = new Java2DFrameConverter();

        Frame frame = converterToMat.convert(mat);
        BufferedImage bufferedImage = converterToBufferedImage.convert(frame);
        return bufferedImage;

        // Los convertidores se pueden reutilizar, pero es buena práctica cerrarlos
        // cuando ya no los necesites en tu aplicación
    }

    public static Mat bufferedImageToMat(BufferedImage image) {
        Mat mat = null;

        // Determinar el tipo de Mat según el tipo de BufferedImage
        int type = -1;
        int channels = 0;

        switch (image.getType()) {
            case BufferedImage.TYPE_BYTE_GRAY:
                type = CV_8UC1;
                channels = 1;
                break;
            case BufferedImage.TYPE_3BYTE_BGR:
                type = CV_8UC3;
                channels = 3;
                break;
            case BufferedImage.TYPE_4BYTE_ABGR:
                type = CV_8UC4;
                channels = 4;
                break;
            case BufferedImage.TYPE_INT_RGB:
            case BufferedImage.TYPE_INT_BGR:
                // Convertir a BGR
                BufferedImage converted = new BufferedImage(image.getWidth(),
                        image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
                converted.getGraphics().drawImage(image, 0, 0, null);
                image = converted;
                type = CV_8UC3;
                channels = 3;
                break;
            case BufferedImage.TYPE_INT_ARGB:
                // Convertir a ABGR
                BufferedImage convertedAlpha = new BufferedImage(image.getWidth(),
                        image.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
                convertedAlpha.getGraphics().drawImage(image, 0, 0, null);
                image = convertedAlpha;
                type = CV_8UC4;
                channels = 4;
                break;
            default:
                throw new IllegalArgumentException("Tipo de imagen no soportado: " + image.getType());
        }

        // Crear Mat
        mat = new Mat(image.getHeight(), image.getWidth(), type);

        // Obtener datos de la imagen
        byte[] data = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();

        // Si es ABGR, necesitamos convertir a BGRA para OpenCV
        if (channels == 4) {
            byte[] bgra = new byte[data.length];
            for (int i = 0; i < data.length; i += 4) {
                bgra[i] = data[i + 1];     // B
                bgra[i + 1] = data[i + 2]; // G
                bgra[i + 2] = data[i + 3]; // R
                bgra[i + 3] = data[i];     // A
            }
            mat.data().put(bgra);
        } else {
            // Para otros formatos, copiar directamente
            mat.data().put(data);
        }

        return mat;
    }

    public BufferedImage aplicarTransparencia(BufferedImage imagen, float alpha) {
        BufferedImage resultado = new BufferedImage(
                imagen.getWidth(),
                imagen.getHeight(),
                BufferedImage.TYPE_INT_ARGB
        );

        Graphics2D g2d = resultado.createGraphics();

        // Establecer transparencia
        g2d.setComposite(AlphaComposite.getInstance(
                AlphaComposite.SRC_OVER, alpha
        ));

        // Dibujar imagen con transparencia
        g2d.drawImage(imagen, 0, 0, null);
        g2d.dispose();

        return resultado;
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

    /**
     * @param original Imagen a rotar
     * @param grados Grados de rotación (positivo o negativo)
     * @param sentidoHorario true para rotar en sentido horario, false para
     * antihorario
     */
    public BufferedImage rotarImagen(BufferedImage original, double grados, boolean sentidoHorario) {
        // Convertir grados a radianes
        double radianes = Math.toRadians(grados);

        // Si es sentido horario, invertir el ángulo
        if (sentidoHorario) {
            radianes = -radianes;
        }

        // Calcular dimensiones de la imagen rotada
        double sin = Math.abs(Math.sin(radianes));
        double cos = Math.abs(Math.cos(radianes));

        int w = original.getWidth();
        int h = original.getHeight();

        // Nuevo tamaño para contener la imagen rotada completa
        int newWidth = (int) Math.floor(w * cos + h * sin);
        int newHeight = (int) Math.floor(h * cos + w * sin);

        // Crear imagen con nuevo tamaño
        BufferedImage rotada = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = rotada.createGraphics();

        // Configurar alta calidad
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        // Fondo transparente
        g2d.setComposite(AlphaComposite.Clear);
        g2d.fillRect(0, 0, newWidth, newHeight);
        g2d.setComposite(AlphaComposite.SrcOver);

        // Trasladar al centro de la nueva imagen
        g2d.translate(newWidth / 2.0, newHeight / 2.0);

        // Rotar
        g2d.rotate(radianes);

        // Dibujar imagen centrada
        g2d.drawImage(original, -w / 2, -h / 2, null);
        g2d.dispose();

        return rotada;
    }

    public BufferedImage invertirImagen(BufferedImage img) {
        AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
        tx.translate(-img.getWidth(), 0);
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        return op.filter(img, null);
    }

}
