module.exports = {
    moduleFileExtensions: ["js", "jsx", "json", "vue"],
    transform: {
      "^.+\\.vue$": "@vue/vue3-jest",
      "^.+\\.jsx?$": "babel-jest"
    },
    setupFiles: ["<rootDir>/tests/unit/index.js"],
    testEnvironmentOptions: {
        customExportConditions: ["node", "node-addons"],
     },
    testEnvironment: 'jsdom',
    transformIgnorePatterns: ["/node_modules/"],
    collectCoverage: false,
    coverageDirectory: "<rootDir>/coverage",
    collectCoverageFrom: ["<rootDir>/src/store/modules/**/*.(actions|getters).js", "<rootDir>/src/components/**/*.{js,jsx,vue}"],
    moduleNameMapper: {
      "^@/(.*)$": "<rootDir>/src/$1"
    },
    testMatch: ["**/tests/unit/**/*.spec.(js|jsx|ts|tsx)", "**/__tests__/*.(js|jsx|ts|tsx)"],
    coverageReporters: ["text", "html", "cobertura", "lcov"]
  }
  