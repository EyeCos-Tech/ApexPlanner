import React from 'react';
import { useLaserAnalysis } from '../context/LaserAnalysisContext';

// Componentes de iconos SVG
const EyeIcon = ({ className }) => (
  <svg className={className} fill="currentColor" viewBox="0 0 20 20">
    <path d="M10 12a2 2 0 100-4 2 2 0 000 4z"></path>
    <path fillRule="evenodd" d="M.458 10C1.732 5.943 5.522 3 10 3s8.268 2.943 9.542 7c-1.274 4.057-5.064 7-9.542 7S1.732 14.057.458 10zM14 10a4 4 0 11-8 0 4 4 0 018 0z" clipRule="evenodd"></path>
  </svg>
);

const PhotoIcon = ({ className }) => (
  <svg className={className} fill="currentColor" viewBox="0 0 20 20">
    <path fillRule="evenodd" d="M4 3a2 2 0 00-2 2v10a2 2 0 002 2h12a2 2 0 002-2V5a2 2 0 00-2-2H4zm12 12H4l4-8 3 6 2-4 3 6z" clipRule="evenodd"></path>
  </svg>
);

const ResultsWindows = () => {
  const {
    analysisResults,
    eyeImage,
    layerImages,
    currentLayer,
    setCurrentLayer,
    isCalculating
  } = useLaserAnalysis();

  // Ahora las ventanas aparecen SIEMPRE al inicio, no solo cuando hay resultados
  const handleLayerChange = (event) => {
    setCurrentLayer(parseInt(event.target.value));
  };

  const getRiskLevelColor = (level) => {
    switch (level?.toUpperCase()) {
      case 'BAJO':
        return 'text-green-600 bg-green-100';
      case 'MEDIO':
        return 'text-yellow-600 bg-yellow-100';
      case 'ALTO':
        return 'text-red-600 bg-red-100';
      default:
        return 'text-gray-600 bg-gray-100';
    }
  };

  return (
    <div className="space-y-6">
      {/* Información de Resultados - solo si hay resultados */}
      {analysisResults && (
        <div className="bg-white rounded-lg shadow-md p-6 border border-gray-200">
          <h2 className="text-xl font-bold text-gray-800 mb-4">Resultados del Análisis</h2>
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
            <div className="text-center">
              <div className="text-2xl font-bold text-primary">{analysisResults.totalLayers}</div>
              <div className="text-sm text-gray-600">Capas Totales</div>
            </div>
            <div className="text-center">
              <div className="text-2xl font-bold text-primary">{analysisResults.maxIntensity}%</div>
              <div className="text-sm text-gray-600">Intensidad Máx</div>
            </div>
            <div className="text-center">
              <div className="text-2xl font-bold text-primary">{analysisResults.averageIntensity}%</div>
              <div className="text-sm text-gray-600">Intensidad Prom</div>
            </div>
            <div className="text-center">
              <div className={`text-sm font-semibold px-2 py-1 rounded ${getRiskLevelColor(analysisResults.riskLevel)}`}>
                {analysisResults.riskLevel}
              </div>
              <div className="text-sm text-gray-600">Nivel de Riesgo</div>
            </div>
          </div>
        </div>
      )}

      {/* Ventanas de Visualización - SIEMPRE APARECEN */}
      <div className="analysis-grid">
        {/* Ventana 1: Imagen del Ojo con Isobaras */}
        <div className="window-container">
          <div className="bg-primary text-white px-4 py-2 font-semibold flex items-center">
            <EyeIcon className="w-5 h-5 mr-2" />
            Representación Ocular - Isobaras de Color
          </div>
          <div className="p-4">
            <div className="relative bg-gradient-to-br from-blue-50 to-blue-100 rounded-lg h-96 flex items-center justify-center eye-visualization">
              {isCalculating ? (
                <div className="text-center">
                  <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary mx-auto mb-4"></div>
                  <p className="text-gray-600">Generando representación del ojo...</p>
                </div>
              ) : analysisResults ? (
                /* Simulación de imagen del ojo con isobaras */
                <div className="relative">
                  {/* Ojo simulado */}
                  <div className="w-48 h-48 rounded-full bg-gradient-radial from-gray-200 via-blue-200 to-blue-400 relative overflow-hidden shadow-2xl">
                    {/* Iris */}
                    <div className="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 w-24 h-24 rounded-full bg-gradient-radial from-amber-200 via-amber-400 to-amber-600">
                      {/* Pupila */}
                      <div className="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 w-8 h-8 rounded-full bg-black"></div>
                    </div>
                    
                    {/* Isobaras simuladas */}
                    <div className="absolute inset-0 opacity-70">
                      <div className="absolute top-4 left-4 w-16 h-16 rounded-full border-2 border-red-500 animate-pulse"></div>
                      <div className="absolute top-8 right-8 w-12 h-12 rounded-full border-2 border-yellow-500 animate-pulse" style={{animationDelay: '0.5s'}}></div>
                      <div className="absolute bottom-6 left-6 w-14 h-14 rounded-full border-2 border-green-500 animate-pulse" style={{animationDelay: '1s'}}></div>
                      <div className="absolute bottom-4 right-4 w-10 h-10 rounded-full border-2 border-blue-500 animate-pulse" style={{animationDelay: '1.5s'}}></div>
                    </div>
                  </div>
                  
                  {/* Leyenda de colores */}
                  <div className="absolute bottom-4 right-4 bg-white bg-opacity-90 rounded-lg p-2 text-xs">
                    <div className="font-semibold mb-1">Intensidad:</div>
                    <div className="flex items-center space-x-2">
                      <div className="w-3 h-3 bg-red-500 rounded"></div>
                      <span>Alta</span>
                    </div>
                    <div className="flex items-center space-x-2">
                      <div className="w-3 h-3 bg-yellow-500 rounded"></div>
                      <span>Media</span>
                    </div>
                    <div className="flex items-center space-x-2">
                      <div className="w-3 h-3 bg-green-500 rounded"></div>
                      <span>Baja</span>
                    </div>
                    <div className="flex items-center space-x-2">
                      <div className="w-3 h-3 bg-blue-500 rounded"></div>
                      <span>Mínima</span>
                    </div>
                  </div>
                </div>
              ) : (
                /* Estado inicial - esperando datos */
                <div className="text-center text-gray-500">
                  <EyeIcon className="w-16 h-16 mx-auto mb-4 opacity-50" />
                  <p className="text-lg font-semibold">Representación Ocular</p>
                  <p className="text-sm">Configure los parámetros y ejecute el análisis</p>
                  <p className="text-sm">para visualizar las isobaras de intensidad</p>
                </div>
              )}
            </div>
            
            {analysisResults && (
              <div className="mt-4 text-center text-sm text-gray-600">
                <p>Distribución de intensidad láser en estructura ocular</p>
                <p className="font-semibold text-primary">Resolución: 1024x1024 px | Escala: 1:1000</p>
              </div>
            )}
          </div>
        </div>

        {/* Ventana 2: Visualizador de Capas */}
        <div className="window-container">
          <div className="bg-primary text-white px-4 py-2 font-semibold flex items-center">
            <PhotoIcon className="w-5 h-5 mr-2" />
            Visualización por Capas
          </div>
          <div className="p-4">
            <div className="bg-gray-100 rounded-lg h-96 flex items-center justify-center relative overflow-hidden">
              {isCalculating ? (
                <div className="text-center">
                  <div className="animate-pulse-soft rounded-full h-12 w-12 bg-primary mx-auto mb-4"></div>
                  <p className="text-gray-600">Procesando capas de análisis...</p>
                </div>
              ) : analysisResults ? (
                /* Simulación de imagen de capa actual */
                <div className="w-full h-full bg-gradient-to-br from-gray-200 to-gray-300 flex items-center justify-center">
                  <div className="text-center">
                    <div className="w-32 h-32 mx-auto bg-gradient-radial from-blue-300 via-purple-300 to-red-300 rounded-lg opacity-80 animate-pulse-soft"></div>
                    <div className="mt-4 text-lg font-semibold text-gray-700">
                      Capa {currentLayer + 1} de {analysisResults?.totalLayers || 15}
                    </div>
                    <div className="text-sm text-gray-600">
                      Profundidad: {((currentLayer + 1) * 0.1).toFixed(1)} mm
                    </div>
                  </div>
                </div>
              ) : (
                /* Estado inicial - esperando datos */
                <div className="text-center text-gray-500">
                  <PhotoIcon className="w-16 h-16 mx-auto mb-4 opacity-50" />
                  <p className="text-lg font-semibold">Análisis por Capas</p>
                  <p className="text-sm">Las capas de análisis aparecerán aquí</p>
                  <p className="text-sm">después de ejecutar el cálculo</p>
                </div>
              )}
              
              {/* Información de la capa */}
              {analysisResults && (
                <div className="absolute top-4 left-4 bg-black bg-opacity-60 text-white rounded px-2 py-1 text-xs">
                  Intensidad: {(Math.random() * 100).toFixed(1)}%
                </div>
              )}
            </div>
            
            {/* Slider de capas - solo cuando hay resultados */}
            {analysisResults && (
              <div className="mt-4">
                <div className="flex items-center justify-between mb-2">
                  <label className="text-sm font-semibold text-gray-700">
                    Navegación por Capas:
                  </label>
                  <span className="text-sm text-gray-600">
                    {currentLayer + 1} / {analysisResults?.totalLayers || 15}
                  </span>
                </div>
                
                <input
                  type="range"
                  min="0"
                  max={(analysisResults?.totalLayers || 15) - 1}
                  value={currentLayer}
                  onChange={handleLayerChange}
                  className="layer-slider w-full"
                />
                
                <div className="flex justify-between text-xs text-gray-500 mt-1">
                  <span>Superficie</span>
                  <span>Profundidad Máxima</span>
                </div>
                
                {/* Controles adicionales */}
                <div className="flex justify-center mt-4 space-x-2">
                  <button
                    onClick={() => setCurrentLayer(Math.max(0, currentLayer - 1))}
                    disabled={currentLayer === 0}
                    className="px-3 py-1 bg-primary text-white rounded text-sm disabled:bg-gray-300"
                  >
                    ◀ Anterior
                  </button>
                  <button
                    onClick={() => setCurrentLayer(Math.min((analysisResults?.totalLayers || 15) - 1, currentLayer + 1))}
                    disabled={currentLayer >= (analysisResults?.totalLayers || 15) - 1}
                    className="px-3 py-1 bg-primary text-white rounded text-sm disabled:bg-gray-300"
                  >
                    Siguiente ▶
                  </button>
                </div>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default ResultsWindows;
