package com.music.backend.demos.util;

import org.audiveris.proxymusic.*;

import java.util.Arrays;

/**
 * @author rty
 * @version 1.0
 * @description: MusicXML 标签分类工具类
 * @date 2024/8/25 10:19
 */
public class ClassifyUtils {

    /** Measure的common部分定义 */
    private static final Class<?>[] MEASURE_COMMON = {Backup.class, Attributes.class, FiguredBass.class, Print.class, Sound.class, Listening.class,Barline.class, Grouping.class, Link.class, Bookmark.class};

    /*
     * @description: 判断标签是否属于Measure的common部分
     * @param: measure
     * @return: boolean
     * @author: rty
     * @date: 2024/8/25 10:37
     */
    public static boolean isMeasureCommonPart(Object measure){
        return Arrays.stream(MEASURE_COMMON).anyMatch(clazz -> clazz.isInstance(measure));
    }

    /*
     * @description: 判断标签是否属于Measure的有属性staff的部分
     * @param: measure
     * @return: boolean
     * @author: rty
     * @date: 2024/8/25 10:37
     */
    public static boolean isMeasureStaffPart(Object measure){
        return Arrays.stream(MEASURE_COMMON).noneMatch(clazz -> clazz.isInstance(measure));
    }
}
