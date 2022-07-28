/*
 * PROJECT valkyrja2
 * util/SystemSetting.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.util;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 系统配置文件的缓存对象，使用单例方法来对所有程序提供服务
 * 
 * 系统变量<br>
 * <br>
 * java.version						Java 运行时环境版本<br>
 * java.vendor						Java 运行时环境供应商<br>
 * java.vendor.url					Java 供应商的 URL<br>
 * java.home 						Java 安装目录<br>
 * java.vm.specification.version	Java 虚拟机规范版本<br>
 * java.vm.specification.vendor		Java 虚拟机规范供应商<br>
 * java.vm.specification.name		Java 虚拟机规范名称<br>
 * java.vm.version					Java 虚拟机实现版本<br>
 * java.vm.vendor					Java 虚拟机实现供应商<br>
 * java.vm.name						Java 虚拟机实现名称<br>
 * java.specification.version		Java 运行时环境规范版本<br>
 * java.specification.vendor		Java 运行时环境规范供应商<br>
 * java.specification.name			Java 运行时环境规范名称<br>
 * java.class.version				Java 类格式版本号<br>
 * java.class.path					Java 类路径<br>
 * java.library.path				加载库时搜索的路径列表<br>
 * java.io.tmpdir					默认的临时文件路径<br>
 * java.compiler					要使用的 JIT 编译器的名称<br>
 * java.ext.dirs					一个或多个扩展目录的路径<br>
 * os.name							操作系统的名称<br>
 * os.arch							操作系统的架构<br>
 * os.version						操作系统的版本<br>
 * file.separator					文件分隔符（在 UNIX 系统中是“/”）<br>
 * path.separator					路径分隔符（在 UNIX 系统中是“:”）<br>
 * line.separator					行分隔符（在 UNIX 系统中是“/n”）<br>
 * user.name						用户的账户名称<br>
 * user.home						用户的主目录<br>
 * user.dir							用户的当前工作目录<br>
 * project.dir						当前项目目录<br>
 * project.web.dir					当前Web项目目录<br>
 * 
 * @author Tequila
 *
 */
public class SystemSetting {
	
	private static final Logger log = LoggerFactory.getLogger(SystemSetting.class);
	
	private static volatile SystemSetting _instance;

	private SystemSetting() { init(); }
	/**
	 * 获取DNABuilder的操作实例
	 * @return DNABuilder
	 */
	public static SystemSetting getInstance() {
		if (_instance == null) {
			synchronized (SystemSetting.class) {
				if (_instance == null) {
					_instance = new SystemSetting();
				}
			}
		}
		return _instance;
	}
	
	private static final String SETTING_FILE = "setting.properties";
	private static final String SETTING_FILE_EXT = ".properties";
	private static final String PREFIX = "\\{";
	private static final String SUFFIX = "\\}";
	private static final String REGX   = "\\{(.+?)\\}";
	
	public static final String SYS_VAR_JAVA_IO_TMPDIR = "java.io.tmpdir";
	public static final String SYS_VAR_PROJECT_DIR     = "project.dir";
	public static final String SYS_VAR_PROJECT_WEB_DIR = "project.web.dir";
	public static final String SYS_VAR_PROJECT_CLASS_DIR = "project.class.dir";

	/** 当前系统的名称 */
	public static final String PROP_APP_NAME = "appName";
	/** debug模式开关 */
	public static final String PROP_DEBUG_KEY = "debug";
	/** 调试输出dir */
	public static final String DEBUG_OUTPUT_DIR = "debug.output";
	/** 引用的其他properties文件 */
	public static final String PROP_INCULDE = "_include";
	
	public static final String INT_TRUE_VALUE = "1";
	public static final String INT_FALSE_VALUE = "0";
	public static final String TRUE_VALUE = "true";
	public static final String FALSE_VALUE = "false";
	
	public static final String MSG_FILE_NOT_EXISTS = "Can not found setting file `{}`";
	
	/** 已载入的配置文件路径 */
	private String loadedPropFilePath;

	/** 系统参数 */
	private final Properties sysProp = System.getProperties();

	/** 配置文件后去的参数 */
	private final Map<String, Object> settings = new HashMap<>();

	/** 宏参数 */
	private final Map<String, String> macros = new HashMap<>();

	/**
	 * 初始化系统配置文件
	 *
	 * @author Tequila
	 * @date 2022/06/28 15:29
	 */
	private void init() {
		// nothing
	}

	/**
	 * 重新载入配置文件<br>
	 * synchronized方法 每次同时只可以载入一次
	 *
	 * @param settingFile 设置文件
	 * @throws IOException IO异常
	 * @author Tequila
	 * @date 2022/06/28 15:26
	 */
	public void load(String settingFile) throws IOException {
		this.loadedPropFilePath = settingFile;

		loadFromFile(settingFile);
		
		Properties props = System.getProperties();
		for (Object o : props.keySet()) {
			String key = (String) o;
			putMacro(key, props.getProperty(key));
		}
		
		String projectPath = "";
		try {
			projectPath = new File(Objects.requireNonNull(this.getClass().getResource("/")).toURI()).toString();
		} catch (URISyntaxException e) { /* ignore */ }
		putMacro(SYS_VAR_PROJECT_CLASS_DIR, projectPath);
		put(SYS_VAR_PROJECT_CLASS_DIR, projectPath);
		
		int pos = -1;
		pos = projectPath.indexOf(File.separatorChar + "classes", 0);
		if (pos >= 0) {
			projectPath = projectPath.substring(0, pos);
		}
		putMacro(SYS_VAR_PROJECT_WEB_DIR, projectPath);
		put(SYS_VAR_PROJECT_WEB_DIR, projectPath);
		
		pos = projectPath.indexOf(File.separatorChar + "WEB-INF", 0);
		if (pos >= 0) {
			projectPath = projectPath.substring(0, pos);
		}
		putMacro(SYS_VAR_PROJECT_DIR, projectPath);
		put(SYS_VAR_PROJECT_DIR, projectPath);
		
		loadInclude();
	}

	/**
	 * 重新载入配置文件
	 *
	 * @throws IOException IO异常
	 * @author Tequila
	 * @date 2022/06/28 15:26
	 */
	public void load() throws IOException {
		URL url = SystemSetting.class.getClassLoader().getResource(SETTING_FILE);
		if (url == null) {
			throw new FileNotFoundException("Can not found setting file `setting.properties.`");
		}

		try {
			String sysPropFilePath = url.toURI().getPath();
			load(sysPropFilePath);
		} catch (URISyntaxException e) {
			throw new IOException(e.getMessage(), e);
		}
	}

	/**
	 * 定义项目初始路径，并载入配置文件
	 *
	 * @param projectRoot 项目根
	 * @author Tequila
	 * @date 2022/06/28 15:47
	 */
	public void loadFormRoot(String projectRoot) throws IOException {
		String sysPropFilePath = projectRoot + File.separatorChar + SETTING_FILE;
		load(sysPropFilePath);
	}

	/**
	 * 重新载入配置文件
	 *
	 * @throws IOException IO异常
	 * @author Tequila
	 * @date 2022/06/28 15:30
	 */
	public void reload() throws IOException {
		load(this.loadedPropFilePath);
	}

	/**
	 * 载入引用的配置文件
	 *
	 * @author Tequila
	 * @date 2022/06/28 15:31
	 */
	private void loadInclude() throws IOException {
		if (getProperty(PROP_INCULDE) != null) {
			String[] includes = getProperty(PROP_INCULDE).toString().split(",");
			for (String include: includes) {
				String fileName = include + SETTING_FILE_EXT;
				String includePropFilePath = Objects.requireNonNull(SystemSetting.class.getClassLoader().getResource("")).getPath() + File.separatorChar + fileName;
				loadFromFile(includePropFilePath);
			}
		}
	}

	/**
	 * 读取配置文件内容，并放入SystemSetting中
	 *
	 * @param propFilePath properties文件路径
	 * @author Tequila
	 * @date 2022/06/28 15:01
	 */
	private void loadFromFile(String propFilePath) throws IOException {
		File propFile = new File(propFilePath);
		if (propFile.exists()) {
			log.info("SystemSetting is load from `{}`", propFile.getAbsolutePath());
			Properties prop = new Properties();
			try ( FileInputStream fis = new FileInputStream(propFile); ) {
				prop.load(fis);
				for (Object o : prop.keySet()) {
					String key = (String) o;
					put(key, prop.getProperty(key));
				}
			}
		} else {
			log.warn(MSG_FILE_NOT_EXISTS, propFile.getName());
		}
	}

	/**
	 * 从配置文件中获取内容，如果key对应的配置信息找不到，则返回<code>null</code>
	 *
	 * @param key 键
	 * @return {@link T }
	 * @author Tequila
	 * @date 2022/06/28 15:32
	 */
	public <T> T getProperty(String key) {
		String value = get(key);
		return (T) value;
	}

	/**
	 * 从配置文件中获取内容，如果key对应的配置信息找不到，则返回defaultValue
	 *
	 * @param key          键
	 * @param defaultValue 默认值
	 * @return {@link T }
	 * @author Tequila
	 * @date 2022/06/28 15:32
	 */
	@SuppressWarnings({ "unchecked" })
	public <T> T getProperty(String key, T defaultValue) {
		if (contains(key)) {
			if (defaultValue instanceof Boolean) {
				String v = get(key);
				if (INT_TRUE_VALUE.equals(v)) {
					return (T) Boolean.valueOf(true);
				} else if (INT_FALSE_VALUE.equals(v)) {
					return (T) Boolean.valueOf(false);
				} else {
					return (T) Boolean.valueOf(v);
				}
			} else if (defaultValue instanceof Integer) {
				return (T) Integer.valueOf((String) get(key));
			} else if (defaultValue instanceof Long) {
				return (T) Long.valueOf((String) get(key));
			} else {
				return (T) get(key);
			}
		} else {
			return defaultValue;
		}
	}

	/**
	 * 从系统配置文件中获取内容，如果key对应的配置信息找不到，则返回<code>null</code>
	 *
	 * @param key 键
	 * @return {@link T }
	 * @author Tequila
	 * @date 2022/06/28 15:33
	 */
	public <T> T getSysProperty(String key) {
		return (T) this.sysProp.getProperty(key);
	}

	/**
	 * 从系统配置文件中获取内容，如果key对应的配置信息找不到，则返回defaultValue
	 *
	 * @param key          键
	 * @param defaultValue 默认值
	 * @return {@link T }
	 * @author Tequila
	 * @date 2022/06/28 15:33
	 */
	public <T> T getSysProperty(String key, T defaultValue) {
		return (T) this.sysProp.getProperty(key, (String) defaultValue);
	}

	/**
	 * 获取键的数据
	 *
	 * @param key key
	 * @return {@link T }
	 * @author Tequila
	 * @date 2022/06/28 15:33
	 */
	public <T> T get(String key) {
		Object obj = settings.get(key);
		if (obj instanceof String) {
			String s = (String) obj;
			Pattern pattern = Pattern.compile(REGX, Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(s);
			while (matcher.find()) {
				String macro = matcher.group(1);
				String value = "";
				if (macros.containsKey(macro) && !settings.containsKey(macro)) {
					value = macros.get(macro);
				} else if (settings.containsKey(macro)) {
					value = (String) get(macro);
				}
				s = s.replace(matcher.group(), value);
			}

			return (T) StringEscapeUtils.unescapeHtml(s);
		} else {
			return (T) obj;
		}
	}

	/**
	 * 添加普通参数
	 *
	 * @param key   键
	 * @param value 值
	 * @author Tequila
	 * @date 2022/06/28 15:08
	 */
	public void put(String key, Object value) {
		settings.put(key, value);
	}

	/**
	 * 判断SystemSetting中是否包含key
	 *
	 * @param key key
	 * @return boolean
	 * @author Tequila
	 * @date 2022/06/28 15:08
	 */
	public boolean contains(String key) {
		return settings.containsKey(key);
	}

	/**
	 * 获取宏数据
	 *
	 * @param macro 宏
	 * @return {@link String }
	 * @author Tequila
	 * @date 2022/06/28 15:08
	 */
	public String getMacro(String macro) {
		return macros.get(macro);
	}

	/**
	 * 添加宏参数
	 *
	 * @param macro 宏
	 * @param value 值
	 * @author Tequila
	 * @date 2022/06/28 15:09
	 */
	public void putMacro(String macro, String value) {
		macros.put(macro, value);
	}

	/**
	 * 获取所有参数
	 *
	 * @return {@link Map }<{@link String }, {@link Object }>
	 * @author Tequila
	 * @date 2022/06/28 15:11
	 */
	public Map<String, Object> getSettings() {
		return settings;
	}

	/**
	 * 批量设置参数
	 *
	 * @param settings 设置
	 * @author Tequila
	 * @date 2022/06/28 15:12
	 */
	public void setSettings(Map<String, Object> settings) {
		this.settings.putAll(settings);
	}

	/**
	 * 获取所有宏参数
	 *
	 * @return {@link Map }<{@link String }, {@link String }>
	 * @author Tequila
	 * @date 2022/06/28 15:12
	 */
	public Map<String, String> getMacros() {
		return macros;
	}

	/**
	 * 批量设置宏参数
	 *
	 * @param macros 宏
	 * @author Tequila
	 * @date 2022/06/28 15:16
	 */
	public void setMacros(Map<String, String> macros) {
		this.macros.putAll(macros);
	}

	/**
	 * 返回当前是不是调试模式
	 *
	 * @return boolean
	 * @author Tequila
	 * @date 2022/06/28 15:16
	 */
	public boolean isDebugMode() {
		return isSwitchEnabled(PROP_DEBUG_KEY, false);
	}

	/**
	 * 开关变量是否开启
	 *
	 * @param switchName   开关名字
	 * @param defaultValue 默认值
	 * @return boolean
	 * @author Tequila
	 * @date 2022/06/28 15:17
	 */
	public boolean isSwitchEnabled(String switchName, boolean defaultValue) {
		Object v = settings.get(switchName);
		if (v == null) return defaultValue;
		if (v instanceof String) {
			return INT_TRUE_VALUE.equals(v) 
					|| TRUE_VALUE.equals(v);
		} else if (v instanceof Boolean) {
			return (Boolean) v;
		}
		
		return false;
	}

	/**
	 * 获取系统临时目录
	 *
	 * @return {@link String }
	 * @author Tequila
	 * @date 2022/06/28 15:17
	 */
	public String getTempDir() {
		return getSysProperty(SYS_VAR_JAVA_IO_TMPDIR);
	}

	/**
	 * 获得系统临时目录并添加后续目录
	 *
	 * @param appendDir 附加意外死亡
	 * @return {@link String }
	 * @author Tequila
	 * @date 2022/06/28 15:17
	 */
	public String getTempDir(String appendDir) {
		return String.format("%s%s", getSysProperty(SYS_VAR_JAVA_IO_TMPDIR), appendDir);
	}

	/**
	 * 获取项目目录
	 *
	 * @return {@link String }
	 * @author Tequila
	 * @date 2022/06/28 15:20
	 */
	public String getProjectDir() {
		return getProperty(SYS_VAR_PROJECT_DIR);
	}

	/**
	 * 如果是web项目，则获取项目WEB-INF路径
	 *
	 * @return {@link String }
	 * @author Tequila
	 * @date 2022/06/28 15:21
	 */
	public String getProjectWebDir() {
		return getProperty(SYS_VAR_PROJECT_WEB_DIR);
	}

	/**
	 * 获取项目的classes目录
	 *
	 * @return {@link String }
	 * @author Tequila
	 * @date 2022/06/28 15:21
	 */
	public String getProjectClassesDir() {
		return getProperty(SYS_VAR_PROJECT_CLASS_DIR);
	}

	/**
	 * 获取调试输出的文件夹，如果在setting中定义了则返回setting中定义的那个文件夹，如果没有则返回默认的java temp文件夹
	 *
	 * @return {@link String }
	 * @author Tequila
	 * @date 2022/06/28 15:21
	 */
	public String getDebugDir() {
		return getDebugDir(null);
	}

	/**
	 * 得到调试输出目录
	 *
	 * @param fileName 文件名称
	 * @return {@link String }
	 * @author Tequila
	 * @date 2022/06/28 15:23
	 */
	public String getDebugDir(String fileName) {
		String dir = getProperty(DEBUG_OUTPUT_DIR, getTempDir());
		if (!dir.endsWith(String.valueOf(File.separatorChar))) {
			dir += File.separatorChar;
		}
		if (StringUtils.isNotBlank(fileName)) dir += fileName;
		
		return dir;
	}

	/**
	 * 输出调试信息到调试文件夹中（如果不存在则输出到临时文件夹），只有在调试模式中有效
	 *
	 * @param bytes 文件内容
	 * @param ext   文件扩展名
	 * @return boolean
	 * @author Tequila
	 * @date 2022/06/28 15:24
	 */
	public boolean writeDebugFile(byte[] bytes, byte[] ext) {
		String tempFileName = String.format("%s.%s", UUID.randomUUID().toString(), new String(ext));
		return writeDebugFile(bytes, tempFileName);
	}

	/**
	 * 输出调试信息到指定文件中，只有在调试模式中有效
	 *
	 * @param bytes    文件内容
	 * @param fileName 文件名称
	 * @return boolean
	 * @author Tequila
	 * @date 2022/06/28 15:24
	 */
	public boolean writeDebugFile(byte[] bytes, String fileName) {
		if (!isDebugMode()) return true;
		String file = getDebugDir(fileName);
		log.debug("Write debug output file to `{}`", file);
		try {
			FileUtils.writeByteArrayToFile(new File(file), bytes);
			return true;
		} catch (IOException e) {
			log.warn(String.format("Can not write debug output file %s, because %s.", fileName, e.getMessage()), e);
			return false;
		}
	}

	/**
	 * 获取应用名称
	 *
	 * @return {@link String }
	 * @author Tequila
	 * @date 2022/06/28 15:25
	 */
	public String getAppName() {
		return get(PROP_APP_NAME);
	}

}
