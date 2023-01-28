package org.valkyrja2.component.async;

/**
 * 存储池状态
 *
 * @author Tequila
 * @create 2022/08/04 17:43
 **/
public class StoragePoolStatus {

    /** 服务器名称 */
    private String serverName;

    /** 存储池大小 */
    private Long storagePoolSize;

    public StoragePoolStatus() {
        super();
    }

    public StoragePoolStatus(String serverName, Long storagePoolSize) {
        this.serverName = serverName;
        this.storagePoolSize = storagePoolSize;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public Long getStoragePoolSize() {
        return storagePoolSize;
    }

    public void setStoragePoolSize(Long storagePoolSize) {
        this.storagePoolSize = storagePoolSize;
    }
}
