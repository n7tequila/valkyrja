/*
 * PROJECT valkyrja2
 * util/IDStructure.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.util;

import java.net.IDN;

/**
 * 身份证结构
 *
 * @author Tequila
 * @create 2022/06/28 16:48
 **/
public class IDStructure {

	/** 身份证 */
	private String idno;

	/** 身份证是否校验通过 */
	private boolean valid;

	/** 性别 */
	private String sex;

	/** 性别中文 */
	private String sexCN;

	/** 生日 */
	private String birthday;

	/** 生日中文 */
	private String birthdayCN;

	private IDStructure(String id) {
		this.idno = id;
		validate();
		calc();
	}

	public static IDStructure forID(String id) {
		return new IDStructure(id);
	}

	public void validate() {
		this.valid = IDNoUtils.isValid(idno);
	}

	/**
	 * 计算身份证号信息
	 *
	 * @author Tequila
	 * @date 2022/06/28 16:50
	 */
	public void calc() {
		char sex;
		String birthday;
		if (idno.length() == 18) {
			sex = idno.charAt(16);
			birthday = idno.substring(6, 14);
		} else {
			sex = idno.charAt(14);
			birthday = idno.substring(6, 12);
			birthday = "19" + birthday;
		}

		if (Integer.valueOf(sex) % 2 == 0) {
			this.sex = "F";
			this.sexCN = "女";
		} else {
			this.sex = "M";
			this.sexCN = "男";
		}

		this.birthday = birthday.substring(0, 4) + "-" + birthday.substring(4, 6) + "-" + birthday.substring(6, 8);
		this.birthdayCN = birthday.substring(0, 4) + "年" + birthday.substring(4, 6) + "月" + birthday.substring(6, 8)
		        + "日";
	}

	public String getIdno() {
		return idno;
	}

	public void setIdno(String idno) {
		this.idno = idno;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getSexCN() {
		return sexCN;
	}

	public void setSexCN(String sexCN) {
		this.sexCN = sexCN;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getBirthdayCN() {
		return birthdayCN;
	}

	public void setBirthdayCN(String birthdayCN) {
		this.birthdayCN = birthdayCN;
	}

	public boolean isValid() {
		return valid;
	}

}
