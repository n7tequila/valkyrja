/*
 * PROJECT valkyrja2
 * core/Operator.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.core.db;

/**
 * 数据库操作描述对象<br>
 * 描述了每一条数据具体的Create, Modify, Delete的基本信息。
 * 
 * @author Tequila
 *
 */
public class Operator implements java.io.Serializable {

	private static final long serialVersionUID = -443798886433430653L;

	private static final String USER_SYSTEM = "SYSTEM";
	private static final String USER_UNKNOWN = "UNKNOWN";

	/** 常量 系统用户 */
	public static final Operator SYSTEM = new Operator(USER_SYSTEM, USER_SYSTEM);

	/** 常量 位置用户 */
	public static final Operator UNKNOWN = new Operator(USER_UNKNOWN, USER_UNKNOWN);
	
	/** 操作用户ID */
	private String operateUserId;
	
	/** 操作用户名 */
	private String operateUser;
	
	/** 操作时间 */
	private long operateDate;
	
	public Operator() {
		// nothing
	}
	
	public Operator(String id, String user) {
		this.operateUserId = id;
		this.operateUser = user;
		this.operateDate = System.currentTimeMillis();
	}
		
	public String getOperateUserId() {
		return operateUserId;
	}

	public void setOperateUserId(String operateUserId) {
		this.operateUserId = operateUserId;
	}

	public String getOperateUser() {
		return operateUser;
	}

	public void setOperateUser(String operateUser) {
		this.operateUser = operateUser;
	}

	public long getOperateDate() {
		return operateDate;
	}

	public void setOperateDate(long operateDate) {
		this.operateDate = operateDate;
	}
}
