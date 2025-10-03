package org.kpah.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigLoader {
    private final Properties props = new Properties();

    public ConfigLoader(String configFilePath) {
        try (FileInputStream fis = new FileInputStream(configFilePath)) {
            props.load(fis);
        } catch (IOException e) {
            System.err.println("Cant load config file: " + configFilePath);
        }
    }

    // Get config value: ENV > FILE > DEFAULT
    public String getString(String key, String defaultValue) {
        String env = System.getenv(key);
        if (env != null && !env.isEmpty())
            return env;

        String prop = props.getProperty(key);
        return (prop != null && !prop.isEmpty()) ? prop : defaultValue;
    }

    // Get int config value
    public int getInt(String key, int defaultValue) {
        String valueStr = getString(key, String.valueOf(defaultValue));
        try {
            int value = Integer.parseInt(valueStr);
            return value;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    // Get byte config value
    public byte getByte(String key, byte defaultValue) {
        String valueStr = getString(key, String.valueOf(defaultValue));
        try {
            byte value = Byte.parseByte(valueStr);
            return value;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    // Get short config value
    public short getShort(String key, short defaultValue) {
        String valueStr = getString(key, String.valueOf(defaultValue));
        try {
            short value = Short.parseShort(valueStr);
            return value;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    // Get long config value
    public long getLong(String key, long defaultValue) {
        String valueStr = getString(key, String.valueOf(defaultValue));
        try {
            long value = Long.parseLong(valueStr);
            return value;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    // Get double config value
    public double getDouble(String key, double defaultValue) {
        String valueStr = getString(key, String.valueOf(defaultValue));
        try {
            double value = Double.parseDouble(valueStr);
            return value;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    // Get float config value
    public float getFloat(String key, float defaultValue) {
        String valueStr = getString(key, String.valueOf(defaultValue));
        try {
            float value = Float.parseFloat(valueStr);
            return value;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    // Get boolean config value
    public boolean getBoolean(String key, boolean defaultValue) {
        String valueStr = getString(key, String.valueOf(defaultValue));
        return Boolean.parseBoolean(valueStr);
    }

}
