/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/**/*.{html,ts}",
  ],
  theme: {
    extend: {
      colors: {
        dark: '#1e1e2e',
        darker: '#181825',
        darkest: '#11111b',
        primary: '#89b4fa',
        secondary: '#f5c2e7',
        success: '#a6e3a1',
        danger: '#f38ba8',
        textMain: '#cdd6f4',
        textMuted: '#a6adc8',
        borderDark: '#313244'
      }
    },
  },
  plugins: [],
}