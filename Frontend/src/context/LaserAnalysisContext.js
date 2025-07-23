import React, { createContext, useContext, useReducer } from 'react';

const LaserAnalysisContext = createContext();

const initialState = {
  folderPath: '',
  laserType: '',
  waveLengthPower: '',
  isCalculating: false,
  analysisResults: null,
  eyeImage: null,
  layerImages: [],
  currentLayer: 0,
  errors: []
};

const laserAnalysisReducer = (state, action) => {
  switch (action.type) {
    case 'SET_FOLDER_PATH':
      return {
        ...state,
        folderPath: action.payload,
        errors: state.errors.filter(error => error.field !== 'folderPath')
      };
    case 'SET_LASER_TYPE':
      return {
        ...state,
        laserType: action.payload,
        errors: state.errors.filter(error => error.field !== 'laserType')
      };
    case 'SET_WAVELENGTH_POWER':
      return {
        ...state,
        waveLengthPower: action.payload,
        errors: state.errors.filter(error => error.field !== 'waveLengthPower')
      };
    case 'START_CALCULATION':
      return {
        ...state,
        isCalculating: true,
        errors: []
      };
    case 'SET_ANALYSIS_RESULTS':
      return {
        ...state,
        isCalculating: false,
        analysisResults: action.payload.results,
        eyeImage: action.payload.eyeImage,
        layerImages: action.payload.layerImages
      };
    case 'SET_CURRENT_LAYER':
      return {
        ...state,
        currentLayer: action.payload
      };
    case 'SET_ERROR':
      return {
        ...state,
        isCalculating: false,
        errors: [...state.errors, action.payload]
      };
    case 'CLEAR_ERRORS':
      return {
        ...state,
        errors: []
      };
    case 'RESET_ANALYSIS':
      return {
        ...initialState
      };
    default:
      return state;
  }
};

export const LaserAnalysisProvider = ({ children }) => {
  const [state, dispatch] = useReducer(laserAnalysisReducer, initialState);

  const setFolderPath = (path) => {
    dispatch({ type: 'SET_FOLDER_PATH', payload: path });
  };

  const setLaserType = (type) => {
    dispatch({ type: 'SET_LASER_TYPE', payload: type });
  };

  const setWaveLengthPower = (power) => {
    dispatch({ type: 'SET_WAVELENGTH_POWER', payload: power });
  };

  const startCalculation = () => {
    dispatch({ type: 'START_CALCULATION' });
  };

  const setAnalysisResults = (results) => {
    dispatch({ type: 'SET_ANALYSIS_RESULTS', payload: results });
  };

  const setCurrentLayer = (layer) => {
    dispatch({ type: 'SET_CURRENT_LAYER', payload: layer });
  };

  const setError = (error) => {
    dispatch({ type: 'SET_ERROR', payload: error });
  };

  const clearErrors = () => {
    dispatch({ type: 'CLEAR_ERRORS' });
  };

  const resetAnalysis = () => {
    dispatch({ type: 'RESET_ANALYSIS' });
  };

  const canCalculate = () => {
    return state.folderPath && state.laserType && state.waveLengthPower && !state.isCalculating;
  };

  const value = {
    ...state,
    setFolderPath,
    setLaserType,
    setWaveLengthPower,
    startCalculation,
    setAnalysisResults,
    setCurrentLayer,
    setError,
    clearErrors,
    resetAnalysis,
    canCalculate
  };

  return (
    <LaserAnalysisContext.Provider value={value}>
      {children}
    </LaserAnalysisContext.Provider>
  );
};

export const useLaserAnalysis = () => {
  const context = useContext(LaserAnalysisContext);
  if (context === undefined) {
    throw new Error('useLaserAnalysis must be used within a LaserAnalysisProvider');
  }
  return context;
};
