package github.cheneykwok.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

@Slf4j
public class PropertiesFileUtil {

    public static Properties readPropertiesFile(String fileName) {
        URL url = Thread.currentThread().getContextClassLoader().getResource("");
        String rpcConfigPath = "";
        if (url != null) {
            rpcConfigPath = url.getPath() + fileName;
        }
        Properties properties = null;
        try (InputStreamReader reader = new InputStreamReader(Files.newInputStream(Paths.get(rpcConfigPath)), StandardCharsets.UTF_8)) {
            properties = new Properties();
            properties.load(reader);
        } catch (Exception e) {
            log.error("occur exception when read properties file [{}]", fileName);
        }

        return properties;
    }
}
