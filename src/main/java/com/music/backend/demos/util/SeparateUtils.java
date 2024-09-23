package com.music.backend.demos.util;

import org.audiveris.proxymusic.*;
import org.audiveris.proxymusic.util.Marshalling;

import java.io.*;
import java.lang.String;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author rty
 * @version 1.0
 * @description: TODO
 * @date 2024/8/30 21:21
 */
public class SeparateUtils {
    // Generated factory for all proxymusic elements
    private static final ObjectFactory factory = new ObjectFactory();

    private static final Integer MEASURE_COUNT = 4;

    /** Temporary area. */
    private static final File TEMP_DIR = new File("src/main/resources/temp");

    /*
     * @description: 按照 MEASURE_COUNT 将小节拆分
     * @param: measures 小节列表
     * @return: java.util.List<java.util.List<org.audiveris.proxymusic.ScorePartwise.Part.Measure>> 拆分后的包含小节列表的列表
     * @author: rty
     * @date: 2024/8/30 21:16
     */
    public static List<List<ScorePartwise.Part.Measure>> separateMeasureList(List<ScorePartwise.Part.Measure> measures){
        return new ArrayList<>(IntStream.range(0, measures.size())
                .boxed()
                .collect(Collectors.groupingBy(
                        index -> index / MEASURE_COUNT, // 分组键，每四个元素为一组
                        LinkedHashMap::new, // 使用 LinkedHashMap 保持顺序
                        Collectors.mapping(measures::get, Collectors.toList())
                )).values());
    }

    /*
     * @description:  将XML用修改后的PartList和Part进行序列化
     * @param: scorePartwise 源ScorePartwise，即需要拆分的XML转化的ScorePartwise对象
               outputPath 输出文件路径
               subPartList 修改后的PartList部分
               part 修改后的Part部分
     * @return: void
     * @author: rty
     * @date: 2024/8/30 20:54
     */
    public static void marshalFormScorePartwise(ScorePartwise scorePartwise, String outputPath, PartList subPartList, ScorePartwise.Part part){
        ScorePartwise temp = factory.createScorePartwise();
        copyScorePartwise(scorePartwise, temp);
        temp.setPartList(subPartList);
        temp.getPart().add(part);
        marshal(outputPath, temp);
    }

    /*
     * @description: 如果一个part有两个staff，则拆分为左右手
     * @param: measures 小节列表
     * @return: java.util.List<java.util.List<org.audiveris.proxymusic.ScorePartwise.Part.Measure>> 含有左手小节列表和右手小节列表的List
     * @author: rty
     * @date: 2024/8/25 22:17
     */
    public static List<List<ScorePartwise.Part.Measure>> separateStaffList(List<ScorePartwise.Part.Measure> measures){
        List<ScorePartwise.Part.Measure> staff1 = new ArrayList<>();
        List<ScorePartwise.Part.Measure> staff2 = new ArrayList<>();

        /* 遍历小节列表 */
        for (ScorePartwise.Part.Measure measure: measures) {
            /* 将一个小节拆分为左右手 */
            List<ScorePartwise.Part.Measure> temp = separateStaff(measure);
            staff1.add(temp.get(0));
            staff2.add(temp.get(1));
        }

        return Arrays.asList(staff1, staff2);
    }

    /*
     * @description:  从 Measure 中的 Attributes 获取拆分为左手和右手后的 Attributes
     * @param: measure  需要拆分的 Measure 对象
               clefNumber clef序号，1为上，2为下
     * @return: org.audiveris.proxymusic.Attributes
     * @author: rty
     * @date: 2024/8/25 22:01
     */
    public static Attributes getSeparateAttributes(ScorePartwise.Part.Measure measure, int clefNumber) {
        Attributes copy = factory.createAttributes();
        for (Object object:measure.getNoteOrBackupOrForward()) {
            if (object instanceof Attributes attributes) {
                if (attributes.getStaves() == null) return attributes;

                /* 如果有两个五线谱再进行处理 */
                if (attributes.getStaves().intValue() == 2) {
                    Clef clef = attributes.getClef().get(clefNumber - 1);
                    copy.setStaves(null);
                    copy.getClef().clear();
                    copy.getClef().add(clef);
                }
            }
        }
        return copy;
    }

    /*
     * @description: 将scorePartwise对象写入xml文件
     * @param: xmlPath 写入xml文件的路径
               scorePartwise scorePartwise对象
     * @return: void
     * @author: rty
     * @date: 2024/8/23 0:19
     */
    public static void marshal(String xmlPath,ScorePartwise scorePartwise){
        /* 校验ScorePartwise是否合法 */
        //ValidateUtils.validateXML(scorePartwise);
        File xmlFile = new File(TEMP_DIR, xmlPath);
        try (OutputStream os = new FileOutputStream(xmlFile)){
            Marshalling.marshal(scorePartwise, os ,false,2);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Marshalling.MarshallingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * @description: 将src的属性复制到dst
     * @param: src ScorePartwise 源对象
               dst ScorePartwise 目标对象
     * @return: void
     * @author: rty
     * @date: 2024/8/23 0:19
     */
    public static void copyScorePartwise(ScorePartwise src,ScorePartwise dst){
        dst.setWork(src.getWork());
        dst.setVersion(src.getVersion());
        /* 不复制PartList是因为拆分一定会改变PartList */
        //dst.setPartList(src.getPartList());
        dst.setMovementTitle(src.getMovementTitle());
        dst.setMovementNumber(src.getMovementNumber());
        dst.setIdentification(src.getIdentification());
        dst.setDefaults(src.getDefaults());
    }

    /*
     * @description: 将xml文件解析为对象
     * @param: xmlPath xml文件路径
     * @return: java.lang.Object
     * @author: rty
     * @date: 2024/8/24 9:38
     */
    public static Object unmarshal(String xmlPath) {
        Object object = null;
        try {
            InputStream inputStream = new FileInputStream(xmlPath);
            object = Marshalling.unmarshal(inputStream);
        } catch (Marshalling.UnmarshallingException | FileNotFoundException e) {
            e.printStackTrace();
        }

        return object;
    }

    /*
     * @description: 从 PartList标签部分，获取所有ScorePart标签
     * @param: partList PartList标签部分
     * @return: java.util.List<java.lang.Object>
     * @author: rty
     * @date: 2024/8/26 0:00
     */
    public static List<Object> getScorePartList(PartList partList){
        return partList.getPartGroupOrScorePart().stream().filter(item -> item instanceof ScorePart).collect(Collectors.toList());
    }

    /*
     * @description: 将一个小节拆分为左右手部分,必须有两个staff才能拆分
     * @param: measure 小节
     * @return: java.util.List<org.audiveris.proxymusic.ScorePartwise.Part.Measure> 含有左右手的小节列表
     * @author: rty
     * @date: 2024/8/25 22:21
     */
    public static List<ScorePartwise.Part.Measure> separateStaff(ScorePartwise.Part.Measure measure){
        List<ScorePartwise.Part.Measure> result = new ArrayList<>();

        /* 获取第一行五线谱的attributes */
        Attributes attributes1 = getSeparateAttributes(measure,1);

        /* 获取第二行五线谱的attributes */
        Attributes attributes2 = getSeparateAttributes(measure,2);

        /* 获取小节的所有音符 */
        List<Object> notes = measure.getNoteOrBackupOrForward().stream().filter(ClassifyUtils::isMeasureStaffPart).toList();

        /* 获取公共部分信息 */
        List<Object> common = measure.getNoteOrBackupOrForward().stream().filter(ClassifyUtils::isMeasureCommonPart).toList();

        /* 当小节没有音符时，直接返回 */
        if (notes.size() == 0) {
            result.add(measure);
            return result;
        }
        // 当有音符时，按照staff进行拆分
        List<Object> staff1 = notes.stream().filter(item -> {
            try {
                Field staffField = item.getClass().getDeclaredField("staff");
                staffField.setAccessible(true);
                BigInteger staff = (BigInteger) staffField.get(item);
                return staff.equals(BigInteger.ONE);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
                return false;
            }
        }).toList();
        List<Object> staff2 = notes.stream().filter(item -> {
            try {
                Field staffField = item.getClass().getDeclaredField("staff");
                staffField.setAccessible(true);
                BigInteger staff = (BigInteger) staffField.get(item);
                return staff.equals(BigInteger.TWO);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
                return false;
            }
        }).toList();

        if (staff1.size() != 0){
            ScorePartwise.Part.Measure measure1 = factory.createScorePartwisePartMeasure();

            /* 对attributes标签进行处理，只保留一个staff */
            List<Object> temp = common.stream().filter(item -> {
                return !(item instanceof Attributes);
            }).toList();
            measure1.getNoteOrBackupOrForward().clear();
            measure1.getNoteOrBackupOrForward().addAll(temp);
            measure1.getNoteOrBackupOrForward().addAll(staff1);
            measure1.getNoteOrBackupOrForward().add(1,attributes1);
            measure1.setNumber(measure.getNumber());
            measure1.setText(measure.getText());
            measure1.setWidth(measure.getWidth());
            measure1.setId(measure.getId());
            measure1.setImplicit(measure.getImplicit());
            measure1.setNonControlling(measure.getNonControlling());
            result.add(measure1);
        }

        if (staff2.size() != 0){
            ScorePartwise.Part.Measure measure2 = factory.createScorePartwisePartMeasure();

            /* 对attributes标签进行处理，只保留一个staff */
            List<Object> temp = common.stream().filter(item -> {
                return !(item instanceof Attributes);
            }).toList();
            measure2.getNoteOrBackupOrForward().clear();
            measure2.getNoteOrBackupOrForward().addAll(temp);
            measure2.getNoteOrBackupOrForward().addAll(staff2);
            measure2.getNoteOrBackupOrForward().add(1,attributes2);
            measure2.setNumber(measure.getNumber());
            measure2.setText(measure.getText());
            measure2.setWidth(measure.getWidth());
            measure2.setId(measure.getId());
            measure2.setImplicit(measure.getImplicit());
            measure2.setNonControlling(measure.getNonControlling());
            result.add(measure2);
        }
        return result;
    }
}
