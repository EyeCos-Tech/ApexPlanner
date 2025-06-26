package nomograma;

import com.eyecostech.apexplanner.Calculador;
import com.eyecostech.apexplanner.Laser;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementación completa de un nomograma de tratamiento láser para cirugía refractiva.
 * Contiene toda la lógica de cálculo y aplicación de parámetros.
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
    private ParametrosTratamiento parametrosCalculados;
    
    /**
     * Constructor vacío
     */
    public Nomograma() {
        // Constructor vacío
    }
    
    /**
     * Establece los datos del paciente desde un Map
     * @param datosPaciente Map con los datos del paciente
     */
    public void setDatosPacienteDesdeMap(Map<String, Object> datosPaciente) throws Exception {
        try {
            this.refraccionEsferica = ((Number) datosPaciente.get("esfera")).doubleValue();
            this.refraccionCilindrica = ((Number) datosPaciente.get("cilindro")).doubleValue();
            this.ejeAstigmatismo = ((Number) datosPaciente.get("eje")).intValue();
            this.edadPaciente = ((Number) datosPaciente.get("edad")).intValue();
            this.paquimetria = ((Number) datosPaciente.get("paquimetria")).doubleValue();
            this.queratometria = ((Number) datosPaciente.get("queratometria")).doubleValue();
            this.diametroPupilar = ((Number) datosPaciente.get("diametroPupilar")).doubleValue();
        } catch (NullPointerException e) {
            throw new Exception("Faltan datos requeridos del paciente");
        } catch (ClassCastException e) {
            throw new Exception("Formato incorrecto en los datos del paciente");
        }
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
     * Calcula y retorna los parámetros de tratamiento como Map
     * @return Map con todos los parámetros calculados
     */
    public Map<String, Object> calcularTratamientoComoMap() throws IllegalStateException {
        // Calcular parámetros
        ParametrosTratamiento params = calcularTratamiento();
        
        // Convertir a Map para fácil serialización
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("status", "success");
        resultado.put("parametrosTratamiento", parametrosAMap(params));
        resultado.put("datosOriginales", datosOriginalesAMap());
        resultado.put("factoresAplicados", factoresAplicadosAMap());
        resultado.put("mensaje", "Cálculo completado exitosamente");
        
        return resultado;
    }
    
    /**
     * Aplica el nomograma a una matriz de ablación
     * @param matrizOriginal Matriz de ablación original
     * @return Map con la matriz ajustada y estadísticas
     */
    public Map<String, Object> aplicarNomogramaAMatriz(List<List<Double>> matrizOriginal, Laser laser) {
        if (parametrosCalculados == null) {
            calcularTratamiento();
        }
        
        // Calcular factor de corrección
        double factorCorreccion = calcularFactorCorreccion();
        
        // Ajustar matriz
        List<List<Double>> matrizAjustada = ajustarMatriz(matrizOriginal, factorCorreccion);
        
        // Calcular estadísticas
        Calculador calc = new Calculador();
        double totalDisparos = calc.calcularNumeroShootsTotal(matrizAjustada, laser);
        double maxDisparos = calc.maxShoots(matrizAjustada, laser);
        
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("matrizAjustada", matrizAjustada);
        resultado.put("factorCorreccion", factorCorreccion);
        resultado.put("disparosTotales", totalDisparos);
        resultado.put("disparosMaximos", maxDisparos);
        resultado.put("profundidadMaxima", parametrosCalculados.getProfundidadAblacion());
        
        return resultado;
    }
    
    /**
     * Obtiene información sobre los factores del nomograma
     * @return Map con información de todos los factores
     */
    public static Map<String, Object> obtenerInformacionFactores() {
        Map<String, Object> info = new HashMap<>();
        
        info.put("factoresEdad", FACTORES_EDAD);
        info.put("factoresRefraccion", FACTORES_REFRACCION);
        info.put("limitesSeguridad", Map.of(
            "lechoResidualMinimo", "300 µm",
            "profundidadMaximaRecomendada", "150 µm",
            "zonaOpticaMinima", "5.5 mm",
            "zonaOpticaMaxima", "7.0 mm",
            "edadMinima", "18 años",
            "miopiaMaxima", "-12.00 D",
            "hipermetropiaMaxima", "+6.00 D"
        ));
        info.put("descripcion", "Nomograma para ajuste de tratamientos láser refractivos");
        
        return info;
    }
    
    /**
     * Valida si los datos del paciente son aptos para tratamiento
     * @return Map con el resultado de la validación
     */
    public Map<String, Object> validarPaciente() {
        Map<String, Object> validacion = new HashMap<>();
        List<String> advertencias = new ArrayList<>();
        List<String> errores = new ArrayList<>();
        boolean apto = true;
        
        // Validar edad
        if (edadPaciente < 18) {
            errores.add("Paciente menor de 18 años");
            apto = false;
        } else if (edadPaciente < 21) {
            advertencias.add("Paciente entre 18-21 años: verificar estabilidad refractiva");
        }
        
        // Validar refracción
        if (refraccionEsferica < -12.0) {
            errores.add("Miopía excede -12.00 D");
            apto = false;
        } else if (refraccionEsferica < -10.0) {
            advertencias.add("Miopía alta: considerar técnicas alternativas");
        }
        
        if (refraccionEsferica > 6.0) {
            errores.add("Hipermetropía excede +6.00 D");
            apto = false;
        } else if (refraccionEsferica > 4.0) {
            advertencias.add("Hipermetropía alta: resultados menos predecibles");
        }
        
        // Validar paquimetría
        if (paquimetria < 480) {
            errores.add("Paquimetría insuficiente (<480 µm)");
            apto = false;
        } else if (paquimetria < 500) {
            advertencias.add("Paquimetría límite: evaluar cuidadosamente");
        }
        
        validacion.put("apto", apto);
        validacion.put("advertencias", advertencias);
        validacion.put("errores", errores);
        
        return validacion;
    }
    
    // ========== MÉTODOS PRIVADOS DE CÁLCULO ==========
    
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
        
        this.parametrosCalculados = params;
        return params;
    }
    
    private double obtenerFactorEdad() {
        if (edadPaciente >= 18 && edadPaciente <= 25) return FACTORES_EDAD.get("18-25");
        if (edadPaciente >= 26 && edadPaciente <= 35) return FACTORES_EDAD.get("26-35");
        if (edadPaciente >= 36 && edadPaciente <= 45) return FACTORES_EDAD.get("36-45");
        if (edadPaciente >= 46 && edadPaciente <= 55) return FACTORES_EDAD.get("46-55");
        return FACTORES_EDAD.get("56+");
    }
    
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
        
        if (Math.abs(refraccionCilindrica) > 1.0) {
            factor *= FACTORES_REFRACCION.get("ASTIGMATISMO");
        }
        
        return factor;
    }
    
    private double calcularZonaOptica() {
        double zonaBase = 6.5;
        
        if (diametroPupilar > 6.0) {
            zonaBase = Math.min(7.0, diametroPupilar + 0.5);
        }
        
        double magnitud = Math.abs(refraccionEsferica) + Math.abs(refraccionCilindrica / 2);
        if (magnitud > 6.0) {
            zonaBase -= 0.5;
        }
        
        return Math.max(5.5, Math.min(7.0, zonaBase));
    }
    
    private double calcularZonaTransicion(double zonaOptica) {
        return zonaOptica + 1.5;
    }
    
    private double calcularProfundidadAblacion(double esfera, double cilindro, double zona) {
        double ablacionEsferica = Math.abs(esfera) * zona * zona / 3.0;
        double ablacionCilindrica = Math.abs(cilindro) * zona * zona / 3.0;
        return ablacionEsferica + (ablacionCilindrica / 2.0);
    }
    
    private double calcularEnergia(double profundidadAblacion) {
        double energiaBase = 193.0;
        if (profundidadAblacion > 100) {
            energiaBase *= 1.02;
        }
        return energiaBase;
    }
    
    private void verificarLimitesSeguridad(double profundidadAblacion) {
        double lechoResidual = paquimetria - profundidadAblacion;
        
        if (lechoResidual < 300) {
            throw new IllegalStateException(
                "ADVERTENCIA: Lecho corneal residual insuficiente (" + 
                lechoResidual + "µm). Mínimo requerido: 300µm"
            );
        }
        
        if (profundidadAblacion > 150) {
            System.out.println(
                "PRECAUCIÓN: Profundidad de ablación alta (" + 
                profundidadAblacion + "µm). Considerar técnica alternativa."
            );
        }
    }
    
    private double redondear(double valor, double incremento) {
        return Math.round(valor / incremento) * incremento;
    }
    
    private double calcularFactorCorreccion() {
        if (parametrosCalculados == null || refraccionEsferica == 0) {
            return 1.0;
        }
        return Math.abs(parametrosCalculados.getEsferaCorregida() / refraccionEsferica);
    }
    
    private List<List<Double>> ajustarMatriz(List<List<Double>> matrizOriginal, double factor) {
        List<List<Double>> matrizAjustada = new ArrayList<>();
        
        for (List<Double> fila : matrizOriginal) {
            List<Double> filaAjustada = new ArrayList<>();
            for (Double valor : fila) {
                filaAjustada.add(valor * factor);
            }
            matrizAjustada.add(filaAjustada);
        }
        
        return matrizAjustada;
    }
    
    // ========== MÉTODOS AUXILIARES PARA CONVERSIÓN ==========
    
    private Map<String, Object> parametrosAMap(ParametrosTratamiento params) {
        Map<String, Object> map = new HashMap<>();
        map.put("esferaCorregida", params.getEsferaCorregida());
        map.put("cilindroCorregido", params.getCilindroCorregido());
        map.put("eje", params.getEje());
        map.put("zonaOptica", params.getZonaOptica());
        map.put("zonaTransicion", params.getZonaTransicion());
        map.put("profundidadAblacion", params.getProfundidadAblacion());
        map.put("energiaNominal", params.getEnergiaNominal());
        return map;
    }
    
    private Map<String, Object> datosOriginalesAMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("esfera", refraccionEsferica);
        map.put("cilindro", refraccionCilindrica);
        map.put("eje", ejeAstigmatismo);
        map.put("edad", edadPaciente);
        map.put("paquimetria", paquimetria);
        map.put("queratometria", queratometria);
        map.put("diametroPupilar", diametroPupilar);
        return map;
    }
    
    private Map<String, Object> factoresAplicadosAMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("factorEdad", obtenerFactorEdad());
        map.put("factorRefraccion", obtenerFactorRefraccion());
        return map;
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
    }
    
    
   
    
    
    
    
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