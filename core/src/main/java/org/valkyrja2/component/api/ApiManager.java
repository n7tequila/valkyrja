package org.valkyrja2.component.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * api管理对象
 *
 * @author Tequila
 * @create 2022/08/01 21:52
 **/
public class ApiManager {

	private static final Logger log = LoggerFactory.getLogger(ApiManager.class);

	private static volatile ApiManager _instance;
    private ApiManager() { /* nothing */ }
    public static ApiManager getInstance() {
    	if (_instance == null) {
    		synchronized (ApiManager.class) {
    			if (_instance == null) {
    				_instance = new ApiManager();
    			}
    		}
    	}
    	return _instance;
    }

	/** 匿名appCode */
	public static final String ANONYMOUS_APP_CODE = "*";

	/** AccessKey repository */
	private AccessKeyRepository accessKeyRepo;

	/** api user map */
	private Map<String, AccessKey> accessKeyMap = new ConcurrentHashMap<>();

    public void init() {

    }

	/**
	 * 获取appCode对应的AccessKey对象
	 *
	 * @param appCode 应用程序代码
	 * @return {@link AccessKey }
	 * @author Tequila
	 * @date 2022/08/01 23:55
	 */
	public AccessKey get(String appCode) {
		AccessKey accessKey;
		// 如果appCode指向的AccessKey找不到，则返回匿名AccessKey(如果存在的话）
		if (contains(appCode)) {
			accessKey = accessKeyMap.get(appCode);
		} else {
			accessKey = accessKeyMap.get(ANONYMOUS_APP_CODE);
		}

		if (accessKey != null && accessKey.isAvailable()) {
			return accessKey;
		} else {
			return null;
		}
    }

	/**
	 * 得到匿名appCode
	 *
	 * @return {@link AccessKey }
	 * @author Tequila
	 * @date 2022/08/02 00:22
	 */
	public AccessKey getAnonymous() {
		return accessKeyMap.get(ANONYMOUS_APP_CODE);
	}

	/**
	 * appCode是否存在
	 *
	 * @param appCode 应用程序代码
	 * @return boolean
	 * @author Tequila
	 * @date 2022/08/01 23:58
	 */
	public boolean contains(String appCode) {
		return accessKeyMap.containsKey(appCode);
	}

	/**
	 * 清空
	 *
	 * @author Tequila
	 * @date 2022/08/02 00:45
	 */
	public void clear() {
		accessKeyMap.clear();
	}

	public AccessKeyRepository getAccessKeyRepo() {
		return accessKeyRepo;
	}

	public void setAccessKeyRepo(AccessKeyRepository accessKeyRepo) {
		this.accessKeyRepo = accessKeyRepo;
	}

	public Map<String, AccessKey> getAccessKeyMap() {
		return accessKeyMap;
	}

	public void setAccessKeyMap(Map<String, AccessKey> accessKeyMap) {
		this.accessKeyMap.putAll(accessKeyMap);
	}
}
