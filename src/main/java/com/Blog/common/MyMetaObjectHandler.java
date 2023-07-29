package com.Blog.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    /**
     * 插入数据时填充
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        // 先获取到password的值，再进行判断，如果为空，就进行填充，如果不为空，就不做处理
        Object deleted = getFieldValByName("deleted", metaObject);
        if(null == deleted){
           setFieldValByName("deleted", "0", metaObject);
        }
    }

    /**
     * 更新数据时填充
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {

    }
}
