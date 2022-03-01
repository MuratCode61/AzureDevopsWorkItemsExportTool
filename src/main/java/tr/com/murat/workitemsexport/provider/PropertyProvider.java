package tr.com.murat.workitemsexport.provider;

import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.Properties;

@Slf4j
public class PropertyProvider {

    private static Properties properties = null;

    private static synchronized void loadProperties() {
        if(properties == null) {
            try {
                InputStream inputStream = PropertyProvider.class.getClassLoader().getResourceAsStream("config.properties");
                if (inputStream != null) {
                    properties = new Properties();
                    properties.load(inputStream);
                } else {
                    log.error("Property file {} could not be found in the classpath");
                }
            } catch (Exception e) {
                log.error("loadProperties - Exception has been occurred.", e);
                properties = null;
            }
        }
    }

    public static String getProperty(String key) {
        if(properties == null) {
            loadProperties();
        }
        return properties.getProperty(key);
    }
}
