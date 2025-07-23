# Laser Eye Analyzer

Sistema de análisis de efectos láser ocular desarrollado en React con diseño basado en el proyecto Analyzer existente.

## Características

- **Interfaz intuitiva**: Formulario de entrada con validación para configurar parámetros del láser
- **Selección de carpeta**: Input para acceder a carpetas de Windows con datos del láser
- **Tipos de láser**: Dropdown con múltiples tipos de láser (Diodo Rojo, Verde, Azul, Infrarrojo, Nd:YAG, CO2, etc.)
- **Análisis visual**: Dos ventanas equidistantes que muestran:
  1. Representación del ojo con colores de isobaras
  2. Visualizador de capas con slider para navegación

## Tecnologías Utilizadas

- React 18
- Tailwind CSS (con configuración personalizada)
- Heroicons para iconografía
- Context API para gestión de estado
- Framer Motion (preparado para animaciones avanzadas)

## Paleta de Colores
2yumb2yk7xb5xk3f1p8
Basada en el proyecto Analyzer original:
- **Primario**: #5a9891 (verde azulado)
- **Secundario**: #e2e2e2
- **Accent**: #f8f9fa
- **Fuente**: Raleway

## Instalación

```bash
# Instalar dependencias
npm install

# Iniciar en modo desarrollo
npm start

# Construir para producción
npm run build
```

## Estructura del Proyecto

```
src/
├── components/
│   ├── InputForm.js          # Formulario de entrada de datos
│   └── ResultsWindows.js     # Ventanas de visualización de resultados
├── context/
│   └── LaserAnalysisContext.js # Context para gestión de estado
├── hooks/                    # Hooks personalizados (preparado)
├── utils/                    # Utilidades (preparado)
├── App.js                    # Componente principal
├── App.css                   # Estilos específicos de la app
├── index.js                  # Punto de entrada
└── index.css                 # Estilos globales con Tailwind
```

## Funcionalidades

### Formulario de Entrada
- Selector de carpeta de Windows
- Dropdown con 8 tipos de láser diferentes
- Input numérico para potencia/longitud de onda
- Validación en tiempo real
- Botón calcular que se activa cuando todos los campos están completos

### Ventanas de Visualización
1. **Ventana de Isobaras**: Muestra representación del ojo con colores que indican intensidad
2. **Ventana de Capas**: Visualizador con slider para navegar entre diferentes capas de análisis

### Estado de la Aplicación
- Indicador visual del estado (Listo/Analizando/Completo)
- Barra de progreso durante el análisis
- Manejo de errores y validación

## Próximas Mejoras

- Integración con backend para análisis real
- Implementación de Three.js para visualización 3D
- Exportación de resultados
- Historial de análisis
- Configuraciones avanzadas de láser

## Buenas Prácticas Implementadas

- Nombres consistentes y descriptivos
- Componentes reutilizables
- Gestión de estado centralizada
- Validación robusta de formularios
- Diseño responsivo
- Código limpio y bien documentado
