package com.eyecostech.apexplanner.controller;

import Responses.UserResponse;
import com.eyecostech.apexplanner.Paciente;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.Mat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * AQUI HAN D'ANAR LES PETICIONS DEL FRONT I ENVIARLES AL APEXPLANNERSERVICE
 * (QUE LES ENVIA AL BACK)
 *
 * @author Pau Savall
 */
@RestController
@RequestMapping("")  //Define la ruta base para todos los endpoints del controlador:
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000"})


  

    //// Permite que React (puerto 3000) acceda al backend (puerto 8080)
public class Controller {
        private static final Logger logger = LoggerFactory.getLogger(Controller.class);

    @Autowired
    private ApexplannerService service;

    /**
     * Health check endpoint GET /api/iris/health
     */
    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("CHECK", "check");
        response.put("CHECK", "check");
        response.put("CHECK", null);
        response.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/paciente/{pacienteId}")
    //public Response getPaciente(@PathVariable String pacienteId){
    public ResponseEntity<?> getPaciente(@PathVariable String pacienteId) {
        logger.info("Accediendo al usuario {}", pacienteId);
        try {

            Paciente paciente = service.getPaciente(pacienteId);
            return ResponseEntity.ok()
                    .header("Persona")
                    .body(paciente);

        } catch (Exception e) {
            logger.error("Fallo al acceder al paciente {}", pacienteId);
            return null;
        }
    }

    /**
     * ASIGNA UN PACIENTE AL PLANNER I RETORNA UN OBJECETE PACIENT AL FRONT
     */
    @PostMapping("/api/paciente")
    public ResponseEntity<Paciente> setPaciente(@RequestParam String pacienteId) {
        Paciente paciente;
        service.setPaciente(pacienteId);
        paciente = service.getPaciente(pacienteId);
//        this.path = planner.getPath();
//
        return ResponseEntity.ok(paciente);
    }

    @GetMapping("/paciente/info")
    public ResponseEntity<String> getPacientAge(@PathVariable String pacientId) {
        try {
            logger.info("Accediendo a {}", pacientId);

            String edad = service.getPaciente(pacientId).getEdad();
            logger.info("Consulta exitosa. edad: {}", edad);
            return ResponseEntity.ok(edad);
        } catch (Exception e) {

            logger.error("Fallo en la consulta");

            return null;
        }
    }

    @GetMapping("/laser/cambiar")
    public ResponseEntity<?> setLaserPowerValue(@PathVariable double newValue) {
        logger.info("power: {}", service.getPowerValue());
        service.setPowerValue(newValue);

        if (service.getPowerValue() == newValue) {
            logger.info("new Power: {}", service.getPowerValue());
            Map<String, Object> response = new HashMap<>();
            response.put("Operacion ", "exitosa.");

            return ResponseEntity.ok(response);
        } else {
            logger.error("Fallo al cambiar el valor!");
            logger.info("new Power: {}", service.getPowerValue());
            Map<String, Object> response = new HashMap<>();
            response.put("Valor de power: ", service.getPowerValue());
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/paciente/colorImg/{pacienteId")
    public ResponseEntity<?> getMatColorImage(@PathVariable String pacienteId) {
        try {
            logger.info("Accediendo a la imagen en color de {}", pacienteId);
            Mat image = service.getColorImage();
            return ResponseEntity.ok()
                    .header("Color Image")
                    .body(image);
        } catch (Exception e) {
            logger.error("Error al acceder a la imagen en color de {}", pacienteId);
            return null;
        }
    }

    @GetMapping("/paciente/colorImg/{pacienteId}")
    public ResponseEntity<byte[]> getBytesColorImage(@PathVariable String pacienteId) throws IOException {
        try {
            logger.info("Accediendo a la imagen en color");
            Mat imagen;
            imagen = service.getColorImage();
            BytePointer buffer = new BytePointer();
            IntPointer params = new IntPointer(
                    opencv_imgcodecs.IMWRITE_JPEG_QUALITY, 95
            );
            boolean success = opencv_imgcodecs.imencode(".jpg", imagen, buffer, params);
            if (!success) {
                throw new RuntimeException("Error al codificar la imagen");
            }
            byte[] imageBytes = new byte[(int) buffer.limit()];
            buffer.get(imageBytes);

            buffer.deallocate();
            params.deallocate();
            imagen.deallocate();

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(imageBytes);
        } catch (Exception e) {
            logger.error("error al cargar la imagen");
            return null;
        }

    }

    @PutMapping("/laser/{laserId}")
    public ResponseEntity<?> setLaser(@PathVariable String laserId) {
        logger.info("Cambiando el tipo de laser");
        try {
            service.setLaser(laserId);
            return ResponseEntity.ok("Nuevo Laser: "+service.getLaserName());
        } catch (Exception e) {
            logger.error("Fallo al cambiar el Laser");
            return ResponseEntity.ok("ERROR");
        }

    }

}
//    /**
//     * Get API information GET /api/iris/info
//     */
//    @GetMapping("/info")
//    public ResponseEntity<?> getApiInfo() {
//        Map<String, Object> response = new HashMap<>();
//        response.put("CHECK", "check");
//
//        return ResponseEntity.ok(response);
//    }
//    @GetMapping("laser/laserId")
//    public Laser getLaser(){
//    
//        return null;
//    }
/*loguejar
        try{
        loguejar
        fer
        return
        }cahtc{
        loguejar
        fer
        return}*/
 /*Un response entity:
Map<String, Object> response = new HashMap<>();
        response.put("service", "OK");
        response.put("version", "1.1.0");
        response.put("description", paciente);
        response.put("fecha: ", fecha);

        return ResponseEntity.ok(response);
 */

/*PER A RETORNAR MES D'UN OBJECTE EN UNA RESPONSE.
fas un mapa amb clau:objecte

@GetMapping("/api/ejemplo")
public ResponseEntity<?> obtenerDatos() {
    Paciente paciente = new Paciente("Juan");
    List<Double> valores = Arrays.asList(1.2, 3.4, 5.6);
    
    Map<String, Object> respuesta = new HashMap<>();
    respuesta.put("paciente", paciente);
    respuesta.put("valores", valores);
    
    return ResponseEntity.ok(respuesta);
}
*/
