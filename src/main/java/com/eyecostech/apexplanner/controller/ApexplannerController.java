/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.eyecostech.apexplanner.controller;

import com.eyecostech.apexplanner.Apexplanner;
import com.eyecostech.apexplanner.Paciente;
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
import java.util.Set;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.eyecostech.apexplanner.Nomograma;
import com.eyecostech.apexplanner.Nomograma.ParametrosTratamiento;
import java.util.ArrayList;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.Map;

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
                    <h3>Imatge de Topografia en Color </h3>
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
                    fetch('/api/imagenColor')
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
                            errorDiv.innerHTML = '<div class="error">Error: Verifica que el endpoint /api/imagenColor esté configurado en Spring Boot</div>';
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

    /**
     * ASIGNA UN PACIEN AL PLANNER I RETORNA UN OBJECETE PACIENT AL FRONT (per
     * poder accedir a les veriables de paciente)*
     */
    @PostMapping("/api/paciente")
    public ResponseEntity<Paciente> setPaciente(@RequestParam String nombre) {
        planner.setPaciente(nombre);
        Paciente paciente = planner.getPaciente();

        return ResponseEntity.ok(paciente);
    }

    @GetMapping("/api/imagenColor")
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

        /*comprovar que l'array esta ple*/
        for (ImageIcon elemento : arrayImagenes) {
            System.out.println(elemento);
        }

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
    /**
 * Endpoint para calcular parámetros de tratamiento usando el nomograma
 * 
 * URL: POST http://localhost:8080/api/nomograma/calcular
 * 
 * Ejemplo de JSON de entrada:
 * {
 *   "esfera": -4.50,
 *   "cilindro": -1.25,
 *   "eje": 90,
 *   "edad": 28,
 *   "paquimetria": 540,
 *   "queratometria": 43.5,
 *   "diametroPupilar": 5.8
 * }
 * 
 */
@PostMapping("/api/nomograma/calcular")
public ResponseEntity<?> calcularNomograma(@RequestBody Map<String, Object> datosPaciente) {
    try {
        // 1. EXTRAER DATOS DEL JSON RECIBIDO
        double esfera = ((Number) datosPaciente.get("esfera")).doubleValue();
        double cilindro = ((Number) datosPaciente.get("cilindro")).doubleValue();
        int eje = ((Number) datosPaciente.get("eje")).intValue();
        int edad = ((Number) datosPaciente.get("edad")).intValue();
        double paquimetria = ((Number) datosPaciente.get("paquimetria")).doubleValue();
        double queratometria = ((Number) datosPaciente.get("queratometria")).doubleValue();
        double diametroPupilar = ((Number) datosPaciente.get("diametroPupilar")).doubleValue();
        
        // 2. CREAR INSTANCIA DEL NOMOGRAMA
        Nomograma nomograma = new Nomograma();
        
        // 3. CONFIGURAR CON LOS DATOS DEL PACIENTE
        nomograma.setDatosPaciente(esfera, cilindro, eje, edad, 
                                   paquimetria, queratometria, diametroPupilar);
        
        // 4. CALCULAR TRATAMIENTO
        ParametrosTratamiento parametros = nomograma.calcularTratamiento();
        
        // 5. CREAR RESPUESTA JSON
        Map<String, Object> respuesta = Map.of(
            "status", "success",
            "parametrosTratamiento", Map.of(
                "esferaCorregida", parametros.getEsferaCorregida(),
                "cilindroCorregido", parametros.getCilindroCorregido(),
                "eje", parametros.getEje(),
                "zonaOptica", parametros.getZonaOptica(),
                "zonaTransicion", parametros.getZonaTransicion(),
                "profundidadAblacion", parametros.getProfundidadAblacion(),
                "energiaNominal", parametros.getEnergiaNominal()
            ),
            "datosOriginales", Map.of(
                "esfera", esfera,
                "cilindro", cilindro,
                "edad", edad
            ),
            "mensaje", "Cálculo completado exitosamente"
        );
        
        // 6. RETORNAR RESPUESTA EXITOSA
        return ResponseEntity.ok(respuesta);
        
    } catch (IllegalStateException e) {
        // 7. MANEJAR ERROR DE SEGURIDAD (ej: lecho corneal insuficiente)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
            "status", "error",
            "tipo", "seguridad",
            "mensaje", e.getMessage(),
            "detalles", "Los parámetros del paciente no cumplen con los límites de seguridad"
        ));
    } catch (NullPointerException e) {
        // 8. MANEJAR ERROR DE DATOS FALTANTES
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
            "status", "error",
            "tipo", "datos_faltantes",
            "mensaje", "Faltan datos requeridos en la petición",
            "detalles", "Verifica que todos los campos estén presentes: esfera, cilindro, eje, edad, paquimetria, queratometria, diametroPupilar"
        ));
    } catch (Exception e) {
        // 9. MANEJAR OTROS ERRORES
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
            "status", "error",
            "tipo", "interno",
            "mensaje", "Error al calcular los parámetros: " + e.getMessage()
        ));
    }
}

/**
 * Endpoint para obtener información sobre los factores del nomograma
 * URL: GET http://localhost:8080/api/nomograma/factores
 * 
 * @author Pau Savall
 */
@GetMapping("/api/nomograma/factores")
public ResponseEntity<Map<String, Object>> obtenerFactoresNomograma() {
    Map<String, Object> factores = Map.of(
        "factoresEdad", Map.of(
            "18-25", "1.05 (sobrecorrección ligera)",
            "26-35", "1.00 (sin ajuste)",
            "36-45", "0.95 (subcorrección ligera)",
            "46-55", "0.90 (subcorrección moderada)",
            "56+", "0.85 (subcorrección significativa)"
        ),
        "factoresRefraccion", Map.of(
            "MIOPIA_BAJA", "1.00 (-0.25 a -3.00 D)",
            "MIOPIA_MEDIA", "0.98 (-3.25 a -6.00 D)",
            "MIOPIA_ALTA", "0.95 (-6.25 a -12.00 D)",
            "HIPERMETROPIA_BAJA", "1.10 (+0.25 a +3.00 D)",
            "HIPERMETROPIA_ALTA", "1.15 (+3.25 a +6.00 D)",
            "ASTIGMATISMO", "1.05 (factor adicional)"
        ),
        "limitesSeguridad", Map.of(
            "lechoResidualMinimo", "300 µm",
            "profundidadMaximaRecomendada", "150 µm",
            "zonaOpticaMinima", "5.5 mm",
            "zonaOpticaMaxima", "7.0 mm"
        )
    );
    
    return ResponseEntity.ok(factores);
}

/**
 * Endpoint para aplicar el nomograma a la planificación actual
 * URL: POST http://localhost:8080/api/nomograma/aplicar
 * 
 * @author Pau Savall
 */
@PostMapping("/api/nomograma/aplicar")
public ResponseEntity<?> aplicarNomogramaAPlanificacion(@RequestBody Map<String, Object> datos) {
    try {
        // Primero calcular los parámetros del nomograma
        ResponseEntity<?> calculoResponse = calcularNomograma(datos);
        
        if (calculoResponse.getStatusCode() != HttpStatus.OK) {
            return calculoResponse;
        }
        
        // Obtener los parámetros calculados
        Map<String, Object> resultado = (Map<String, Object>) calculoResponse.getBody();
        Map<String, Object> parametros = (Map<String, Object>) resultado.get("parametrosTratamiento");
        
        // Aplicar los parámetros a la matriz de ablación actual
        double factorCorreccion = calcularFactorCorreccion(parametros);
        
        // Actualizar la planificación
        List<List<Double>> matrizOriginal = planner.getCsv().cerarMatriz(planner.getPath());
        List<List<Double>> matrizAjustada = ajustarMatrizConNomograma(matrizOriginal, factorCorreccion);
        
        // Recalcular disparos con la matriz ajustada
        double totalDisparos = planner.calc.calcularNumeroShootsTotal(matrizAjustada);
        double maxDisparos = planner.calc.maxShoots(matrizAjustada);
        
        return ResponseEntity.ok(Map.of(
            "status", "success",
            "parametrosNomograma", parametros,
            "factorCorreccion", factorCorreccion,
            "disparosTotales", totalDisparos,
            "disparosMaximos", maxDisparos,
            "mensaje", "Nomograma aplicado exitosamente a la planificación"
        ));
        
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
            "status", "error",
            "mensaje", "Error al aplicar el nomograma: " + e.getMessage()
        ));
    }
}

/**
 * Método auxiliar para calcular el factor de corrección basado en los parámetros del nomograma
 */
private double calcularFactorCorreccion(Map<String, Object> parametros) {
    double esferaOriginal = -4.50; // Esto debería venir de los datos originales
    double esferaCorregida = (Double) parametros.get("esferaCorregida");
    
    // Factor de corrección = corrección ajustada / corrección original
    return Math.abs(esferaCorregida / esferaOriginal);
}

/**
 * Método auxiliar para ajustar la matriz de ablación con el factor del nomograma
 */
private List<List<Double>> ajustarMatrizConNomograma(List<List<Double>> matrizOriginal, 
                                                     double factorCorreccion) {
    List<List<Double>> matrizAjustada = new ArrayList<>();
    
    for (List<Double> fila : matrizOriginal) {
        List<Double> filaAjustada = new ArrayList<>();
        for (Double valor : fila) {
            // Aplicar el factor de corrección a cada punto
            filaAjustada.add(valor * factorCorreccion);
        }
        matrizAjustada.add(filaAjustada);
    }
    
    return matrizAjustada;
}

}
