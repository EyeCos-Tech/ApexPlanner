import React, { useRef } from 'react';
import { useLaserAnalysis } from '../context/LaserAnalysisContext';

const LASER_TYPES = [
  { value: 'red_diode', label: 'Diodo Rojo (635-670 nm)', color: 'text-red-500' },
  { value: 'green_diode', label: 'Diodo Verde (515-545 nm)', color: 'text-green-500' },
  { value: 'blue_diode', label: 'Diodo Azul (445-465 nm)', color: 'text-blue-500' },
  { value: 'infrared', label: 'Infrarrojo (780-1400 nm)', color: 'text-red-800' },
  { value: 'nd_yag', label: 'Nd:YAG (1064 nm)', color: 'text-gray-600' },
  { value: 'co2', label: 'CO2 (10600 nm)', color: 'text-orange-600' },
  { value: 'argon', label: 'Argón (488-514 nm)', color: 'text-cyan-500' },
  { value: 'he_ne', label: 'He-Ne (632.8 nm)', color: 'text-red-400' }
];

// Componentes de iconos SVG
const FolderIcon = ({ className }) => (
  <svg className={className} fill="currentColor" viewBox="0 0 20 20">
    <path d="M2 6a2 2 0 012-2h5l2 2h5a2 2 0 012 2v6a2 2 0 01-2 2H4a2 2 0 01-2-2V6z"></path>
  </svg>
);

const BeakerIcon = ({ className }) => (
  <svg className={className} fill="currentColor" viewBox="0 0 20 20">
    <path fillRule="evenodd" d="M7 2a1 1 0 00-.707 1.707L7 4.414v3.758a1 1 0 01-.293.707l-4 4C.817 14.769 2.156 18 4.828 18h10.343c2.673 0 4.012-3.231 2.122-5.121l-4-4A1 1 0 0113 8.172V4.414l.707-.707A1 1 0 0013 2H7zm2 6.172V4h2v4.172a3 3 0 00.879 2.12l1.027 1.028a4 4 0 00-2.171.102l-.47.156a4 4 0 01-2.53 0l-.563-.187a1.993 1.993 0 00-.415-.084l1.358-1.358A3 3 0 009 8.172z" clipRule="evenodd"></path>
  </svg>
);

const BoltIcon = ({ className }) => (
  <svg className={className} fill="currentColor" viewBox="0 0 20 20">
    <path fillRule="evenodd" d="M11.3 1.046A1 1 0 0112 2v5h4a1 1 0 01.82 1.573l-7 10A1 1 0 018 18v-5H4a1 1 0 01-.82-1.573l7-10a1 1 0 011.12-.38z" clipRule="evenodd"></path>
  </svg>
);

const InputForm = () => {
  const {
    folderPath,
    laserType,
    waveLengthPower,
    isCalculating,
    errors,
    setFolderPath,
    setLaserType,
    setWaveLengthPower,
    startCalculation,
    canCalculate
  } = useLaserAnalysis();

  const folderInputRef = useRef(null);

  const handleFolderSelect = () => {
    // Simulamos la selección de carpeta para demo
    setFolderPath('C:\\LaserData\\Experiment_2024');
  };

  const handleFolderChange = (event) => {
    const files = event.target.files;
    if (files.length > 0) {
      const folderPath = files[0].webkitRelativePath.split('/')[0];
      setFolderPath(folderPath);
    }
  };

  const handleLaserTypeChange = (event) => {
    setLaserType(event.target.value);
  };

  const handlePowerChange = (event) => {
    const value = event.target.value;
    // Validar que solo sean números y punto decimal
    if (value === '' || /^\d*\.?\d*$/.test(value)) {
      setWaveLengthPower(value);
    }
  };

  const handleCalculate = () => {
    if (canCalculate()) {
      startCalculation();
      // Aquí se haría la llamada real al backend
      simulateAnalysis();
    }
  };

  const simulateAnalysis = () => {
    // Simulación de análisis para demo
    setTimeout(() => {
      // Aquí normalmente vendría del backend
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
      
      // En el contexto real, esto vendría del resultado del análisis
      // setAnalysisResults(mockResults);
    }, 3000);
  };

  const getFieldError = (fieldName) => {
    return errors.find(error => error.field === fieldName)?.message;
  };

  return (
    <div className="card max-w-2xl mx-auto">
      <div className="text-center mb-8">
        <h1 className="text-3xl font-bold text-gray-800 mb-2">
          Analizador de Efectos Láser Ocular
        </h1>
        <p className="text-gray-600">
          Configure los parámetros del láser para realizar el análisis de seguridad ocular
        </p>
      </div>

      <div className="space-y-6">
        {/* Selector de Carpeta */}
        <div>
          <label className="block text-sm font-semibold text-gray-700 mb-2">
            <FolderIcon className="inline-block w-4 h-4 mr-2" />
            Carpeta de Datos
          </label>
          <div className="flex gap-3">
            <input
              type="text"
              value={folderPath}
              readOnly
              placeholder="Seleccionar carpeta con datos del láser..."
              className={`input-field flex-1 ${getFieldError('folderPath') ? 'border-red-500' : ''}`}
            />
            <button
              type="button"
              onClick={handleFolderSelect}
              className="btn-primary whitespace-nowrap"
              disabled={isCalculating}
            >
              Examinar
            </button>
          </div>
          <input
            ref={folderInputRef}
            type="file"
            onChange={handleFolderChange}
            style={{ display: 'none' }}
            multiple
          />
          {getFieldError('folderPath') && (
            <p className="text-red-500 text-sm mt-1">{getFieldError('folderPath')}</p>
          )}
        </div>

        {/* Tipo de Láser */}
        <div>
          <label className="block text-sm font-semibold text-gray-700 mb-2">
            <BeakerIcon className="inline-block w-4 h-4 mr-2" />
            Tipo de Láser
          </label>
          <select
            value={laserType}
            onChange={handleLaserTypeChange}
            className={`input-field w-full ${getFieldError('laserType') ? 'border-red-500' : ''}`}
            disabled={isCalculating}
          >
            <option value="">Seleccionar tipo de láser...</option>
            {LASER_TYPES.map((type) => (
              <option key={type.value} value={type.value}>
                {type.label}
              </option>
            ))}
          </select>
          {getFieldError('laserType') && (
            <p className="text-red-500 text-sm mt-1">{getFieldError('laserType')}</p>
          )}
        </div>

        {/* Potencia/Longitud de Onda */}
        <div>
          <label className="block text-sm font-semibold text-gray-700 mb-2">
            <BoltIcon className="inline-block w-4 h-4 mr-2" />
            Potencia (mW) / Longitud de Onda (nm)
          </label>
          <input
            type="text"
            value={waveLengthPower}
            onChange={handlePowerChange}
            placeholder="Ej: 5.0 mW / 650 nm"
            className={`input-field w-full ${getFieldError('waveLengthPower') ? 'border-red-500' : ''}`}
            disabled={isCalculating}
          />
          {getFieldError('waveLengthPower') && (
            <p className="text-red-500 text-sm mt-1">{getFieldError('waveLengthPower')}</p>
          )}
          <p className="text-gray-500 text-sm mt-1">
            Ingrese el valor numérico de potencia y/o longitud de onda
          </p>
        </div>

        {/* Botón Calcular */}
        <div className="pt-4">
          <button
            onClick={handleCalculate}
            disabled={!canCalculate()}
            className={`w-full py-3 px-6 rounded-lg font-semibold text-white transition-all duration-200 ${
              canCalculate()
                ? 'bg-primary hover:bg-primary-dark transform hover:scale-105 shadow-lg'
                : 'bg-gray-300 cursor-not-allowed'
            }`}
          >
            {isCalculating ? (
              <div className="flex items-center justify-center">
                <div className="animate-spin rounded-full h-5 w-5 border-b-2 border-white mr-2"></div>
                Calculando análisis...
              </div>
            ) : (
              'Calcular Análisis'
            )}
          </button>
        </div>

        {/* Información de Estado */}
        {folderPath && laserType && waveLengthPower && (
          <div className="bg-primary-50 border border-primary-200 rounded-lg p-4 mt-4">
            <h3 className="font-semibold text-primary-800 mb-2">Configuración Lista:</h3>
            <ul className="text-sm text-primary-700 space-y-1">
              <li>📁 Carpeta: {folderPath}</li>
              <li>🔬 Láser: {LASER_TYPES.find(t => t.value === laserType)?.label}</li>
              <li>⚡ Potencia/Longitud: {waveLengthPower}</li>
            </ul>
          </div>
        )}
      </div>
    </div>
  );
};

export default InputForm;
