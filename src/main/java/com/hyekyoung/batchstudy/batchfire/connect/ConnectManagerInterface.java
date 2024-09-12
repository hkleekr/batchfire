package com.hyekyoung.batchstudy.batchfire.connect;

/**
 * ConnectManager 강제 구성 사항 Interface
 * @param <T>
 */
public interface ConnectManagerInterface<T> {
    T connect(String systemName) throws Exception;
    void closeConnect(T t) throws Exception;
}
