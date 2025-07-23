package com.eyecostech.apexplanner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clase principal de la aplicación APEXPLANNER
 * Punto de entrada para iniciar el servidor Spring Boot
 * 
 * @author Pau Savall
 */
@SpringBootApplication
public class ApexplannerApp {
    
    private static final Logger logger = LoggerFactory.getLogger(ApexplannerApp.class);
    
    /**
     * Método principal - Punto de entrada de la aplicación
     * 
     * @param args Argumentos de línea de comandos
     */
    public static void main(String[] args) {
        // Configurar el contexto de la aplicación
        System.out.println("\n===================================================");
        System.out.println("     INICIANDO APEXPLANNER - SPRING BOOT");
        System.out.println("     Sistema de Planificacion para Cirugia Laser");
        System.out.println("===================================================\n");
        
        try {
            // Iniciar la aplicación Spring Boot
            ConfigurableApplicationContext context = SpringApplication.run(ApexplannerApp.class, args);
            
            // Log de inicio exitoso
            logger.info("APEXPLANNER iniciado correctamente");
            logger.info("Version de Spring Boot: {}", SpringApplication.class.getPackage().getImplementationVersion());
            
        } catch (Exception e) {
            logger.error("Error al iniciar APEXPLANNER: ", e);
            System.err.println("\n[ERROR] No se pudo iniciar la aplicacion: " + e.getMessage());
            System.exit(1);
        }
    }
    
    /**
     * CommandLineRunner para ejecutar acciones después del inicio
     * 
     * @return CommandLineRunner con las acciones post-inicio
     */
    @Bean
    public CommandLineRunner inicializacionCompleta() {
        return args -> {
            System.out.println("\n=====================================");
            System.out.println(" APEXPLANNER INICIADO CORRECTAMENTE");
            System.out.println("=====================================");
            System.out.println(" URL Principal: http://localhost:8080");
            System.out.println(" Documentacion: http://localhost:8080/info");
            System.out.println(" API REST disponible en: http://localhost:8080/api");
            System.out.println(" Para detener: Ctrl+C en consola");
            System.out.println("=====================================\n");
            
            // Log adicional
            logger.info("Servidor listo para recibir peticiones del frontend React");
            logger.info("CORS configurado para permitir peticiones desde React");
            
            // Verificar directorio de trabajo
            String workingDir = System.getProperty("user.dir");
            logger.info("Directorio de trabajo: {}", workingDir);
            
            // Verificar configuración de pacientes
            String pacientesDir = workingDir + "\\pacientes";
            logger.info("Directorio de pacientes: {}", pacientesDir);
        };
    }
    
    /**
     * Bean para manejo de cierre elegante de la aplicación
     * 
     * @return Runnable para ejecutar al cerrar
     */
    @Bean
    public CommandLineRunner shutdownHook() {
        return args -> {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("\n=====================================");
                System.out.println(" Cerrando APEXPLANNER...");
                System.out.println("=====================================");
                logger.info("Apagando servidor Spring Boot");
                logger.info("Liberando recursos...");
                System.out.println(" Servidor detenido correctamente");
                System.out.println("=====================================\n");
            }));
        };
    }
}