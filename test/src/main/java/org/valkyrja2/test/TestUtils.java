/*
 * PROJECT valkyrja2
 * test/TestUtils.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.test;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.util.StringUtils;
import org.valkyrja2.test.exception.TestRuntimeException;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Random;

/**
 * 测试工具包
 *
 * @author Tequila
 * @create 2022/06/28 14:42
 **/
public class TestUtils {

	private TestUtils() {
		throw new IllegalStateException("Utility class");
	}

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
	 * 随机真实姓名
	 *
	 * @return {@link String } 生成的姓名
	 * @author Tequila
	 * @date 2022/06/28 14:44
	 */
	public static String randomRealName() {
		String[] surname = {"赵", "钱", "孙", "李", "周", "吴", "郑", "王", "冯", "陈", "褚", "卫", "蒋", "沈", "韩", "杨", "朱", "秦",
				"尤", "许", "何", "吕", "施", "张", "孔", "曹", "严", "华", "金", "魏", "陶", "姜", "戚", "谢", "邹", "喻", "柏", "水", "窦",
				"章", "云", "苏", "潘", "葛", "奚", "范", "彭", "郎", "鲁", "韦", "昌", "马", "苗", "凤", "花", "方", "俞", "任", "袁", "柳",
				"酆", "鲍", "史", "唐", "费", "廉", "岑", "薛", "雷", "贺", "倪", "汤", "滕", "殷", "罗", "毕", "郝", "邬", "安", "常", "乐",
				"于", "时", "傅", "皮", "卞", "齐", "康", "伍", "余", "元", "卜", "顾", "孟", "平", "黄", "和", "穆", "萧", "尹", "姚", "邵",
				"湛", "汪", "祁", "毛", "禹", "狄", "米", "贝", "明", "臧", "计", "伏", "成", "戴", "谈", "宋", "茅", "庞", "熊", "纪", "舒",
				"屈", "项", "祝", "董", "梁", "杜", "阮", "蓝", "闵", "席", "季"};
		String girl = "秀娟英华慧巧美娜静淑惠珠翠雅芝玉萍红娥玲芬芳燕彩春菊兰凤洁梅琳素云莲真环雪荣爱妹霞香月莺媛艳瑞凡佳嘉琼勤珍贞莉桂娣叶璧璐娅琦晶妍茜秋珊莎锦黛青倩婷姣婉娴瑾颖露瑶怡婵雁蓓纨仪荷丹蓉眉君琴蕊薇菁梦岚苑婕馨瑗琰韵融园艺咏卿聪澜纯毓悦昭冰爽琬茗羽希宁欣飘育滢馥筠柔竹霭凝晓欢霄枫芸菲寒伊亚宜可姬舒影荔枝思丽";
		String boy = "伟刚勇毅俊峰强军平保东文辉力明永健世广志义兴良海山仁波宁贵福生龙元全国胜学祥才发武新利清飞彬富顺信子杰涛昌成康星光天达安岩中茂进林有坚和彪博诚先敬震振壮会思群豪心邦承乐绍功松善厚庆磊民友裕河哲江超浩亮政谦亨奇固之轮翰朗伯宏言若鸣朋斌梁栋维启克伦翔旭鹏泽晨辰士以建家致树炎德行时泰盛雄琛钧冠策腾楠榕风航弘";
		int index = random.nextInt(surname.length - 1);
		String name = surname[index]; // 获得一个随机的姓氏
		int i = random.nextInt(3);// 可以根据这个数设置产生的男女比例
		if (i == 2) {
			int j = random.nextInt(girl.length() - 2);
			if (j % 2 == 0) {
				name = name + girl.substring(j, j + 2);
			} else {
				name = name + girl.substring(j, j + 1);
			}
		} else {
			int j = random.nextInt(boy.length() - 2);
			if (j % 2 == 0) {
				name = name + boy.substring(j, j + 2);
			} else {
				name = name + boy.substring(j, j + 1);
			}
		}

		return name;
	}

	/**
	 * 随机身份证号
	 *
	 * @return {@link String } 生成的身份证号
	 * @author Tequila
	 * @date 2022/06/28 14:45
	 */
	public static String randomIDNo() {
		String id = "";
		// 随机生成省、自治区、直辖市代码 1-2
		String[] provinces = {"11", "12", "13", "14", "15", "21", "22", "23", "31", "32", "33", "34", "35", "36", "37",
				"41", "42", "43", "44", "45", "46", "50", "51", "52", "53", "54", "61", "62", "63", "64", "65", "71",
				"81", "82"};
		String province = provinces[random.nextInt(provinces.length - 1)];
		// 随机生成地级市、盟、自治州代码 3-4
		String[] cities = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "21", "22", "23", "24", "25",
				"26", "27", "28"};
		String city = cities[random.nextInt(cities.length - 1)];
		// 随机生成县、县级市、区代码 5-6
		String[] counties = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "21", "22", "23", "24", "25",
				"26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38"};
		String county = counties[random.nextInt(counties.length - 1)];
		// 随机生成出生年月 7-14
		SimpleDateFormat dft = new SimpleDateFormat("yyyyMMdd");
		Date beginDate = new Date();
		Calendar date = Calendar.getInstance();
		date.setTime(beginDate);
		date.set(Calendar.DATE, date.get(Calendar.DATE) - random.nextInt(365 * 100));
		String birth = dft.format(date.getTime());
		// 随机生成顺序号 15-17
		String no = random.nextInt(999) + "";
		// 随机生成校验码 18
		String checks[] = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "X"};
		String check = checks[random.nextInt(checks.length - 1)];
		// 拼接身份证号码
		id = province + city + county + birth + no + check;

		return id;
	}

	/**
	 * 随机手机号
	 *
	 * @return {@link String } 生成的手机号
	 * @author Tequila
	 * @date 2022/06/28 14:45
	 */
	public static String randomMobileNo() {
		String[] phonePrefix = "134,135,136,137,138,139,150,151,152,157,158,159,130,131,132,155,156,133,153".split(",");
		int index = random.nextInt(phonePrefix.length);
		String first = phonePrefix[index];
		String second = String.valueOf(random.nextInt(888) + 10000).substring(1);
		String third = String.valueOf(random.nextInt(9100) + 10000).substring(1);
		return first + second + third;
	}

	/**
	 * 新建一个http请求
	 *
	 * @param method      方法
	 * @param url         url
	 * @param params      参数个数
	 * @param contentType 内容类型
	 * @param content     内容
	 * @param form        表单
	 * @param header      头
	 * @return {@link MockHttpServletRequest }
	 * @author Tequila
	 * @date 2022/06/30 21:33
	 */
	public static MockHttpServletRequest newHttpRequest(String method, String url, String contextPath, Map<String, String> params, String contentType, byte[] content, Map<String, String> form, Map<String, String> header) {
		URI uri;
		try {
			uri = new URI(url);
		} catch (URISyntaxException e) {
			throw new TestRuntimeException(String.format( "Can not parse url `%s`", url), e);
		}
		MockHttpServletRequest request = new MockHttpServletRequest(method, url);
		request.setScheme(uri.getScheme());
		request.setServerName(uri.getHost());
		if (uri.getPort() != -1) {  // 如果port是-1，则表示默认端口，不设置
			request.setServerPort(uri.getPort());
		}
		if (contextPath != null && !"".equals(contextPath.trim())) {
			request.setContextPath(contextPath);
		}
		request.setRequestURI(uri.getPath());
		request.setQueryString(uri.getQuery());

		if (params != null) {
			request.setParameters(params);
		}

		if (header != null) {
			header.forEach(request::addHeader);
		}

		return request;
	}

	/**
	 * 新建一个get http请求
	 *
	 * @param url     url
	 * @param headers 头
	 * @return {@link MockHttpServletRequest }
	 * @author Tequila
	 * @date 2022/06/30 21:36
	 */
	public static MockHttpServletRequest newHttpRequestGet(String url, String contextPath, Map<String, String> headers) {
		return newHttpRequest("GET", url, contextPath,null, null, null, null, headers);
	}
}
