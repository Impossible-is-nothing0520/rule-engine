package com.example.rule.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

/**
 * 规则执行上下文
 */
@Data
@Accessors(chain = true)
public class RuleContext {
    /**
     * 规则输入参数
     */
    private Map<String, Object> params = new HashMap<>();

    /**
     * 规则执行结果
     */
    private Object result;

    /**
     * 添加参数
     */
    public RuleContext addParam(String key, Object value) {
        params.put(key, value);
        return this;
    }

    /**
     * 获取参数
     */
    public Object getParam(String key) {
        return params.get(key);
    }

    /**
     * 清空上下文
     */
    public void clear() {
        params.clear();
        result = null;
    }
} 