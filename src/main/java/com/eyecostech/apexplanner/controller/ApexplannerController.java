/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.eyecostech.apexplanner.controller;

import com.eyecostech.apexplanner.Apexplanner;
import jakarta.annotation.PostConstruct;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.Mat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * CONTROLADOR PRINCIPAL DE LA APLICACIÓN
 *
 * Este controlador maneja todas las peticiones HTTP - GET: - POST: - PUT: -
 * DELETE:
 *
 * @RestController = @Controller + @ResponseBody Todos los métodos devuelven
 * datos directamente (JSON/texto)
 * @author Pau Savall
 *
 *
 */
// @RestController: Combina @Controller + @ResponseBody
// Indica que esta clase maneja peticiones HTTP y retorna JSON automáticamente
// @RequestMapping: Define la ruta base para todas las APIs de este controlador
// Todas las rutas de este controlador empezarán con "/api/***"
// @CrossOrigin: Permite peticiones desde otros dominios (CORS)
// origins = "http://localhost:3000" permite peticiones desde React


@RestController
public class ApexplannerController {

    @Autowired
    private Apexplanner planner;  // Spring inyecta la instancia

    private String path;

    @PostConstruct
    public void init() {
        this.path = planner.getPath();
    }

    /**
     * RUTA PRINCIPAL - PÁGINA DE INICIO
     *
     * URL: http://localhost:8080/
     */
    @GetMapping("/")
    public String paginaInicio() {
        String fechaHora = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
        );
        return """
        <!DOCTYPE html>
        <html lang="es">
        <head>
            <meta charset="UTF-8">
            <title>Mi Primera App Spring Boot</title>
            <style>
                body { 
                    font-family: Arial, sans-serif; 
                    margin: 40px;
                    background-color: #f5f5f5; 
                }
                
                .main-wrapper {
                    display: flex;
                    gap: 20px;
                    max-width: 1200px;
                    margin: 0 auto;
                }
                
                .container { 
                    flex: 1;
                    background: white;
                    padding: 30px; 
                    border-radius: 10px;
                    box-shadow: 0 2px 10px rgba(0,0,0,0.1); 
                }
                
                .image-container {
                    width: 350px;
                    background: white;
                    padding: 20px;
                    border-radius: 10px;
                    box-shadow: 0 2px 10px rgba(0,0,0,0.1);
                }
                
                .image-container h3 {
                    color: #2c3e50;
                    margin-top: 0;
                    margin-bottom: 15px;
                }
                
                .image-wrapper {
                    width: 100%;
                    min-height: 200px;
                    background: #f0f0f0;
                    border-radius: 5px;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    overflow: hidden;
                }
                
                .image-wrapper img {
                    max-width: 100%;
                    max-height: 400px;
                    object-fit: contain;
                }
                
                .image-placeholder {
                    color: #999;
                    text-align: center;
                }
                
                h1 { 
                    color: #2c3e50; 
                }
                
                .info { 
                    background: #e8f4f8; 
                    padding: 15px;
                    border-radius: 5px; 
                    margin: 20px 0; 
                }
                
                a { 
                    color: #3498db; 
                    text-decoration: none; 
                }
                
                .load-image-btn {
                    margin-top: 15px;
                    padding: 10px 20px;
                    background: #3498db;
                    color: white;
                    border: none;
                    border-radius: 5px;
                    cursor: pointer;
                    width: 100%;
                    font-size: 16px;
                }
                
                .load-image-btn:hover {
                    background: #2980b9;
                }
                
                .loading {
                    color: #3498db;
                    text-align: center;
                }
                
                .error {
                    color: #e74c3c;
                    text-align: center;
                    padding: 10px;
                    background: #ffe6e6;
                    border-radius: 5px;
                    margin-top: 10px;
                }
            </style>
        </head>
        <body>
            <div class="main-wrapper">
                <div class="container">
                    <h1>PROBA APP SPRINGBOAT</h1>
                    <div class="info">
                        <h3>Estado de la Aplicación</h3>
                        <p><strong>Estado:</strong> Funcionando correctamente</p>
                        <p><strong>Servidor:</strong> Apache Tomcat (embebido)</p>
                        <p><strong>Puerto:</strong> 8080</p>
                        <p><strong>Framework:</strong> Spring Boot 3.3.0</p>
                        <p><strong>Cargado el:</strong> <span id="loadTime"></span></p>
                    </div>
                </div>
                
                <div class="image-container">
                    <h3>Imatge amb Isobares</h3>
                    <div class="image-wrapper" id="imageWrapper">
                        <div class="image-placeholder">
                            <p>Haz clic en el botón para cargar una imagen</p>
                        </div>
                    </div>
                    <button class="load-image-btn" onclick="cargarImagen()">Carregar Imatge</button>
                    <div id="errorMessage"></div>
                </div>
            
                <div class="image-container">
                    <h3>Array Imatges</h3>
                    <div class="image-wrapper" id="imageArrayWrapper">
                        <div class="image-placeholder">
                            <p>Haz clic en el botón para cargar un array</p>
                        </div>
                    </div>
                    <button class="load-image-btn" onclick="cargarArray()">Carregar Array</button>
                    <div id="errorArrayMessage"></div>
                </div>
            </div>
            
            <script>
                // Mostrar la hora de carga
                document.getElementById('loadTime').textContent = new Date().toLocaleString('es-ES');
                
                function cargarImagen() {
                    const imageWrapper = document.getElementById('imageWrapper');
                    const errorDiv = document.getElementById('errorMessage');
                    
                    // Limpiar mensajes de error anteriores
                    errorDiv.innerHTML = '';
                    
                    // Mostrar estado de carga
                    imageWrapper.innerHTML = '<div class="loading">Cargando imagen...</div>';
                    
                    // Realizar petición al backend
                    fetch('/api/isobaras')
                        .then(response => {
                            if (!response.ok) {
                                throw new Error('Error al cargar la imagen');
                            }
                            return response.blob();
                        })
                        .then(blob => {
                            const imageUrl = URL.createObjectURL(blob);
                            imageWrapper.innerHTML = `<img src="${imageUrl}" alt="Imagen del proyecto">`;
                        })
                        .catch(error => {
                            console.error('Error:', error);
                            imageWrapper.innerHTML = '<div class="image-placeholder"><p>No se pudo cargar la imagen</p></div>';
                            errorDiv.innerHTML = '<div class="error">Error: Verifica que el endpoint /api/isobaras esté configurado en Spring Boot</div>';
                        });
                }
                
                // Variables para navegación
                let currentIndex = 0;
                let totalImages = 0;
                
                // Función principal para cargar el array
                function cargarArray() {
                    console.log("Iniciando carga de array...");
                    
                    fetch('/api/array/count')
                        .then(response => {
                            console.log("Respuesta count:", response);
                            return response.json();
                        })
                        .then(count => {
                            console.log("Total de imágenes:", count);
                            totalImages = count;
                            if (count > 0) {
                                mostrarImagen(0);
                            } else {
                                const imageWrapper = document.getElementById('imageArrayWrapper');
                                imageWrapper.innerHTML = '<div class="image-placeholder"><p>No hay imágenes disponibles</p></div>';
                            }
                        })
                        .catch(error => {
                            console.error('Error al obtener count:', error);
                            const imageWrapper = document.getElementById('imageArrayWrapper');
                            const errorDiv = document.getElementById('errorArrayMessage');
                            imageWrapper.innerHTML = '<div class="image-placeholder"><p>Error al cargar imágenes</p></div>';
                            errorDiv.innerHTML = '<div class="error">Error: ' + error.message + '</div>';
                        });
                }
                
                // Función para mostrar una imagen específica
                function mostrarImagen(index) {
                    console.log("Mostrando imagen:", index);
                    const imageWrapper = document.getElementById('imageArrayWrapper');
                    const errorDiv = document.getElementById('errorArrayMessage');
                    
                    // Limpiar errores previos
                    errorDiv.innerHTML = '';
                    
                    // Mostrar loading
                    imageWrapper.innerHTML = '<div class="loading">Cargando imagen ' + (index + 1) + '...</div>';
                    
                    fetch(`/api/array/${index}`)
                        .then(response => {
                            if (!response.ok) {
                                throw new Error('Error al cargar imagen ' + (index + 1));
                            }
                            return response.blob();
                        })
                        .then(blob => {
                            const imageUrl = URL.createObjectURL(blob);
                            imageWrapper.innerHTML = `
                                <div style="text-align: center;">
                                    <img src="${imageUrl}" alt="Imagen ${index + 1}" style="max-width: 100%; height: auto;">
                                    <div style="margin-top: 10px;">
                                        <button class="load-image-btn" style="width: auto; margin: 0 5px;" 
                                                onclick="imagenAnterior()" 
                                                ${index === 0 ? 'disabled' : ''}>
                                            ← Anterior
                                        </button>
                                        <span style="margin: 0 10px; font-weight: bold;">
                                            ${index + 1} / ${totalImages}
                                        </span>
                                        <button class="load-image-btn" style="width: auto; margin: 0 5px;" 
                                                onclick="imagenSiguiente()"
                                                ${index === totalImages - 1 ? 'disabled' : ''}>
                                            Siguiente →
                                        </button>
                                    </div>
                                </div>
                            `;
                            currentIndex = index;
                        })
                        .catch(error => {
                            console.error('Error:', error);
                            imageWrapper.innerHTML = '<div class="image-placeholder"><p>Error al cargar la imagen</p></div>';
                            errorDiv.innerHTML = '<div class="error">' + error.message + '</div>';
                        });
                }
                
                // Navegación
                function imagenAnterior() {
                    if (currentIndex > 0) {
                        mostrarImagen(currentIndex - 1);
                    }
                }
                
                function imagenSiguiente() {
                    if (currentIndex < totalImages - 1) {
                        mostrarImagen(currentIndex + 1);
                    }
                }
                
                // Cargar imagen automáticamente al iniciar (opcional)
                // window.onload = () => cargarImagen();
            </script>
        </body>
        </html>
        """;
    }

    @GetMapping("/api/isobaras")
    public ResponseEntity<byte[]> obtenerImagen() throws IOException {

        /*cargar una Mat*/
        Mat imagen;
        imagen = planner.getCsv().prepararImagen(planner.getCsv().cerarMatriz(path));
        /*convertir mat a bytes*/
        // Codificar la imagen a bytes
        BytePointer buffer = new BytePointer();
        IntPointer params = new IntPointer(
                opencv_imgcodecs.IMWRITE_JPEG_QUALITY, 95 // Calidad JPEG
        );

        boolean success = opencv_imgcodecs.imencode(".jpg", imagen, buffer, params);

        if (!success) {
            throw new RuntimeException("Error al codificar la imagen");
        }

        // Convertir BytePointer a byte[]
        byte[] imageBytes = new byte[(int) buffer.limit()];
        buffer.get(imageBytes);

        // Liberar memoria
        buffer.deallocate();
        params.deallocate();
        imagen.deallocate();

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(imageBytes);
    }

@GetMapping("/api/array")
public ResponseEntity<byte[]> obtenerArrayImagenes() throws IOException {
    System.out.println("BOTON!");
    List<ImageIcon> arrayImagenes = planner.getArrayImg();
    
    // Verificar que hay imágenes
    if (arrayImagenes == null || arrayImagenes.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    
    // Tomar la primera imagen
    ImageIcon primeraImagen = arrayImagenes.get(0);
    
    // Convertir a BufferedImage
    BufferedImage bufferedImage = new BufferedImage(
        primeraImagen.getIconWidth(),
        primeraImagen.getIconHeight(),
        BufferedImage.TYPE_INT_RGB
    );
    
    Graphics2D g2d = bufferedImage.createGraphics();
    primeraImagen.paintIcon(null, g2d, 0, 0);
    g2d.dispose();
    
    // Convertir a bytes
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ImageIO.write(bufferedImage, "jpg", baos);
    
    return ResponseEntity.ok()
            .contentType(MediaType.IMAGE_JPEG)
            .body(baos.toByteArray());
}
/*RETORNA UN ARRAY PERO AMB UN CONTADOR PER DESPLAÇAR-TE ENTRE L'ARRAY */
    @GetMapping("/api/array/{index}")
    public ResponseEntity<byte[]> obtenerImagenPorIndice(@PathVariable int index) throws IOException {
        List<ImageIcon> arrayImagenes = planner.getArrayImg();

        if (arrayImagenes == null || index < 0 || index >= arrayImagenes.size()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        ImageIcon imagen = arrayImagenes.get(index);

        // Convertir a BufferedImage
        BufferedImage bufferedImage = new BufferedImage(
                imagen.getIconWidth(),
                imagen.getIconHeight(),
                BufferedImage.TYPE_INT_RGB
        );

        Graphics2D g2d = bufferedImage.createGraphics();
        imagen.paintIcon(null, g2d, 0, 0);
        g2d.dispose();

        // Convertir a bytes
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "jpg", baos);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(baos.toByteArray());
    }

    @GetMapping("/api/array/count")
    public ResponseEntity<Integer> obtenerTotalImagenes() {
        List<ImageIcon> arrayImagenes = planner.getArrayImg();
        return ResponseEntity.ok(arrayImagenes != null ? arrayImagenes.size() : 0);
    }


}
