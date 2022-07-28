/*
 * PROJECT valkyrja2
 * core/AuditDescription.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.component.auditor;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.valkyrja2.util.JsonObject;
import org.valkyrja2.util.StringUtils;

import java.util.Date;

/**
 * 审计数据描述
 * 
 * @author Tequila
 *
 */
public class AuditDescription implements JsonObject {

	/** 系统 */
	private String system;
	
	/** 模块 */
	private String module;
	
	/** 操作 */
	private String operate;
	
	/** 描述 */
	private String comment;
	
	/** 当前用户 */
	private String user;
	
	/** 数据 */
	private String data;

	/** 发生时间 */
	@JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private Date occurDate;
	
	/**
	 * 审计配置对象
	 */
	@JsonIgnore
	private Auditor auditor;

	public AuditDescription() {
		/* ignore */
	}
	
	public AuditDescription(String comment) {
		this.comment = comment;
	}
	
	public AuditDescription(Auditor auditor) {
		this.system = auditor.system();
		this.module = auditor.module();
		this.operate = auditor.operate();
		this.auditor = auditor;
	}
	
	/**
	 * 格式化日志输出
	 */
	public String toString() {
		return comment
				.replace("<%system%>", StringUtils.null2blank(system))
				.replace("<%module%>", StringUtils.null2blank(module))
				.replace("<%operate%>", StringUtils.null2blank(operate))
				.replace("<%user%>", StringUtils.null2blank(user))
				.replace("<%data%>", StringUtils.null2blank(data));
	}
	
	public String getSystem() {
		return system;
	}

	public void setSystem(String system) {
		this.system = system;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getOperate() {
		return operate;
	}

	public void setOperate(String operate) {
		this.operate = operate;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public Auditor getAuditor() {
		return auditor;
	}

	public void setAuditor(Auditor auditor) {
		this.auditor = auditor;
	}

	public Date getOccurDate() {
		return occurDate;
	}

	public void setOccurDate(Date occurDate) {
		this.occurDate = occurDate;
	}
}
