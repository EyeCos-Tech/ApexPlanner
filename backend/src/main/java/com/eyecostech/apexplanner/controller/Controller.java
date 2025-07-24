package com.eyecostech.apexplanner.controller;

import com.eyecostech.apexplanner.Paciente;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * AQUI HAN D'ANAR LES PETICIONS DEL FRONT I ENVIARLES AL APEXPLANNERSERVICE (QUE LES ENVIA AL BACK)
 *
 * @author Pau Savall
 */
@RestController
@RequestMapping("")  //Define la ruta base para todos los endpoints del controlador:
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000"}) //// Permite que React (puerto 3000) acceda al backend (puerto 8080)
public class Controller {
        private static final Logger logger = LoggerFactory.getLogger(Controller.class);
 
    @Autowired
    private ApexplannerService service;
    
    @GetMapping("/user/{user}")
    //public Response getUser(@PathVariable String userId){
    public Paciente getUser(@PathVariable String userId){
        Paciente paciente= service.getPaciente(userId);
        
        return paciente;
    }
    
    @GetMapping("laser/laserId")
    public Laser getLaser(){
    
        return null;
    }
    
}
