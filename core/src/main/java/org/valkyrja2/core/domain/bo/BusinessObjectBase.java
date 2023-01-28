/*
 * PROJECT valkyrja2
 * core/BusinessObjectBase.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.core.domain.bo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.CrudRepository;
import org.valkyrja2.core.db.AbstractCMDDocument;
import org.valkyrja2.core.db.AbstractCMDocument;
import org.valkyrja2.core.db.AbstractEntity;
import org.valkyrja2.core.db.Operator;
import org.valkyrja2.exception.BizRuntimeException;
import org.valkyrja2.mvc.ResponseCode;
import org.valkyrja2.mvc.spring.SpringUtils;
import org.valkyrja2.util.ClassUtils;

import java.io.Serializable;

import java.util.Collection;


/**
 * BO对象基础类
 * @param <T>
 * @param <ID>
 * @param <REPO>
 *
 * @author Tequila
 * @create 2022/07/01 21:45
 **/
public abstract class BusinessObjectBase<T extends AbstractEntity<ID>, ID extends Serializable, REPO extends CrudRepository<T, ID>> extends AbstractBusinessObject<T, ID, REPO> {
	
	private static final Logger log = LoggerFactory.getLogger(BusinessObjectBase.class);
	
	/** bo对象定义 */
	private BusinessObjectDefine boDefine;

	/**
	 * 构造一个BO对象，并且初始化对应的Repository对象
	 *
	 * @author Tequila
	 * @date 2022/07/01 21:48
	 */
	protected BusinessObjectBase() {
		super();

		/* 初始化BusinessObjectDefine */
		initDefine();

		/* 执行自定义初始化 */
		handleInit();
	}

	/**
	 * 根据数据Id构造一个BO对象，并根据ID查询是否在数据库中有相应的数据，如果没有则抛出BizRuntimeException.OBJECT_NOT_FOUND异常
	 *
	 * @param id id
	 * @author Tequila
	 * @date 2022/07/01 21:48
	 */
	protected BusinessObjectBase(ID id) {
		this(id, false,true);
	}

	/**
	 * 根据数据Id构造一个BO对象，并根据ID查询是否在数据库中有相应的数据<br>
	 * 并根据notFoundRaiseError参数判断是否在没有找到数据时抛出BizRuntimeException.OBJECT_NOT_FOUND异常<br>
	 *
	 * @param id                 id
	 * @param notFoundRaiseError 没有找到提高错误
	 * @author Tequila
	 * @date 2022/07/04 11:32
	 */
	protected BusinessObjectBase(ID id, boolean notFoundRaiseError) {
		this(id, false, notFoundRaiseError);
	}

	/**
	 * 根据数据Id构造一个BO对象，并根据ID查询是否在数据库中有相应的数据，<br>
	 * 并根据notFoundRaiseError参数判断是否在没有找到数据时抛出BizRuntimeException.OBJECT_NOT_FOUND异常<br>
	 * super() -> initDefine() -> 载入数据 -> init()
	 *
	 * @param id                 id
	 * @param allowDeleted       允许载入删除标记的数据
	 * @param notFoundRaiseError 没有找到提高错误
	 * @author Tequila
	 * @date 2022/07/04 11:28
	 */
	protected BusinessObjectBase(ID id, boolean allowDeleted, boolean notFoundRaiseError) {
		super();

		findById(id, allowDeleted, notFoundRaiseError);

		initDefine();
		handleInit();
	}

	/**
	 * 根据数据对象构造一个BO对象，不进行任何检查操作。
	 *
	 * @param entity 实体
	 * @author Tequila
	 * @date 2022/07/04 11:03
	 */
	protected BusinessObjectBase(T entity) {
		super(entity);

		initDefine();
		handleInit();
	}

	/**
	 * 根据数据对象和Repository对象狗在一个BO对象
	 *
	 * @param entity 实体
	 * @param repo   repository
	 * @author Tequila
	 * @date 2022/07/04 11:34
	 */
	protected BusinessObjectBase(T entity, REPO repo) {
		super(entity, repo);

		initDefine();
		handleInit();
	}

	/**
	 * 初始化
	 *
	 * @author Tequila
	 * @date 2022/07/13 18:20
	 */
	public void init() {
		// nothing will init
	}

	/**
	 * 初始化BO对象所引用的所有对象
	 *
	 * @author Tequila
	 * @date 2022/07/13 19:32
	 */
	private void initDefine() {
		if (getBODefine().isInit() && repo != null) {
			Class<REPO> repoClass = ClassUtils.getSuperClassGenericType(this.getClass(), CrudRepository.class);
			if (repoClass == null && getBODefine().isRaiseError()) {
				throw new IllegalArgumentException(String.format("Class %s <REPO> generic type is not define", this.getClass().toString()));
			}
			if (repoClass != null) {
				try {
					this.repo = SpringUtils.getBean(repoClass);
				} catch (RuntimeException e) {
					log.warn("{} create REPO {}, raise error", this.getClass().getName(), repoClass, e);
					handleThrow(e);
				}
			}
		}
	}

	/**
	 * 处理初始化操作
	 *
	 * @author Tequila
	 * @date 2022/07/04 11:40
	 */
	private void handleInit() {
		if (getBODefine().isInit()) {
			try {
				init();
			} catch (RuntimeException e) {
				log.warn("{} execute init() raise error", this.getClass().getName(), e);
				handleThrow(e);
			}
		}
	}

	/**
	 * 处理throws事件
	 *
	 * @param e 需要抛出的错误
	 * @author Tequila
	 * @date 2022/07/13 19:36
	 */
	private void handleThrow(RuntimeException e) {
		if (getBODefine().isRaiseError()) {
			throw e;
		}
	}

	/**
	 * 通过id查找数据
	 *
	 * @param id                 id
	 * @param allowDeleted       允许载入删除数据
	 * @param notFoundRaiseError 没有找到数据时是否抛出错误
	 * @return {@link T }
	 * @author Tequila
	 * @date 2022/09/14 14:39
	 */
	public T findById(ID id, boolean allowDeleted, boolean notFoundRaiseError) {
		if (notFoundRaiseError) {
			this.entity = repo.findById(id).orElseThrow(() -> new BizRuntimeException(ResponseCode.OBJECT_NOT_FOUND));
		} else {
			this.entity = repo.findById(id).orElse(null);
		}
		/* 如果允许载入删除数据，则跳过删除检查，否则进行删除标记检查 */
		if (!allowDeleted
				&& this.entity instanceof AbstractCMDDocument<?>
				&& ((AbstractCMDDocument<?>) this.entity).getDeleted()) {
			if (notFoundRaiseError) {
				throw new BizRuntimeException(ResponseCode.OBJECT_NOT_FOUND);
			} else {
				this.entity = null;
			}
		}

		return this.entity;
	}

	/**
	 * 通过id查找数据
	 *
	 * @param id id
	 * @return {@link T }
	 * @author Tequila
	 * @date 2022/09/14 14:42
	 */
	public T findById(ID id) {
		return findById(id, false, true);
	}

	/**
	 * BO对象中的entity对象是否为空，忽略删除标记
	 *
	 * @return boolean
	 * @author Tequila
	 * @date 2022/03/01 23:59
	 */
	public boolean isNull() {
		return (this.entity == null);
	}

	/**
	 * BO对象中的entity对象是否为空，并且如果是有删除标记，则删除标记不能为空
	 *
	 * @return boolean
	 * @author Tequila
	 * @date 2022/03/01 23:59
	 */
	public boolean isExists() {
		if (this.entity instanceof AbstractCMDDocument) {
			return (!((AbstractCMDDocument<?>) this.entity).getDeleted()); 
		} else {
			return (this.entity != null);
		}
	}

	/**
	 * 获取当前操作用户信息，用于记录到审计对象中
	 *
	 * @return {@link Operator }
	 * @author Tequila
	 * @date 2022/07/13 19:41
	 */
	protected Operator getCurrentOperator() {
		return Operator.UNKNOWN;
	}

	/**
	 * 更新审计字段
	 * 
	 * @param entity entity
	 * @param onlyModifyField 仅更新modify字段
	 */
	@SuppressWarnings("unchecked")
	private void updateAuditField(T entity, boolean onlyModifyField) {
		if (entity instanceof AbstractCMDocument) {
			Operator operator = getCurrentOperator();

			AbstractCMDocument<ID> locEntity = (AbstractCMDocument<ID>) entity;
			if (locEntity.getCreator() == null
					|| (locEntity.getCreator() == null && !onlyModifyField)) {
				locEntity.setCreator(operator);
			}
			locEntity.setLastModifier(operator);
		}
	}

	/**
	 * 保存对象，并同时保存依赖对象
	 *
	 * @author Tequila
	 * @date 2022/07/04 09:29
	 */
	@Override
	public void save() {
		save(this.entity, true);
	}

	/**
	 * 根据传入的对象保存实例，但不保存依赖对象
	 *
	 * @param entity 实体
	 * @author Tequila
	 * @date 2022/07/04 09:29
	 */
	public void save(T entity) {
		save(entity, false);
	}
	
	/**
	 * 保存一个实例，并根据withDependency参数选择是否保存依赖对象。<br>
	 * 注意：依赖对象将先于实体对象保存
	 * 
	 * @param entity 实例对象
	 * @param withDependency 是否保存依赖对象
	 */
	public void save(T entity, boolean withDependency) {
		if (entity != null) {
			updateAuditField(entity, false);
			repo.save(entity);
		}
	}

	/**
	 * 保存一批对象，但不保存依赖对象
	 *
	 * @param entities 实体
	 * @author Tequila
	 * @date 2022/07/04 09:58
	 */
	public void saveAll(Collection<T> entities) {
		entities.forEach(entity -> updateAuditField(entity, false));
		
		repo.saveAll(entities);
	}

	/**
	 * 删除对象，同时删除依赖对象
	 *
	 * @author Tequila
	 * @date 2022/07/04 10:29
	 */
	@SuppressWarnings("unchecked")
	public void delete() {
		if (entity instanceof AbstractCMDDocument) {
			Operator operator = getCurrentOperator();

			AbstractCMDDocument<ID> locEntity = (AbstractCMDDocument<ID>) entity;
			if (!locEntity.getDeleted()) {
				locEntity.setDeleted(true);
				locEntity.setDeleter(operator);
			}
			repo.save(entity);
		} else {
			repo.delete(entity);
		}
	}

	/**
	 * 获取BusinessObject的Annotation配置信息
	 *
	 * @return {@link BusinessObjectDefine }
	 * @author Tequila
	 * @date 2022/07/01 22:46
	 */
	protected BusinessObjectDefine getBODefine() {
		if (this.boDefine == null) {
			BusinessObject annotation = this.getClass().getAnnotation(BusinessObject.class);
			if (annotation == null) {
				this.boDefine = new BusinessObjectDefine();
			} else {
				this.boDefine = new BusinessObjectDefine(annotation);
			}
		}
		return this.boDefine;
	}

	/**
	 * 业务对象定义
	 *
	 * @author Tequila
	 * @create 2022/07/01 22:43
	 **/
	private static class BusinessObjectDefine {

		/** 创建repository */
		private final boolean createRepo;

		/** 创建dao */
		private final boolean createDAO;

		/** 当初始化报错的时候是否抛出错误 */
		private final boolean raiseError;

		/** 初始化 */
		private final boolean init;

		public BusinessObjectDefine() {
			this.createRepo = true;
			this.createDAO = true;
			this.raiseError = false;
			this.init = true;
		}

		public BusinessObjectDefine(BusinessObject annotation) {
			this.createRepo = annotation.repo();
			this.createDAO = annotation.dao();
			this.raiseError = annotation.raiseError();
			this.init = annotation.init();
		}

		public boolean isCreateRepo() {
			return createRepo;
		}

		public boolean isCreateDAO() {
			return createDAO;
		}

		public boolean isRaiseError() {
			return raiseError;
		}

		public boolean isInit() {
			return init;
		}
	}
}
