package com.mosect.ashadow;

import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 阴影管理器
 */
public class ShadowManager {

    private static ShadowManager defInstance;

    /**
     * 获取一个默认的阴影管理器
     *
     * @return 阴影管理器
     */
    public static ShadowManager getDefault() {
        if (null == defInstance) {
            defInstance = new ShadowManager();
        }
        return defInstance;
    }

    private List<ShadowFactory> factories;
    private HashMap<Object, Shadow> shadowMap;

    public ShadowManager() {
        factories = new CopyOnWriteArrayList<>();
        factories.add(new RoundShadowFactory());
    }

    /**
     * 绑定一个阴影（阴影）
     *
     * @param key 阴影key
     * @return 阴影对象
     * @throws UnsupportedKeyException 不支持的阴影key
     */
    public Shadow bind(@NonNull Object key) throws UnsupportedKeyException {
        if (null != shadowMap) {
            Shadow shadow = shadowMap.get(key);
            if (null != shadow && shadow.isUsed()) {
                return shadow;
            }
        }
        for (ShadowFactory factory : factories) {
            if (factory.supportKey(key)) {
                Shadow shadow = factory.create(key);
                Object copyKey = factory.copyKey(key);
                if (null == shadowMap) shadowMap = new HashMap<>();
                shadowMap.put(copyKey, shadow);
                shadow.bind();
                return shadow;
            }
        }
        throw new UnsupportedKeyException("Key:" + key);
    }

    /**
     * 解绑一个阴影
     *
     * @param shadow 阴影对象
     * @return true，解绑成功
     */
    public boolean unbind(@NonNull Shadow shadow) {
        Object key = shadow.getKey();
        if (null != key) {
            Shadow temp = shadowMap.get(key);
            if (temp == shadow) {
                shadow.unbind();
                if (!shadow.isUsed()) {
                    shadowMap.remove(key);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 添加阴影工厂
     *
     * @param factory 阴影工厂
     */
    public void addFactory(@NonNull ShadowFactory factory) {
        factories.add(factory);
    }

    /**
     * 移除阴影工厂
     *
     * @param factory 阴影工厂
     */
    public void removeFactory(@NonNull ShadowFactory factory) {
        factories.remove(factory);
    }
}
