/*
 * PROJECT valkyrja2
 * util/ZipUtils.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.util;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;


/**
 * zip工具包
 *
 * @author Tequila
 * @create 2022/05/19 16:33
 **/
public class ZipUtils {

	private static final int BUFFER_SIZE = 4096;

	private ZipUtils() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * 批量压缩文件
	 *
	 * @param resFiles    待压缩文件集合
	 * @param zipFilePath 压缩文件路径
	 * @throws IOException IO错误时抛出
	 * @author Tequila
	 * @date 2022/05/19 16:35
	 */
	public static void zipFiles(Collection<File> resFiles, String zipFilePath)
			throws IOException {
		zipFiles(resFiles, zipFilePath, null);
	}

	/**
	 * 批量压缩文件
	 *
	 * @param resFiles    待压缩文件集合
	 * @param zipFilePath 压缩文件路径
	 * @param comment     压缩文件的注释
	 * @throws IOException IO错误时抛出
	 * @author Tequila
	 * @date 2022/05/19 16:36
	 */
	public static void zipFiles(Collection<File> resFiles, String zipFilePath, String comment)
			throws IOException {
		zipFiles(resFiles, new File(zipFilePath), comment);
	}

	/**
	 * 批量压缩文件
	 *
	 * @param resFiles 待压缩文件集合
	 * @param zipFile  压缩文件
	 * @throws IOException IO错误时抛出
	 * @author Tequila
	 * @date 2022/05/19 16:38
	 */
	public static void zipFiles(Collection<File> resFiles, File zipFile)
			throws IOException {
		zipFiles(resFiles, zipFile, null);
	}

	/**
	 * 批量压缩文件
	 *
	 * @param resFiles 待压缩文件集合
	 * @param zipFile  压缩文件
	 * @param comment  压缩文件的注释
	 * @throws IOException IO错误时抛出
	 * @author Tequila
	 * @date 2022/05/19 16:38
	 */
	public static void zipFiles(Collection<File> resFiles, File zipFile, String comment)
			throws IOException {
		Objects.requireNonNull(resFiles, "resFiles");
		Objects.requireNonNull(zipFile, "zipFile");

		try ( FileOutputStream fos = new FileOutputStream(zipFile);
				ZipOutputStream zos = new ZipOutputStream(fos); ) {
			for (File resFile : resFiles) {
				zipFile(resFile, "", zos, comment);
			}
		}
	}

	/**
	 * 压缩文件
	 *
	 * @param resFilePath 待压缩文件路径
	 * @param zipFilePath 压缩文件路径
	 * @throws IOException IO错误时抛出
	 * @author Tequila
	 * @date 2022/05/19 16:40
	 */
	public static void zipFile(String resFilePath, String zipFilePath)
			throws IOException {
		zipFile(resFilePath, zipFilePath, null);
	}

	/**
	 * 压缩文件
	 *
	 * @param resFilePath 待压缩文件路径
	 * @param zipFilePath 压缩文件路径
	 * @param comment     压缩文件的注释
	 * @throws IOException IO错误时抛出
	 * @author Tequila
	 * @date 2022/05/19 16:40
	 */
	public static void zipFile(String resFilePath, String zipFilePath, String comment)
			throws IOException {
		zipFile(new File(resFilePath), new File(zipFilePath), comment);
	}

	/**
	 * 压缩文件
	 *
	 * @param resFile 待压缩文件
	 * @param zipFile 压缩文件
	 * @throws IOException IO错误时抛出
	 * @author Tequila
	 * @date 2022/05/19 16:42
	 */
	public static void zipFile(File resFile, File zipFile)
			throws IOException {
		zipFile(resFile, zipFile, null);
	}

	/**
	 * 压缩文件
	 *
	 * @param resFile 待压缩文件
	 * @param zipFile 压缩文件
	 * @param comment 压缩文件的注释
	 * @throws IOException IO错误时抛出
	 * @author Tequila
	 * @date 2022/05/19 16:43
	 */
	public static void zipFile(File resFile, File zipFile, String comment)
			throws IOException {
		Objects.requireNonNull(resFile, "resFile");
		Objects.requireNonNull(zipFile, "zipFile");

		try ( FileOutputStream fos = new FileOutputStream(zipFile);
			  ZipOutputStream zos = new ZipOutputStream(fos); ) {
			zipFile(resFile, "", zos, comment);
		}
	}

	/**
	 * 压缩文件
	 *
	 * @param resFile  待压缩文件
	 * @param rootPath 相对于压缩文件的路径
	 * @param zos      压缩文件输出流
	 * @param comment  压缩文件的注释
	 * @throws IOException IO错误时抛出
	 * @author Tequila
	 * @date 2022/05/19 17:09
	 */
	private static void zipFile(File resFile, String rootPath, ZipOutputStream zos, String comment)
			throws IOException {
		String curPath = rootPath + (StringUtils.isBlank(rootPath) ? "" : File.separator) + resFile.getName();
		if (resFile.isDirectory()) {
			File[] fileList = resFile.listFiles();
			if (fileList == null || fileList.length <= 0) {
				ZipEntry entry = new ZipEntry(curPath + '/');
				if (StringUtils.isNotBlank(comment)) entry.setComment(comment);
				zos.putNextEntry(entry);
				zos.closeEntry();
			} else {
				for (File file: fileList) {
					// 如果递归返回false则返回false
					zipFile(file, curPath, zos, comment);
				}
			}
		} else {
			try ( FileInputStream fis = new FileInputStream(resFile);
				  InputStream is =  new BufferedInputStream(fis); ) {
				ZipEntry entry = new ZipEntry(curPath);
				if (StringUtils.isNotBlank(comment)) entry.setComment(comment);
				zos.putNextEntry(entry);
				byte[] buffer = new byte[BUFFER_SIZE];
				int len;
				while ((len = is.read(buffer, 0, BUFFER_SIZE)) != -1) {
					zos.write(buffer, 0, len);
				}
				zos.closeEntry();
			}
		}
	}

	/**
	 * 解压文件
	 *
	 * @param zipFilePath 待解压文件路径
	 * @param destDirPath 目标目录路径
	 * @return boolean
	 * @throws IOException IO错误时抛出
	 * @author Tequila
	 * @date 2022/05/19 18:01
	 */
	public static List<File> unzipFile(String zipFilePath, String destDirPath)
			throws IOException {
		return unzipFileByKeyword(new File(zipFilePath), Paths.get(destDirPath), true, null);
	}

	/**
	 * 解压文件
	 *
	 * @param zipFile   待解压文件
	 * @param destDir   目标目录
	 * @param overwrite 覆盖
	 * @return boolean
	 * @throws IOException IO错误时抛出
	 * @author Tequila
	 * @date 2022/05/19 18:25
	 */
	public static List<File> unzipFile(File zipFile, Path destDir, boolean overwrite)
			throws IOException {
		return unzipFileByKeyword(zipFile, destDir, overwrite, null);
	}

	/**
	 * 解压带有关键字的文件
	 *
	 * @param zipFilePath 待解压文件路径
	 * @param destDirPath 目标目录路径
	 * @param overwrite   覆盖
	 * @param keyword     关键字
	 * @return {@link List }<{@link File }>
	 * @throws IOException IO错误时抛出
	 * @author Tequila
	 * @date 2022/05/19 18:25
	 */
	public static List<File> unzipFileByKeyword(String zipFilePath, String destDirPath, boolean overwrite, String keyword)
			throws IOException {
		return unzipFileByKeyword(new File(zipFilePath), Paths.get(destDirPath), overwrite, keyword);
	}

	/**
	 * 解压带有关键字的文件
	 *
	 * @param zipFile   待解压文件
	 * @param destDir   目标目录
	 * @param overwrite 覆盖
	 * @param keyword   关键字
	 * @return {@link List }<{@link File }>
	 * @throws IOException IO错误时抛出
	 * @author Tequila
	 * @date 2022/05/19 18:18
	 */
	public static List<File> unzipFileByKeyword(File zipFile, Path destDir, boolean overwrite, String keyword)
			throws IOException {
		if (Files.exists(destDir) && !overwrite)
			throw new FileAlreadyExistsException(String.format("Destination folder `%s` exists.", destDir));

		try (ZipFile zf = new ZipFile(zipFile);) {
			List<File> files = new ArrayList<>();
			Enumeration<? extends ZipEntry> entries = zf.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				String entryName = entry.getName();
				if (StringUtils.isBlank(keyword)  //  如果没有关键字匹配
						|| keyword.equals(entryName)  // 如果直接匹配成功
						|| Pattern.matches(keyword, entryName)) {  // 或者正则表达式匹配成功
					String filePath = destDir + File.separator + entryName;
					File file = new File(filePath);
					files.add(file);
					if (entry.isDirectory()) {
						Files.createDirectories(file.toPath());
					} else {
						try (InputStream in = new BufferedInputStream(zf.getInputStream(entry));
							 FileOutputStream fos = new FileOutputStream(file);
							 OutputStream out = new BufferedOutputStream(fos);) {
							byte buffer[] = new byte[BUFFER_SIZE];
							int len;
							while ((len = in.read(buffer)) != -1) {
								out.write(buffer, 0, len);
							}
						}
					}
				}
			}
			return files;
		}
	}

	/**
	 * 获取压缩文件中的文件路径链表
	 *
	 * @param zipFilePath 压缩文件路径
	 * @return 压缩文件中的文件路径链表
	 * @throws IOException IO错误时抛出
	 */
	public static List<String> getFilesPath(String zipFilePath)
			throws IOException {
		return getFilesPath(new File(zipFilePath));
	}

	/**
	 * 获取压缩文件中的文件路径链表
	 *
	 * @param zipFile 压缩文件
	 * @return 压缩文件中的文件路径链表
	 * @throws IOException IO错误时抛出
	 */
	public static List<String> getFilesPath(File zipFile)
			throws IOException {
		Objects.requireNonNull(zipFile, "zipFile");

		List<String> paths = new ArrayList<>();
		Enumeration<?> entries = getEntries(zipFile);
		while (entries.hasMoreElements()) {
			paths.add(((ZipEntry) entries.nextElement()).getName());
		}
		return paths;
	}

	/**
	 * 获取压缩文件中的注释链表
	 *
	 * @param zipFilePath 压缩文件路径
	 * @return 压缩文件中的注释链表
	 * @throws IOException IO错误时抛出
	 */
	public static List<String> getComments(String zipFilePath)
			throws IOException {
		return getComments(new File(zipFilePath));
	}

	/**
	 * 获取压缩文件中的注释链表
	 *
	 * @param zipFile 压缩文件
	 * @return 压缩文件中的注释链表
	 * @throws IOException IO错误时抛出
	 */
	public static List<String> getComments(File zipFile)
			throws IOException {
		Objects.requireNonNull(zipFile, "zipFile");

		List<String> comments = new ArrayList<>();
		Enumeration<?> entries = getEntries(zipFile);
		while (entries.hasMoreElements()) {
			ZipEntry entry = ((ZipEntry) entries.nextElement());
			comments.add(entry.getComment());
		}
		return comments;
	}

	/**
	 * 获取压缩文件中的文件对象
	 *
	 * @param zipFilePath 压缩文件路径
	 * @return 压缩文件中的文件对象
	 * @throws IOException IO错误时抛出
	 */
	public static Enumeration<? extends ZipEntry> getEntries(String zipFilePath)
			throws IOException {
		return getEntries(new File(zipFilePath));
	}

	/**
	 * 获取压缩文件中的文件对象
	 *
	 * @param zipFile 压缩文件
	 * @return 压缩文件中的文件对象
	 * @throws IOException IO错误时抛出
	 */
	public static Enumeration<? extends ZipEntry> getEntries(File zipFile)
			throws IOException {
		Objects.requireNonNull(zipFile, "zipFile");

		return new ZipFile(zipFile).entries();
	}
}
