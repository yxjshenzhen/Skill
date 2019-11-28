package com.you.common.utils.export.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Export {

    String MERGE_START_COLUMN = "startColumn";

    String MERGE_END_COLUMN = "endColumn";

    String sheetName() default "Sheet1";

    String desc() default "";

    String format() default "";

    String mapping() default "";

    //暂时只支持列合并吧，满足 left join 形态数据
    // {'startColumn':0,'endColumn':7} 合并0...7列数据
    String merge() default "";

}
