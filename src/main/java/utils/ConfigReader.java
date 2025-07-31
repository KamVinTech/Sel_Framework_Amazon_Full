package utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {
    private static final Logger logger = LogManager.getLogger(ConfigReader.class);
    private static final Properties properties = new Properties();
    private static final String DEFAULT_CONFIG_PATH = "src/test/resources/config/config.properties";
    private static boolean isInitialized = false;

    static {
        initializeConfig();
    }

    private static void initializeConfig() {
        if (!isInitialized) {
            try {
                // Load default config
                loadProperties(DEFAULT_CONFIG_PATH);

                // Load environment specific config if specified
                String env = System.getProperty("env", "dev");
                String envConfigPath = String.format("src/test/resources/config/%s.properties", env);
                loadProperties(envConfigPath);

                isInitialized = true;
            } catch (IOException e) {
                logger.error("Failed to load configuration: " + e.getMessage());
                throw new RuntimeException("Failed to load configuration", e);
            }
        }
    }

    private static void loadProperties(String filePath) throws IOException {
        try (FileInputStream fis = new FileInputStream(filePath)) {
            properties.load(fis);
            logger.info("Loaded configuration from: " + filePath);
        } catch (IOException e) {
            logger.warn("Could not load configuration from: " + filePath);
            if (filePath.equals(DEFAULT_CONFIG_PATH)) {
                throw e;
            }
        }
    }

    public static String getValue(String key) {
        return getValue(key, null);
    }

    public static String getValue(String key, String defaultValue) {
        String value = System.getProperty(key);
        if (value == null) {
            value = properties.getProperty(key, defaultValue);
        }
        return value;
    }

    public static int getIntValue(String key, int defaultValue) {
        String value = getValue(key);
        try {
            return value != null ? Integer.parseInt(value) : defaultValue;
        } catch (NumberFormatException e) {
            logger.warn("Invalid integer value for key: " + key + ". Using default: " + defaultValue);
            return defaultValue;
        }
    }

    public static boolean getBooleanValue(String key, boolean defaultValue) {
        String value = getValue(key);
        return value != null ? Boolean.parseBoolean(value) : defaultValue;
    }

    public static void setValue(String key, String value) {
        properties.setProperty(key, value);
    }
}
