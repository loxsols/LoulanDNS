package org.loxsols.net.service.dns.loulandns.server.impl.service.endpoint.doh;


import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class SpringContextHolder implements ApplicationContextAware {

    // 静的フィールドにコンテキストを保持する
    private static ConfigurableApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (applicationContext instanceof ConfigurableApplicationContext) {
            SpringContextHolder.context = (ConfigurableApplicationContext) applicationContext;
        }
    }

    /**
     * 任意の場所から静的に ConfigurableApplicationContext を取得するための get メソッド
     */
    public static ConfigurableApplicationContext getContext() {
        if (context == null) {
            throw new IllegalStateException("SpringContextHolder はまだ初期化されていません。");
        }
        return context;
    }

    /**
     * コンテキストから直接指定した Bean を動的に取得したい場合の便利メソッド
     */
    public static <T> T getBean(Class<T> beanClass) {
        return getContext().getBean(beanClass);
    }
}