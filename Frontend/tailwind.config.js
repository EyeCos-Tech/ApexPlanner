/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/**/*.{js,jsx,ts,tsx}",
    "./public/index.html"
  ],
  theme: {
    extend: {
      colors: {
        primary: {
          DEFAULT: '#5a9891',
          50: '#f0f9f8',
          100: '#d8f0ed',
          200: '#b6e2dc',
          300: '#8eccc4',
          400: '#67b0a6',
          500: '#5a9891',
          600: '#4a7b75',
          700: '#3e645f',
          800: '#34524e',
          900: '#2e4442',
          'light': '#BDD6D3',
          'dark': '#4a7b75'
        },
        secondary: '#e2e2e2',
        accent: '#f8f9fa',
        laser: {
          red: '#ff4444',
          green: '#44ff44',
          blue: '#4444ff',
          infrared: '#cc0000',
          ultraviolet: '#8800cc'
        }
      },
      fontFamily: {
        'raleway': ['Raleway', 'sans-serif'],
      },
      backgroundImage: {
        'gradient-radial': 'radial-gradient(var(--tw-gradient-stops))',
      },
      animation: {
        'fade-in': 'fadeIn 0.3s ease-in-out',
        'slide-in-right': 'slideInRight 0.3s ease-out',
        'pulse-soft': 'pulseSoft 2s infinite',
      },
      keyframes: {
        fadeIn: {
          '0%': { opacity: '0', transform: 'translateY(10px)' },
          '100%': { opacity: '1', transform: 'translateY(0)' },
        },
        slideInRight: {
          '0%': { opacity: '0', transform: 'translateX(100px)' },
          '100%': { opacity: '1', transform: 'translateX(0)' },
        },
        pulseSoft: {
          '0%, 100%': { opacity: '1' },
          '50%': { opacity: '0.7' },
        }
      }
    },
  },
  plugins: [],
}
