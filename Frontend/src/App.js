import React, { useEffect } from 'react';
import './App.css';
import { LaserAnalysisProvider, useLaserAnalysis } from './context/LaserAnalysisContext';
import InputForm from './components/InputForm';
import ResultsWindows from './components/ResultsWindows';

const AppContent = () => {
  const { 
    folderPath,
    laserType, 
    waveLengthPower, 
    isCalculating,
    analysisResults,
    startCalculation,
    setAnalysisResults
  } = useLaserAnalysis();

  // Simular análisis cuando se activa el cálculo
  useEffect(() => {
    if (isCalculating) {
      const timer = setTimeout(() => {
        // Simulación de resultados del análisis
        const mockResults = {
          results: {
            totalLayers: 15,
            maxIntensity: 98.7,
            averageIntensity: 45.2,
            riskLevel: 'MEDIO'
          },
          eyeImage: '/api/mock/eye-isobar-image',
          layerImages: Array.from({ length: 15 }, (_, i) => `/api/mock/layer-${i + 1}`)
        };
        
        setAnalysisResults(mockResults);
      }, 3000);

      return () => clearTimeout(timer);
    }
  }, [isCalculating, setAnalysisResults]);

  return (
    <div className="min-h-screen bg-accent">
      {/* Header */}
      <header className="bg-white shadow-sm border-b border-gray-200">
        <div className="max-w-7xl mx-auto px-4 py-4">
          <div className="flex items-center justify-between">
            <div className="flex items-center space-x-3">
              <div className="w-10 h-10 bg-primary rounded-lg flex items-center justify-center">
                <span className="text-white font-bold text-lg">L</span>
              </div>
              <div>
                <h1 className="text-xl font-bold text-gray-900">Laser Eye Analyzer</h1>
                <p className="text-sm text-gray-600">Sistema de Análisis de Seguridad Láser Ocular</p>
              </div>
            </div>
            
            {/* Status indicator */}
            <div className="flex items-center space-x-2">
              <div className={`w-3 h-3 rounded-full ${
                isCalculating ? 'bg-yellow-400 animate-pulse' : 
                analysisResults ? 'bg-green-400' : 'bg-gray-300'
              }`}></div>
              <span className="text-sm text-gray-600">
                {isCalculating ? 'Analizando...' : 
                 analysisResults ? 'Análisis Completo' : 'Listo'}
              </span>
            </div>
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="max-w-7xl mx-auto px-4 py-8">
        {/* Results Windows - AHORA APARECEN PRIMERO */}
        <ResultsWindows />
        
        {/* Progress Info */}
        {isCalculating && (
          <div className="mt-8 bg-blue-50 border border-blue-200 rounded-lg p-4">
            <div className="flex items-center">
              <div className="animate-spin rounded-full h-5 w-5 border-b-2 border-blue-600 mr-3"></div>
              <div>
                <h3 className="text-blue-800 font-semibold">Procesando Análisis</h3>
                <p className="text-blue-600 text-sm">
                  Calculando efectos del láser en estructura ocular...
                </p>
              </div>
            </div>
            <div className="mt-3 bg-blue-200 rounded-full h-2">
              <div className="bg-blue-600 h-2 rounded-full animate-pulse" style={{width: '60%'}}></div>
            </div>
          </div>
        )}
        
        {/* Input Form - AHORA APARECE DESPUÉS */}
        <div className="mt-8">
          <InputForm />
        </div>
      </main>

      {/* Footer */}
      <footer className="bg-white border-t border-gray-200 mt-16">
        <div className="max-w-7xl mx-auto px-4 py-6">
          <div className="text-center text-gray-600 text-sm">
            <p>Sistema de Análisis de Efectos Láser Ocular</p>
            <p className="mt-1">Desarrollado para análisis de seguridad y evaluación de riesgos</p>
          </div>
        </div>
      </footer>
    </div>
  );
};

function App() {
  return (
    <LaserAnalysisProvider>
      <AppContent />
    </LaserAnalysisProvider>
  );
}

export default App;
