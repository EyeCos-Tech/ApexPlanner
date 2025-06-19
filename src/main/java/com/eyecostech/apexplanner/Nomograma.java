package com.eyecostech.apexplanner;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementación de un nomograma de tratamiento láser para cirugía refractiva.
 * Permite calcular los parámetros óptimos de tratamiento basándose en las
 * características del paciente y factores de corrección empíricos.
 *
 * @author Pau Savall
 */
public class Nomograma {
    
    // Factores de ajuste basados en edad
    private static final Map<String, Double> FACTORES_EDAD = new HashMap<>();
    static {
        FACTORES_EDAD.put("18-25", 1.05);  // Sobrecorrección ligera
        FACTORES_EDAD.put("26-35", 1.00);  // Sin ajuste
        FACTORES_EDAD.put("36-45", 0.95);  // Subcorrección ligera
        FACTORES_EDAD.put("46-55", 0.90);  // Subcorrección moderada
        FACTORES_EDAD.put("56+", 0.85);    // Subcorrección significativa
    }
    
    // Factores de ajuste por tipo de defecto refractivo
    private static final Map<String, Double> FACTORES_REFRACCION = new HashMap<>();
    static {
        FACTORES_REFRACCION.put("MIOPIA_BAJA", 1.00);      // -0.25 a -3.00 D
        FACTORES_REFRACCION.put("MIOPIA_MEDIA", 0.98);     // -3.25 a -6.00 D
        FACTORES_REFRACCION.put("MIOPIA_ALTA", 0.95);      // -6.25 a -12.00 D
        FACTORES_REFRACCION.put("HIPERMETROPIA_BAJA", 1.10);  // +0.25 a +3.00 D
        FACTORES_REFRACCION.put("HIPERMETROPIA_ALTA", 1.15);  // +3.25 a +6.00 D
        FACTORES_REFRACCION.put("ASTIGMATISMO", 1.05);     // Factor adicional
    }
    
    private double refraccionEsferica;
    private double refraccionCilindrica;
    private int ejeAstigmatismo;
    private int edadPaciente;
    private double paquimetria;
    private double queratometria;
    private double diametroPupilar;
    
    /**
     * Constructor del nomograma
     */
    public Nomograma() {
        // Constructor vacío
    }
    
    /**
     * Establece los datos del paciente
     */
    public void setDatosPaciente(double esfera, double cilindro, int eje, 
                                int edad, double paquimetria, double queratometria,
                                double diametroPupilar) {
        this.refraccionEsferica = esfera;
        this.refraccionCilindrica = cilindro;
        this.ejeAstigmatismo = eje;
        this.edadPaciente = edad;
        this.paquimetria = paquimetria;
        this.queratometria = queratometria;
        this.diametroPupilar = diametroPupilar;
    }
    
    /**
     * Calcula la corrección ajustada según el nomograma
     * @return Parámetros de tratamiento ajustados
     */
    public ParametrosTratamiento calcularTratamiento() {
        ParametrosTratamiento params = new ParametrosTratamiento();
        
        // 1. Obtener factores de ajuste
        double factorEdad = obtenerFactorEdad();
        double factorRefraccion = obtenerFactorRefraccion();
        
        // 2. Calcular corrección ajustada
        double esferaAjustada = refraccionEsferica * factorEdad * factorRefraccion;
        double cilindroAjustado = refraccionCilindrica * factorEdad;
        
        // 3. Ajustar por paquimetría (seguridad)
        if (paquimetria < 500) {
            // Reducir tratamiento si córnea delgada
            double factorPaquimetria = 0.9 + (paquimetria - 450) * 0.002;
            esferaAjustada *= factorPaquimetria;
            cilindroAjustado *= factorPaquimetria;
        }
        
        // 4. Calcular zona óptica
        double zonaOptica = calcularZonaOptica();
        
        // 5. Calcular profundidad de ablación
        double profundidadAblacion = calcularProfundidadAblacion(esferaAjustada, 
                                                                 cilindroAjustado, 
                                                                 zonaOptica);
        
        // 6. Verificar límites de seguridad
        verificarLimitesSeguridad(profundidadAblacion);
        
        // 7. Establecer parámetros
        params.setEsferaCorregida(redondear(esferaAjustada, 0.25));
        params.setCilindroCorregido(redondear(cilindroAjustado, 0.25));
        params.setEje(ejeAstigmatismo);
        params.setZonaOptica(zonaOptica);
        params.setZonaTransicion(calcularZonaTransicion(zonaOptica));
        params.setProfundidadAblacion(profundidadAblacion);
        params.setEnergiaNominal(calcularEnergia(profundidadAblacion));
        
        return params;
    }
    
    /**
     * Obtiene el factor de ajuste según la edad
     */
    private double obtenerFactorEdad() {
        if (edadPaciente >= 18 && edadPaciente <= 25) return FACTORES_EDAD.get("18-25");
        if (edadPaciente >= 26 && edadPaciente <= 35) return FACTORES_EDAD.get("26-35");
        if (edadPaciente >= 36 && edadPaciente <= 45) return FACTORES_EDAD.get("36-45");
        if (edadPaciente >= 46 && edadPaciente <= 55) return FACTORES_EDAD.get("46-55");
        return FACTORES_EDAD.get("56+");
    }
    
    /**
     * Obtiene el factor de ajuste según el tipo de refracción
     */
    private double obtenerFactorRefraccion() {
        double factor = 1.0;
        
        if (refraccionEsferica < 0) { // Miopía
            if (Math.abs(refraccionEsferica) <= 3.0) {
                factor = FACTORES_REFRACCION.get("MIOPIA_BAJA");
            } else if (Math.abs(refraccionEsferica) <= 6.0) {
                factor = FACTORES_REFRACCION.get("MIOPIA_MEDIA");
            } else {
                factor = FACTORES_REFRACCION.get("MIOPIA_ALTA");
            }
        } else if (refraccionEsferica > 0) { // Hipermetropía
            if (refraccionEsferica <= 3.0) {
                factor = FACTORES_REFRACCION.get("HIPERMETROPIA_BAJA");
            } else {
                factor = FACTORES_REFRACCION.get("HIPERMETROPIA_ALTA");
            }
        }
        
        // Ajuste adicional por astigmatismo
        if (Math.abs(refraccionCilindrica) > 1.0) {
            factor *= FACTORES_REFRACCION.get("ASTIGMATISMO");
        }
        
        return factor;
    }
    
    /**
     * Calcula la zona óptica óptima
     */
    private double calcularZonaOptica() {
        // Base: 6.5mm
        double zonaBase = 6.5;
        
        // Ajustar por diámetro pupilar
        if (diametroPupilar > 6.0) {
            zonaBase = Math.min(7.0, diametroPupilar + 0.5);
        }
        
        // Ajustar por magnitud del defecto
        double magnitud = Math.abs(refraccionEsferica) + Math.abs(refraccionCilindrica / 2);
        if (magnitud > 6.0) {
            // Reducir zona óptica en defectos altos para ahorrar tejido
            zonaBase -= 0.5;
        }
        
        return Math.max(5.5, Math.min(7.0, zonaBase));
    }
    
    /**
     * Calcula la zona de transición
     */
    private double calcularZonaTransicion(double zonaOptica) {
        // Típicamente 1-2mm adicionales
        return zonaOptica + 1.5;
    }
    
    /**
     * Calcula la profundidad de ablación usando la fórmula de Munnerlyn modificada
     */
    private double calcularProfundidadAblacion(double esfera, double cilindro, double zona) {
        // Fórmula de Munnerlyn: t = (D × S²) / 3
        // Donde: t = profundidad (µm), D = dioptrías, S = zona óptica (mm)
        
        double ablacionEsferica = Math.abs(esfera) * zona * zona / 3.0;
        double ablacionCilindrica = Math.abs(cilindro) * zona * zona / 3.0;
        
        // La ablación cilíndrica es máxima en el meridiano del eje
        return ablacionEsferica + (ablacionCilindrica / 2.0);
    }
    
    /**
     * Calcula la energía nominal del láser
     */
    private double calcularEnergia(double profundidadAblacion) {
        // Base: 193nm (excimer láser estándar)
        // Ajustar energía según profundidad
        double energiaBase = 193.0;
        
        if (profundidadAblacion > 100) {
            // Aumentar ligeramente la energía para ablaciones profundas
            energiaBase *= 1.02;
        }
        
        return energiaBase;
    }
    
    /**
     * Verifica los límites de seguridad
     */
    private void verificarLimitesSeguridad(double profundidadAblacion) {
        // Lecho corneal residual mínimo: 300µm
        double lechoResidual = paquimetria - profundidadAblacion;
        
        if (lechoResidual < 300) {
            throw new IllegalStateException(
                "ADVERTENCIA: Lecho corneal residual insuficiente (" + 
                lechoResidual + "µm). Mínimo requerido: 300µm"
            );
        }
        
        // Profundidad máxima de ablación: 150µm (recomendado)
        if (profundidadAblacion > 150) {
            System.out.println(
                "PRECAUCIÓN: Profundidad de ablación alta (" + 
                profundidadAblacion + "µm). Considerar técnica alternativa."
            );
        }
    }
    
    /**
     * Redondea al cuarto de dioptría más cercano
     */
    private double redondear(double valor, double incremento) {
        return Math.round(valor / incremento) * incremento;
    }
    
    /**
     * Clase interna para almacenar los parámetros de tratamiento
     */
    public static class ParametrosTratamiento {
        private double esferaCorregida;
        private double cilindroCorregido;
        private int eje;
        private double zonaOptica;
        private double zonaTransicion;
        private double profundidadAblacion;
        private double energiaNominal;
        
        // Getters y setters
        public double getEsferaCorregida() { return esferaCorregida; }
        public void setEsferaCorregida(double esferaCorregida) { 
            this.esferaCorregida = esferaCorregida; 
        }
        
        public double getCilindroCorregido() { return cilindroCorregido; }
        public void setCilindroCorregido(double cilindroCorregido) { 
            this.cilindroCorregido = cilindroCorregido; 
        }
        
        public int getEje() { return eje; }
        public void setEje(int eje) { this.eje = eje; }
        
        public double getZonaOptica() { return zonaOptica; }
        public void setZonaOptica(double zonaOptica) { 
            this.zonaOptica = zonaOptica; 
        }
        
        public double getZonaTransicion() { return zonaTransicion; }
        public void setZonaTransicion(double zonaTransicion) { 
            this.zonaTransicion = zonaTransicion; 
        }
        
        public double getProfundidadAblacion() { return profundidadAblacion; }
        public void setProfundidadAblacion(double profundidadAblacion) { 
            this.profundidadAblacion = profundidadAblacion; 
        }
        
        public double getEnergiaNominal() { return energiaNominal; }
        public void setEnergiaNominal(double energiaNominal) { 
            this.energiaNominal = energiaNominal; 
        }
        
        @Override
        public String toString() {
            return String.format(
                "Parámetros de Tratamiento:\n" +
                "- Esfera: %.2fD (ajustada: %.2fD)\n" +
                "- Cilindro: %.2fD (ajustado: %.2fD) @ %d°\n" +
                "- Zona óptica: %.1fmm\n" +
                "- Zona transición: %.1fmm\n" +
                "- Profundidad ablación: %.0fµm\n" +
                "- Energía: %.0fnm",
                esferaCorregida, esferaCorregida,
                cilindroCorregido, cilindroCorregido, eje,
                zonaOptica, zonaTransicion,
                profundidadAblacion, energiaNominal
            );
        }
    }
    
    /**
     * Método de ejemplo de uso
     */
//    public static void main(String[] args) {
//        Nomograma nomograma = new Nomograma();
//        
//        // Ejemplo: Paciente con miopía y astigmatismo
//        nomograma.setDatosPaciente(
//            -4.50,  // Esfera
//            -1.25,  // Cilindro
//            90,     // Eje
//            28,     // Edad
//            540,    // Paquimetría
//            43.5,   // Queratometría
//            5.8     // Diámetro pupilar
//        );
//        
//        try {
//            ParametrosTratamiento params = nomograma.calcularTratamiento();
//            System.out.println(params);
//        } catch (IllegalStateException e) {
//            System.err.println("Error de seguridad: " + e.getMessage());
//        }
//    }
}

/*

Explicación del Nomograma
Función Principal
El nomograma actúa como un sistema de ajuste personalizado que modifica los parámetros del tratamiento láser basándose en:

Factores del paciente: edad, tipo de defecto refractivo, grosor corneal
Factores técnicos: zona óptica, energía del láser
Factores de seguridad: lecho corneal residual, profundidad máxima

Ajustes Típicos

Por edad: Los pacientes jóvenes tienden a recibir ligera sobrecorrección, mientras que los mayores reciben subcorrección para preservar algo de acomodación
Por defecto refractivo: La hipermetropía suele requerir más corrección que la calculada, mientras que la miopía alta puede requerir menos
Por paquimetría: Córneas delgadas requieren tratamientos más conservadores

Integración con tu Proyecto
Para integrar este nomograma con tu aplicación actual:

En el backend: Añadir un endpoint REST que reciba los datos del paciente y devuelva los parámetros calculados
Con CsvAnalizer: Usar los parámetros del nomograma para ajustar la matriz de ablación
Con Calculador: Modificar los cálculos de disparos según los factores del nomograma

*/