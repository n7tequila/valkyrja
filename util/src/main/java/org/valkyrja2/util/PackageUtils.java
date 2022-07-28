/*
 * PROJECT valkyrja2
 * util/PackageUtils.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.util;




import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * package工具包
 *
 * @author Tequila
 * @create 2022/05/20 16:31
 **/
public class PackageUtils {
    private static final Logger log = LoggerFactory.getLogger(PackageUtils.class);

    /** 包路径分隔符字符 */
    private static final String SEPARATOR_CHAR = "/";
    /** 点字符 */
    private static final String DOT_CHAR = ".";

    /**
     * 获取某包下（包括该包的所有子包）所有类
     *
     * @param packageName 包名
     * @return {@link List }<{@link String }> 包下所属类的完整名称
     * @throws IOException IO错误时抛出
     * @author Tequila
     * @date 2022/05/21 16:01
     */
    public static List<String> getClassName(String packageName) throws IOException {
        return getClassName(packageName, true);
    }

    /**
     * 获取某包下所有类
     *
     * @param packageName 包名，例如：org.valkyrja2.util
     * @param childPackage 是否遍历子包
     * @return 类的完整名称
     * @throws IOException IO错误时抛出
     */
    public static List<String> getClassName(String packageName, boolean childPackage) throws IOException {
        List<String> fileNames = new ArrayList<>();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        String packagePath = packageName.replace(DOT_CHAR, SEPARATOR_CHAR);
        Enumeration<URL> urls = loader.getResources(packagePath);
        while (urls.hasMoreElements()) {
            URL url = urls.nextElement();
            if (url != null) {
                String type = url.getProtocol();
                if (type.equals("file")) {
                    fileNames.addAll(getClassNameByFile(url.getPath(), packagePath, childPackage));
                } else if (type.equals("jar")) {
                    fileNames.addAll(getClassNameByJar(url.getPath(), childPackage));
                }
            } else {
                fileNames.addAll(getClassNameByJars(((URLClassLoader) loader).getURLs(), packagePath, childPackage));
            }
        }

        return fileNames;
    }

    /**
     * 从项目文件获取某包下所有类
     *
     * @param filePath 文件路径
     * @param childPackage 是否遍历子包
     * @return {@link List }<{@link String }> 所有获取到的类名
     * @author Tequila
     * @date 2022/06/28 10:48
     */
    private static List<String> getClassNameByFile(String filePath, String packagePath, boolean childPackage) {
        List<String> myClassName = new ArrayList<>();
        File file = new File(filePath);
        File[] childFiles = file.listFiles();
        if (childFiles != null) {
            for (File childFile : childFiles) {
                if (childFile.isDirectory()) {
                    if (childPackage) {
                        myClassName.addAll(getClassNameByFile(childFile.getPath(), packagePath, true));
                    }
                } else {
                    String childFilePath = childFile.getPath();
                    if (childFilePath.endsWith(".class")) {
                        childFilePath = childFilePath.substring(childFilePath.indexOf(packagePath), childFilePath.lastIndexOf(DOT_CHAR));
                        childFilePath = childFilePath.replace(SEPARATOR_CHAR, DOT_CHAR);
                        myClassName.add(childFilePath);
                    }
                }
            }
        }

        return myClassName;
    }

    private static final String JAR_FILE_PATH_PREFIX = "file:";

    /**
     * 从jar获取某包下所有类
     *
     * @param jarPath      jar路径
     * @param childPackage 是否遍历子包
     * @return {@link List }<{@link String }> 所有获取到的类名
     * @author Tequila
     * @date 2022/06/28 10:48
     */
    private static List<String> getClassNameByJar(String jarPath, boolean childPackage) throws IOException {
        List<String> myClassName = new ArrayList<>();
        String[] jarInfo = jarPath.split("!");
        String jarFilePath = jarInfo[0].substring(jarInfo[0].indexOf(JAR_FILE_PATH_PREFIX) + JAR_FILE_PATH_PREFIX.length());
        String packagePath = jarInfo[1].substring(1);
        if (packagePath.endsWith(SEPARATOR_CHAR)) {
            packagePath = packagePath.substring(0, packagePath.length() - 1);
        }

        JarFile jarFile = new JarFile(jarFilePath);
        Enumeration<JarEntry> entrys = jarFile.entries();
        while (entrys.hasMoreElements()) {
            JarEntry jarEntry = entrys.nextElement();
            String entryName = jarEntry.getName();
            if (entryName.endsWith(".class")) {
                if (childPackage) {
                    if (entryName.startsWith(packagePath)) {
                        entryName = entryName.replace(SEPARATOR_CHAR, DOT_CHAR).substring(0, entryName.lastIndexOf(DOT_CHAR));
                        myClassName.add(entryName);
                    }
                } else {
                    int index = entryName.lastIndexOf(SEPARATOR_CHAR);
                    String myPackagePath;
                    if (index != -1) {
                        myPackagePath = entryName.substring(0, index);
                    } else {
                        myPackagePath = entryName;
                    }
                    if (myPackagePath.equals(packagePath)) {
                        entryName = entryName.replace(SEPARATOR_CHAR, DOT_CHAR).substring(0, entryName.lastIndexOf(DOT_CHAR));
                        myClassName.add(entryName);
                    }
                }
            }
        }

        return myClassName;
    }

    /**
     * 从所有jar中搜索该包，并获取该包下所有类
     *
     * @param urls URL集合
     * @param packagePath 包路径
     * @param childPackage 是否遍历子包
     * @return {@link List }<{@link String }> 所有获取到的类名
     * @author Tequila
     * @date 2022/06/28 10:43
     */
    private static List<String> getClassNameByJars(URL[] urls, String packagePath, boolean childPackage) throws IOException {
        List<String> myClassName = new ArrayList<>();
        if (urls != null) {
            for (URL url : urls) {
                String urlPath = url.getPath();
                // 不必搜索classes文件夹
                if (urlPath.endsWith("classes" + SEPARATOR_CHAR)) {
                    continue;
                }
                String jarPath = urlPath + "!" + SEPARATOR_CHAR + packagePath;
                myClassName.addAll(getClassNameByJar(jarPath, childPackage));
            }
        }
        return myClassName;
    }

    /**
     * 判断某个类是否实现了某接口
     * 默认排除抽象类
     *
     * @param klass 需要判断的类对象
     * @param type  类型
     * @return boolean
     * @author Tequila
     * @date 2022/06/28 10:43
     */
    public static boolean isImplements(Class<?> klass, Class<?> type) {
        return isImplements(klass, type, true);
    }

    /**
     * 判断某个类是否实现了某接口
     *
     * @param klass           需要判断的类对象
     * @param type            类型
     * @param excludeAbstract 排除文摘
     * @return boolean
     * @author Tequila
     * @date 2022/06/28 10:44
     */
    public static boolean isImplements(Class<?> klass, Class<?> type, boolean excludeAbstract) {
        Class<?>[] interfaces = klass.getInterfaces();
        for (Class<?> i: interfaces) {
            if (i == type  // 判断是否实现了接口
                    && (!excludeAbstract || !Modifier.isAbstract(klass.getModifiers()))) {  // 如果不排除抽象类，则如果抽象类也实现了接口，也返回true
                return true;
            }
        }

        return false;
    }

    /**
     * 获取WEB-INF目录
     *
     * @return {@link String }
     * @author Tequila
     * @date 2022/06/28 10:45
     */
    public static String getWebInfDir() {
        String path = Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("")).getPath();
        return path.substring(0, path.indexOf("/WEB-INF") + "/WEB-INF".length());
    }

    private PackageUtils() {
        throw new IllegalStateException("Utility class");
    }
}
