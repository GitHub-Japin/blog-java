package com.Blog.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    /**
     * 插入数据时填充
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {//插入自动填充
        log.info("公共字段自动填充[insert]");
        log.info(metaObject.toString());
        long id=Thread.currentThread().getId();
        log.info("线程id:{}",id);
        // 先获取到deleted的值，再进行判断，如果为空，就进行填充，如果不为空，就不做处理
        Object deleted = getFieldValByName("deleted", metaObject);
        if (null == deleted) {
            setFieldValByName("deleted", "0", metaObject);
        }
    }

    /**
     * 更新数据时填充
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {//更新自动填充
        log.info("公共字段自动填充[update]");
        log.info(metaObject.toString());
        long id=Thread.currentThread().getId();
        log.info("线程id:{}",id);
    }
}
