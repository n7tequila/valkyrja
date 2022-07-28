/*
 * PROJECT valkyrja2
 * util/ImageUtils.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.util;

import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 图像工具包
 *
 * @author Tequila
 * @create 2022/06/28 17:11
 **/
public class ImageUtils {
	private static final Logger log = LoggerFactory.getLogger(ImageUtils.class);

	private static final Font DEFAULT_FONT = new Font("宋体", Font.PLAIN, 12);
	
	private static final Color DEFAULT_COLOR = Color.BLACK;
	
	public static final String BASE64_IMAGE_REGX = "^data:image/(.{1,30});base64,(.*)";

	public static final String BASE64_IMAGE_MARK = ";base64,";

	/* ========== 图片扩展名常量 ========== */
	public static final String IMAGE_TYPE_PNG = "png";
	public static final String IMAGE_TYPE_JPG = "jpg";
	public static final String IMAGE_TYPE_JPEG = "jpeg";
	public static final String IMAGE_TYPE_GIF = "gif";
	public static final String IMAGE_TYPE_BMP = "bmp";
	public static final String IMAGE_TYPE_SVG = "svg";
	public static final String IMAGE_TYPE_ICON = "ico";

	private ImageUtils() {
		throw new IllegalStateException("Utility class");
	}


	/**
	 * 判断字符串是否是base64格式
	 *
	 * @param imgStr 需处理的图片字符串
	 * @return boolean
	 * @author Tequila
	 * @date 2022/06/28 17:25
	 */
	public static boolean isBase64Image(String imgStr) {
		return Pattern.matches(BASE64_IMAGE_REGX, imgStr);
	}

	/**
	 * 解析base64图片文件的格式
	 *
	 * @param imgBase64   需处理的图片base64
	 * @param defaultType 默认类型
	 * @return {@link String }
	 * @author Tequila
	 * @date 2022/06/28 17:33
	 */
	public static String extractImageType(String imgBase64, String defaultType) {
		Pattern pattern = Pattern.compile(BASE64_IMAGE_REGX);
		Matcher match = pattern.matcher(imgBase64.length() > 50 ? imgBase64.substring(0, 50) : imgBase64);
		if (match.find()) {
			return match.group(1);
		} else {
			return defaultType;
		}
	}

	/**
	 * 提取图像
	 *
	 * @param imgBase64 需处理的图片base64
	 * @return {@link String } 图片base64字符串
	 * @author Tequila
	 * @date 2022/06/28 20:54
	 */
	public static String extractImage(String imgBase64) {
		Pattern pattern = Pattern.compile(BASE64_IMAGE_REGX);
		Matcher match = pattern.matcher(imgBase64);
		if (match.find()) {
			return match.group(2);
		} else {
			return imgBase64;
		}
	}

	/**
	 * 提取图像字节
	 *
	 * @param imgBase64 需处理的图片base64
	 * @return {@link byte[] } 图片字节数组
	 * @author Tequila
	 * @date 2022/06/28 20:54
	 */
	public static byte[] extractImageBytes(String imgBase64) {
		String imgStr = extractImage(imgBase64);
		return Base64.decodeBase64(imgStr);
	}

	/**
	 * 控制图片质量，压缩图片大小。默认在不改变长宽的基础上，使用80%的压缩比率
	 *
	 * @param img 需要处理的图片
	 * @return {@link byte[] }
	 * @throws IOException IO异常
	 * @author Tequila
	 * @date 2022/06/28 17:27
	 */
	public static byte[] compressImageSize(byte[] img) throws IOException {
		return compressImageSize(img, 1f, 0.8f);
	}

	/**
	 * 控制图片质量，压缩图片大小
	 *
	 * @param img     需要处理的图片
	 * @param scale   缩放百分比
	 * @param quality 质量
	 * @return {@link byte[] }
	 * @throws IOException IO异常
	 * @author Tequila
	 * @date 2022/06/28 17:27
	 */
	public static byte[] compressImageSize(byte[] img, float scale, float quality) throws IOException {
		InputStream input = new ByteArrayInputStream(img);
		ByteArrayOutputStream out = new ByteArrayOutputStream() ;
		Thumbnails.of(input).scale(scale).outputQuality(quality).toOutputStream(out);
		return out.toByteArray();
	}

	/**
	 * 控制图片质量，压缩图片大小
	 *
	 * @param img     需要处理的图片
	 * @param width   宽度
	 * @param height  高度
	 * @param quality 质量
	 * @return {@link byte[] }
	 * @throws IOException IO异常
	 * @author Tequila
	 * @date 2022/06/28 17:30
	 */
	public static byte[] compressImageSize(byte[] img, int width, int height, float quality) throws IOException {
		if (width == 0 || height == 0) {
			ImageInfo imageInfo = getImageInfo(img);
			if (width == 0) {
				imageInfo.scaleByHeight(height);
			} else {  // height == 0
				imageInfo.scaleByWidth(width);
			}
			width = imageInfo.getWidth();
			height = imageInfo.getHeight();
		}

		InputStream input = new ByteArrayInputStream(img);
		ByteArrayOutputStream out = new ByteArrayOutputStream() ;
		Thumbnails.of(input).size(width, height).outputQuality(quality).toOutputStream(out);
		return out.toByteArray();
	}

	/**
	 * 压缩图像大小
	 *
	 * @param img     需要处理的图片
	 * @param maxEdge 长边的长度
	 * @param quality 质量
	 * @return {@link byte[] }
	 * @throws IOException IO异常
	 * @author Tequila
	 * @date 2022/06/28 17:31
	 */
	public static byte[] compressImageSize(byte[] img, int maxEdge, float quality) throws IOException {
		ImageInfo imageInfo = getImageInfo(img);
		int width = imageInfo.getWidth();
		int height = imageInfo.getHeight();

		if (width <= maxEdge && height <= maxEdge) return img;   // 如果长宽边都小于maxEdge，则不进行压缩

		if (maxEdge >= 0) {
			if (imageInfo.getWidth() > imageInfo.getHeight()) {
				imageInfo.scaleByWidth(maxEdge);
			} else {
				imageInfo.scaleByHeight(maxEdge);
			}

			width = imageInfo.getWidth();
			height = imageInfo.getHeight();
		}

		InputStream input = new ByteArrayInputStream(img);
		ByteArrayOutputStream out = new ByteArrayOutputStream() ;
		Thumbnails.of(input).size(width, height).outputQuality(quality).toOutputStream(out);
		return out.toByteArray();
	}

	/**
	 * 控制图片质量，压缩图片大小
	 *
	 * @param file 需要处理的图片文件
	 * @return {@link byte[] }
	 * @throws IOException IO异常
	 * @author Tequila
	 * @date 2022/06/28 17:31
	 */
	public static byte[] compressImageSize(File file) throws IOException {
		return compressImageSize(file, 1f, 0.8f);
	}

	/**
	 * 控制图片质量，压缩图片大小
	 *
	 * @param file    需要处理的图片文件
	 * @param scale   缩放百分比
	 * @param quality 质量
	 * @return {@link byte[] }
	 * @throws IOException IO异常
	 * @author Tequila
	 * @date 2022/06/28 17:32
	 */
	public static byte[] compressImageSize(File file, float scale, float quality) throws IOException {
		return compressImageSize(FileUtils.readFileToByteArray(file), scale, quality);
	}

	/**
	 * 控制图片尺寸，压缩图片大小
	 *
	 * @param file    需要处理的图片文件
	 * @param width   宽度
	 * @param height  高度
	 * @param quality 质量
	 * @return {@link byte[] }
	 * @throws IOException IO异常
	 * @author Tequila
	 * @date 2022/06/28 17:32
	 */
	public static byte[] compressImageSize(File file, int width, int height, float quality) throws IOException {
		return compressImageSize(FileUtils.readFileToByteArray(file), width, height, quality);
	}

	/**
	 * 根据指定大小压缩图片
	 *
	 * @param imageBytes 源图片字节数组
	 * @param desFileSize 指定图片大小，单位kb
	 * @return 压缩质量后的图片字节数组
	 * @throws IOException
	 */
	public static byte[] compressImage(byte[] imageBytes, long desFileSize) throws IOException {
		if (imageBytes == null || imageBytes.length <= 0 || imageBytes.length < desFileSize * 1024) {
			return imageBytes;
		}
		long srcSize = imageBytes.length;
		double accuracy = getAccuracy(srcSize / 1024);
		while (imageBytes.length > desFileSize * 1024) {
			ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream(imageBytes.length);
			Thumbnails.of(inputStream)
					.scale(accuracy)
					.outputQuality(accuracy)
					.toOutputStream(outputStream);
			imageBytes = outputStream.toByteArray();
		}

		return imageBytes;
	}

	/**
	 * 自动调节精度(经验数值)
	 *
	 * @param size 源图片大小
	 * @return 图片压缩质量比
	 */
	private static double getAccuracy(long size) {
		double accuracy;
		if (size < 900) {
			accuracy = 0.85;
		} else if (size < 2047) {
			accuracy = 0.6;
		} else if (size < 3275) {
			accuracy = 0.44;
		} else {
			accuracy = 0.4;
		}
		return accuracy;
	}

	/**
	 * 绘制一张透明的文字图片
	 *
	 * @param str    字符串
	 * @param font   字体
	 * @param color  颜色
	 * @param width  宽度
	 * @param height 高度
	 * @return {@link BufferedImage }
	 * @author Tequila
	 * @date 2022/06/28 17:13
	 */
	public static BufferedImage drawTransparentStringImage(String str, Font font, Color color, int width, int height) {
        BufferedImage buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);  
        Graphics2D gd = buffImg.createGraphics();  
        //设置透明 start  
        buffImg = gd.getDeviceConfiguration().createCompatibleImage(width, height, Transparency.TRANSLUCENT);  
        gd = buffImg.createGraphics();  
        //设置透明 end  
        gd.setFont(font); //设置字体  
        gd.setColor(color); //设置颜色  
        gd.drawString(str, width / 2 - font.getSize() * str.length() / 2, font.getSize()); //输出文字（中文横向居中）
        
        return buffImg;
	}

	/**
	 * 绘制一张透明的文字图片
	 *
	 * @param str    字符串
	 * @param font   字体
	 * @param color  颜色
	 * @param width  宽度
	 * @param height 高度
	 * @return {@link byte[] }
	 * @throws IOException IO异常
	 * @author Tequila
	 * @date 2022/06/28 17:15
	 */
	public static byte[] drawTransparentStringImageBytes(String str, Font font, Color color, int width, int height) throws IOException {
        BufferedImage buffImg = drawTransparentStringImage(str, font, color, width, height);
		try (ByteArrayOutputStream os = new ByteArrayOutputStream();) {
			ImageIO.write(buffImg, IMAGE_TYPE_PNG, os);
			return os.toByteArray();
		}
	}

	/**
	 * 画透明字符串图像
	 *
	 * @param str    字符串
	 * @param width  宽度
	 * @param height 高度
	 * @return {@link BufferedImage }
	 * @author Tequila
	 * @date 2022/06/28 17:15
	 */
	public static BufferedImage drawTransparentStringImage(String str, int width, int height) {
        return drawTransparentStringImage(str, DEFAULT_FONT, DEFAULT_COLOR, width, height);
	}

	/**
	 * 画透明字符串图像
	 *
	 * @param str    字符串
	 * @param width  宽度
	 * @param height 高度
	 * @return {@link byte[] }
	 * @throws IOException IO异常
	 * @author Tequila
	 * @date 2022/06/28 17:16
	 */
	public static byte[] drawTransparentStringImageBytes(String str, int width, int height) throws IOException {
		return drawTransparentStringImageBytes(str, DEFAULT_FONT, DEFAULT_COLOR, width, height);
	}


	/**
	 * 让图片透明化
	 *
	 * @param imgSrc 源图byte[]
	 * @return {@link byte[] }
	 * @author Tequila
	 * @date 2022/06/28 17:25
	 */
	public static byte[] transferAlpha2Byte(byte[] imgSrc) throws IOException {
		try (InputStream is = new ByteArrayInputStream(imgSrc);
			 ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();) {

			BufferedImage image = ImageIO.read(is);
			ImageIcon imageIcon = new ImageIcon(image);
			BufferedImage bufferedImage = new BufferedImage(imageIcon.getIconWidth(), imageIcon.getIconHeight(),
					BufferedImage.TYPE_4BYTE_ABGR);
			Graphics2D g2D = (Graphics2D) bufferedImage.getGraphics();
			g2D.drawImage(imageIcon.getImage(), 0, 0, imageIcon.getImageObserver());
			int alpha = 0;
			for (int j1 = bufferedImage.getMinY(); j1 < bufferedImage.getHeight(); j1++) {
				for (int j2 = bufferedImage.getMinX(); j2 < bufferedImage.getWidth(); j2++) {
					int rgb = bufferedImage.getRGB(j2, j1);

					int r = (rgb & 0xff0000) >> 16;
					int g = (rgb & 0xff00) >> 8;
					int b = (rgb & 0xff);
					if (((255 - r) < 30) && ((255 - g) < 30) && ((255 - b) < 30)) {
						rgb = ((alpha + 1) << 24) | (rgb & 0x00ffffff);
					}

					bufferedImage.setRGB(j2, j1, rgb);

				}
			}

			g2D.drawImage(bufferedImage, 0, 0, imageIcon.getImageObserver());

			ImageIO.write(bufferedImage, IMAGE_TYPE_PNG, byteArrayOutputStream); // 转换成byte数组
			return byteArrayOutputStream.toByteArray();
		}
	}

	/**
	 * 获取图片信息
	 *
	 * @param file 图片文件
	 * @return {@link ImageInfo }
	 * @throws IOException IO异常
	 * @author Tequila
	 * @date 2022/06/28 20:47
	 */
	public static ImageInfo getImageInfo(File file) throws IOException {
    	byte[] bytes = FileUtils.readFileToByteArray(file);
    	return getImageInfo(bytes);
    }

	/**
	 * 获取图片信息
	 *
	 * @param image 处理的图片
	 * @return {@link ImageInfo }
	 * @throws IOException IO异常
	 * @author Tequila
	 * @date 2022/06/28 20:46
	 */
	public static ImageInfo getImageInfo(byte[] image) throws IOException {
    	try ( InputStream io = new ByteArrayInputStream(image);	) {
	    	BufferedImage img = ImageIO.read(io);
	    	ImageInfo imageInfo = new ImageInfo();
	    	imageInfo.setWidth(img.getWidth());
	    	imageInfo.setHeight(img.getHeight());
	    	
	    	return imageInfo;
    	}
    }
    
 	/**
	 * 图片文件信息
	 *
	 * @author Tequila
	 * @create 2022/06/28 20:47
	 **/
	public static class ImageInfo {
    	
    	/** 默认长宽都是0的图片信息对象 */
    	public static final ImageInfo ZERO = new ImageInfo(0, 0);

    	/**
    	 * 宽度，如果调用过拉伸函数，则返回拉伸后的宽度
    	 */
		private int width;
    	
		/**
		 * 高度，如果滴啊用过拉伸函数，则返回拉伸后的高度
		 */
    	private int height;
    	
    	/** 原始宽度 */
    	private int originWidth;
    	
    	/** 原始高度 */
    	private int originHeight;
    	
    	public ImageInfo() {
    		// nothings
    	}

		public ImageInfo(int width, int height) {
			this.width = width;
			this.height = height;
			this.originWidth = width;
			this.originHeight = height;
		}
		
		/**
		 * 按比例拉伸长宽
		 * 
		 * @param scale 拉伸比例
		 */
		public void scale(float scale) {
			this.width *= scale;
			this.height *= scale;
		}

		/**
		 * 使用宽度拉伸尺寸
		 *
		 * @param width 宽度
		 * @author Tequila
		 * @date 2022/06/28 20:47
		 */
		public void scaleByWidth(int width) {
			float scale = (float) width / this.width;
			this.width = width;
			this.height = (int) (this.height * scale);
		}

		/**
		 * 使用高度拉伸尺寸
		 *
		 * @param height 高度
		 * @author Tequila
		 * @date 2022/06/28 20:48
		 */
		public void scaleByHeight(int height) {
			float scale = (float) height / this.height;
			this.height = height;
			this.width = (int) (this.width * scale);
		}

		public int getWidth() {
			return width;
		}

		public void setWidth(int width) {
			this.width = width;
		}

		public int getHeight() {
			return height;
		}

		public void setHeight(int height) {
			this.height = height;
		}

		public int getOriginWidth() {
			return originWidth;
		}

		public void setOriginWidth(int originWidth) {
			this.originWidth = originWidth;
		}

		public int getOriginHeight() {
			return originHeight;
		}

		public void setOriginHeight(int originHeight) {
			this.originHeight = originHeight;
		}
    }

	/**
	 * 文字定位位置对象
	 *
	 * @author Tequila
	 * @create 2022/06/28 21:12
	 **/
	public static class TextPosition {
    	
		private int x;
		
		private int y;
		
		private int width;
		
		private int height;
		
		private boolean centerX = true;
		
		private boolean centerY = true;
		
		public TextPosition() {
			/* every position is auto */
		}
		
		public TextPosition(int width, int height) {
			this.width = width;
			this.height = height;
		}
		
		public TextPosition(int x, int y, int width, int height) {
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
			this.centerX = false;
			this.centerY = false;
		}

		int getX() {
			return x;
		}

		void setX(int x) {
			this.x = x;
		}

		int getY() {
			return y;
		}

		void setY(int y) {
			this.y = y;
		}

		int getWidth() {
			return width;
		}

		void setWidth(int width) {
			this.width = width;
		}

		int getHeight() {
			return height;
		}

		void setHeight(int height) {
			this.height = height;
		}

		boolean isCenterX() {
			return centerX;
		}

		void setCenterX(boolean centerX) {
			this.centerX = centerX;
		}

		boolean isCenterY() {
			return centerY;
		}

		void setCenterY(boolean centerY) {
			this.centerY = centerY;
		}
	}

	/** 字体名字 */
	private static final String[] fontNames = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
	/** 排除字体名称 */
	private static final String[] excludeFontNames = {"Waseem", "Webdings", "Wingdings", "Wingdings 2", "Wingdings 3"};

	private static Random random;

	/* 初始化随机数对象 */
	static {
		try {
			random = SecureRandom.getInstanceStrong();
		} catch (NoSuchAlgorithmException e) {
			// nothing to do
		}
	}

	/**
	 * 画出验证码图片
	 *
	 * @param verifyCode   文本
	 * @param width  宽度
	 * @param height 高度
	 * @return {@link byte[] }
	 * @author Tequila
	 * @date 2022/06/28 21:14
	 */
	public static byte[] drawVerifyCodeImage(String verifyCode, int width, int height) {
        /* 创建图片缓冲区 */
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
 
        Graphics2D g = bi.createGraphics();
 
        /* 设置背景颜色 */
        g.setBackground(new Color(225,233,221));
        g.clearRect(0, 0, width, height);
 
        /* 这里只画入四个字符 */
        for (int i = 0; i < verifyCode.length(); i++) {
        	String str = String.valueOf(verifyCode.charAt(i));
			g.setFont(randomFont(height));  // 设置字体，随机
			g.setColor(randomColor());  // 设置颜色，随机

            float x = i * 1.0f * width / verifyCode.length();    // 定义字符的x坐标
            float y = randomInt(g.getFont().getSize(), height);  // 定义字符的y坐标
			log.trace("{}. font={}, size={}, x={}, y={}", i, g.getFont().getName(), g.getFont().getSize(), x, y);
			g.drawString(str, x, y);
        }
       
        /* 定义干扰线的数量（3-5条） */
        int num = random.nextInt(5) % 3 + 3;
        Graphics2D graphics = (Graphics2D) bi.getGraphics();
        for (int i = 0; i < num; i++) {
            int x1 = random.nextInt(width);
            int y1 = random.nextInt(height);
            int x2 = random.nextInt(width);
            int y2 = random.nextInt(height);
            graphics.setColor(randomColor());
            graphics.drawLine(x1, y1, x2, y2);
        }
        // 释放图形上下文
        g.dispose();
        
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            ImageIO.write(bi, "png", output);
            
            return output.toByteArray();
        } catch (IOException e) {
			return new byte[0];
		}
    }

	/**
	 * 随机字体
	 *
	 * @return {@link Font }
	 * @author Tequila
	 * @date 2022/06/28 21:16
	 */
    private static Font randomFont(int height) {
        int index = random.nextInt(fontNames.length);
        String fontName;
		do {
			fontName = fontNames[index];
		} while (ArrayUtils.contains(excludeFontNames, fontName));  // 排除那些表情字体
        int style = random.nextInt(3);  // 随机获取3种字体的样式
        int size = randomInt(15, height - 3);  // 随机获取字体的大小 15 - 图片高度
        return new Font(fontName, style, size);
    }

	/**
	 * 随机颜色
	 *
	 * @return {@link Color }
	 * @author Tequila
	 * @date 2022/06/28 21:16
	 */
    private static Color randomColor() {
        int r = random.nextInt(225);
        int g = random.nextInt(225);
        int b = random.nextInt(225);
        return new Color(r, g, b);
    }

	/**
	 * 随机整数
	 * <code>int num = random.nextInt(max) % (max - min + 1) + min;</code>
	 *
	 * @param min 最小值
	 * @param max 马克斯
	 * @return int
	 * @author Tequila
	 * @date 2022/06/28 21:46
	 */
	private static int randomInt(int min, int max) {
		return random.nextInt(max) % (max - min + 1) + min;
	}
}
