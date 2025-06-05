package com.eyecostech.apexplanner;

import java.awt.BorderLayout;
import java.awt.color.ColorSpace;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import org.bytedeco.opencv.opencv_core.Mat;
import org.opencv.core.CvType;

/**
 *
 * CARREGAR I ANALITZAR UNA IMATGE TRETA D'UNA TOPOGRAFIA SABER EL CODI DE COLOR
 * D'UN PIXEL DE LA IMATGE CLICKAT AMB EL MOUSE
 *
 * @author Pau Savall
 */
public class ImageAnalizer {

    /*CARREGA IMATGE*/
    public BufferedImage cargarImagen(String ruta) {
        BufferedImage imagen = null;

        try {
            imagen = ImageIO.read(new File(ruta));
            if (imagen != null) {
                System.out.println("Imagen cargada");
            } else {
                System.out.println("Imagen NO cargada");
            }
        } catch (IOException e) {
            System.out.println("Error al cargar la imagen: " + e.getMessage());
        }

        return imagen;
    }

    /*CONVERTEIX A ESCALA GRISOS UNA IMATGE*/
    public BufferedImage convertiraGris(BufferedImage imagen) {
        BufferedImage imagenGris = new BufferedImage(
                imagen.getWidth(),
                imagen.getHeight(),
                BufferedImage.TYPE_BYTE_GRAY
        );

        ColorConvertOp op = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
        op.filter(imagen, imagenGris);

        try {
            File archivoSalida = new File("img/gris.png");

            // Asegúrate de que la carpeta exista
            archivoSalida.getParentFile().mkdirs();

            // Guardar la imagen en formato JPG
            ImageIO.write(imagenGris, "png", archivoSalida);

            System.out.println("Imagen guardada exitosamente en: " + archivoSalida.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return imagenGris;

    }

    /*DIU COLOR DEL PIXEL X,Y*/
    public int[] colorPixel(int x, int y, BufferedImage imagen) {
        Scanner sc = new Scanner(System.in);

        // Obtener el valor del píxel (ARGB)
        int pixel = imagen.getRGB(x, y);

        // Components de color:
        int alpha = (pixel >> 24) & 0xff;
        int rojo = (pixel >> 16) & 0xff;
        int verde = (pixel >> 8) & 0xff;
        int azul = pixel & 0xff;

        if (!(rojo == verde && verde == azul)) {
            System.out.println("""
                               La Imagen debe ser en escala de Grises!
                               Quieres convertirla? s/n""");
            String respuesta = sc.nextLine();

            if (respuesta.equals("s")) {

                BufferedImage gris = convertiraGris(imagen);
                //System.out.println("BREAK");
                int[] color = colorPixel(x, y, gris);
                return color;
            } else {
                System.out.println("!!!!!!!!!");
            }
        }

        int[] rgb = {rojo, verde, azul};
        sc.close();

        return rgb;
    }

    /*MOSTRA LA IMATGE I DIU COLOR DEL PIXEL PULSAT*/
    public void mostrarImagen(BufferedImage imagen, JFrame frame) {

        frame.setSize(400, 600);
        System.out.println("BREAK");
        JLabel imagen1 = new JLabel(new ImageIcon(imagen));
        frame.getContentPane().add(imagen1, BorderLayout.LINE_START);
        BufferedImage finalImagen = imagen; // para usar dentro del listener
        imagen1.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();

                // Validar que el clic esté dentro de la imagen
                if (x < finalImagen.getWidth() && y < finalImagen.getHeight()) {
                    int pixel = finalImagen.getRGB(x, y);
                    int r = (pixel >> 16) & 0xff;
                    int g = (pixel >> 8) & 0xff;
                    int b = pixel & 0xff;

                    System.out.println("Clic en pixel (" + x + ", " + y + ") - RGB: " + r + ", " + g + ", " + b);
                } else {
                    System.out.println("Clic fuera de los límites de la imagen");
                }
            }
        });

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

    }

    /*CREAR I RETORNAN UNA IMATGE DE DISPAROS A PARTIR D'UNA IMATGE TOPOGRAFICA*/
    public Mat crearImagenShoots(Mat imagen) {

        return null;
    }

    public Mat deBufferedImageaMat(BufferedImage imagen) {
        // Tipo de imagen
        int type = imagen.getType();
        if (type != BufferedImage.TYPE_3BYTE_BGR) {
            // Convertir a 3BYTE_BGR si es necesario
            BufferedImage converted = new BufferedImage(imagen.getWidth(), imagen.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
            converted.getGraphics().drawImage(imagen, 0, 0, null);
            imagen = converted;
        }

        byte[] pixels = ((DataBufferByte) imagen.getRaster().getDataBuffer()).getData();
        Mat mat = new Mat(imagen.getHeight(), imagen.getWidth(), CvType.CV_8UC3);
        //mat.put(0, 0, pixels); AREGLAR!!!!
        return mat;
    }

}
