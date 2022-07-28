/*
 * PROJECT valkyrja2
 * core/MongoBaseDAO.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.core.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * Mongodb 基础DAO对象
 *
 * @param <T>
 *
 * @author Tequila
 * @create 2022/07/01 21:46
 **/
public abstract class MongoBaseDAO<T extends AbstractDocument<?>> implements DataAccessObject<T> {

	/* ========== 常量保留字段 ========== */
	public static final String FIELD_ID = "_id";
	public static final String FIELD_CREATOR = "creator";
	public static final String FIELD_LAST_MODIFIER = "lastModifier";
	public static final String FIELD_DELETER = "deleter";
	public static final String FIELD_DELETED = "deleted";

	protected MongoTemplate mongoTemplate;

	@Autowired
	public MongoTemplate getMongoTemplate() {
		return mongoTemplate;
	}

	public void setMongoTemplate(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}
}
