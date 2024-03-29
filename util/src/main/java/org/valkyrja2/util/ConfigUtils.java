package org.valkyrja2.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

/**
 * 配置工具包
 *
 * @author Tequila
 * @create 2022/08/05 11:09
 **/
public final class ConfigUtils {

    private static final Logger log = LoggerFactory.getLogger(ConfigUtils.class);

    public static final String CLASSPATH_FILE_FLAG = "classpath:";

    /**
     * <p>Load the properties from provided file.</p>
     * <p>Currently it supports reading from classpath file or local file.</p>
     *
     * @param fileName valid file path
     * @return the retrieved properties from the file; null if the file not exist
     */
    public static Properties loadProperties(String fileName) {
        if (StringUtils.isNotBlank(fileName)) {
            if (absolutePathStart(fileName)) {
                return loadPropertiesFromAbsoluteFile(fileName);
            } else if (fileName.startsWith(CLASSPATH_FILE_FLAG)) {
                return loadPropertiesFromClasspathFile(fileName);
            } else {
                return loadPropertiesFromRelativeFile(fileName);
            }
        } else {
            return null;
        }
    }

    private static Properties loadPropertiesFromAbsoluteFile(String fileName) {
        Properties properties = null;
        try {

            File file = new File(fileName);
            if (!file.exists()) {
                return null;
            }

            try (BufferedReader bufferedReader =
                         new BufferedReader(new InputStreamReader(new FileInputStream(file), getCharset()))) {
                properties = new Properties();
                properties.load(bufferedReader);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return properties;
    }

    private static boolean absolutePathStart(String path) {
        File[] files = File.listRoots();
        for (File file : files) {
            if (path.startsWith(file.getPath())) {
                return true;
            }
        }
        return false;
    }


    private static Properties loadPropertiesFromClasspathFile(String fileName) {
        fileName = fileName.substring(CLASSPATH_FILE_FLAG.length()).trim();

        List<URL> list = new ArrayList<>();
        try {
            Enumeration<URL> urls = getClassLoader().getResources(fileName);
            list = new ArrayList<>();
            while (urls.hasMoreElements()) {
                list.add(urls.nextElement());
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        if (list.isEmpty()) {
            return null;
        }

        Properties properties = new Properties();
        for (URL url : list) {
            try (BufferedReader bufferedReader =
                         new BufferedReader(new InputStreamReader(url.openStream(), getCharset()))) {
                Properties p = new Properties();
                p.load(bufferedReader);
                properties.putAll(p);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return properties;
    }

    private static Properties loadPropertiesFromRelativeFile(String fileName) {
        String userDir = System.getProperty("user.dir");
        String realFilePath = addSeparator(userDir) + fileName;
        return loadPropertiesFromAbsoluteFile(realFilePath);
    }

    private static ClassLoader getClassLoader() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = ConfigUtils.class.getClassLoader();
        }
        return classLoader;
    }

    private static Charset getCharset() {
        // avoid static loop dependencies: SentinelConfig -> SentinelConfigLoader -> ConfigUtil -> SentinelConfig
        // so not use SentinelConfig.charset()
        return Charset.forName(System.getProperty("csp.sentinel.charset", StandardCharsets.UTF_8.name()));
    }

    public static String addSeparator(String dir) {
        if (!dir.endsWith(File.separator)) {
            dir += File.separator;
        }
        return dir;
    }

    private ConfigUtils() {
        throw new IllegalStateException("Utility class");
    }
}
