package org.valkyrja2.component.api;

import org.apache.shiro.authz.Permission;

import java.util.HashSet;
import java.util.Set;

/**
 * 简单AccessKey
 *
 * @author Tequila
 * @create 2022/08/02 00:28
 **/
public class SimplyAccessKey implements AccessKey {

    private String id;

    private String appCode;

    private String signKey;

    private boolean encrypt;

    private String encryptKey;

    private EncryptMode encryptMode;

    private Set<String> permissions;

    private boolean available;

    public SimplyAccessKey() {
        super();
        this.available = true;
    }

    public SimplyAccessKey(String id, String appCode, String signKey) {
        this.id = id;
        this.appCode = appCode;
        this.signKey = signKey;
        this.encrypt = false;
        this.permissions = new HashSet<>();
        this.available = true;
    }

    public SimplyAccessKey(String id, String appCode, String signKey, boolean encrypt, String encryptKey, EncryptMode encryptMode, Set<String> permissions, boolean available) {
        this.id = id;
        this.appCode = appCode;
        this.signKey = signKey;
        this.encrypt = encrypt;
        this.encryptKey = encryptKey;
        this.encryptMode = encryptMode;
        this.permissions = permissions;
        this.available = available;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getAppCode() {
        return appCode;
    }

    public void setAppCode(String appCode) {
        this.appCode = appCode;
    }

    @Override
    public String getSignKey() {
        return signKey;
    }

    public void setSignKey(String signKey) {
        this.signKey = signKey;
    }

    @Override
    public boolean isEncrypt() {
        return encrypt;
    }

    public void setEncrypt(boolean encrypt) {
        this.encrypt = encrypt;
    }

    @Override
    public String getEncryptKey() {
        return encryptKey;
    }

    public void setEncryptKey(String encryptKey) {
        this.encryptKey = encryptKey;
    }

    @Override
    public EncryptMode getEncryptMode() {
        return encryptMode;
    }

    public void setEncryptMode(EncryptMode encryptMode) {
        this.encryptMode = encryptMode;
    }

    @Override
    public Set<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions;
    }

    @Override
    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
