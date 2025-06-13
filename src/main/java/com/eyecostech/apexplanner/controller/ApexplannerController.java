/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.eyecostech.apexplanner.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Arrays;

/**
 * CONTROLADOR PRINCIPAL DE LA APLICACIÓN
 *
 *  Este controlador maneja todas las peticiones HTTP 
 * - GET: 
 * - POST: 
 * - PUT: 
 * - DELETE:
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
                    body { font-family: Arial, sans-serif; margin: 40px;
                    background-color: #f5f5f5; }
                    .container { max-width: 800px; background: white;
                    padding: 30px; border-radius: 10px;
                    box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
                    h1 { color: #2c3e50; }
                    .info { background: #e8f4f8; padding: 15px;
                    border-radius: 5px; margin: 20px 0; }
                    a { color: #3498db; text-decoration: none; }
                </style>
            </head>
            <body>
                <div class="container">
                    <h1> ¡Bienvenido a tu Primera App Spring Boot!</h1>
                    <div class="info">
                    <h3> Estado de la Aplicación</h3>
                    <p><strong>Estado:</strong> Funcionando correctamente</p>
                    <p><strong>Servidor:</strong> Apache Tomcat (embebido)</p>
                    <p><strong>Puerto:</strong> 8080</p><p><strong>Framework:</strong> Spring Boot 3.3.0</p>
                    <p><strong>Cargado el:</strong></p>
                </div>
                </ul>
                </div>
            </body>
        </html>
        """;
    }
    
    
    
}
