package com.music.backend.demos.util;

import org.audiveris.proxymusic.*;
import org.audiveris.proxymusic.util.Marshalling;

import java.io.*;
import java.lang.String;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author rty
 * @version 1.0
 * @description: XML工具类
 * @date 2024/8/12 22:32
 */
public class XMLUtils {
    /** Temporary area. */
    private static final File TEMP_DIR = new File("src/main/resources/temp");

    /** Name of the temporary XML file. */
    private static final String FILE_NAME = "hello-world-test.xml";

    // Generated factory for all proxymusic elements
    private static ObjectFactory factory = new ObjectFactory();

    private static Integer MEASURE_COUNT = 4;

    /*
     * @description: 将 MusicXML文件，按照 Part进行拆分
     * @param: xmlPath MusicXML文件路径
     * @return: void
     * @author: rty
     * @date: 2024/8/26 0:00
     */
    public static void separateXMLByPart(String xmlPath){
        ScorePartwise scorePartwise = (ScorePartwise) SeparateUtils.unmarshal(xmlPath);
        /** Part标签部分 */
        List<ScorePartwise.Part> parts = scorePartwise.getPart();

        /** PartList标签部分 */
        PartList partList = scorePartwise.getPartList();

        int partCount = partList.getPartGroupOrScorePart().size();

        for (int i = 0; i < partCount; i++) {
            PartList subPartList = factory.createPartList();
            subPartList.getPartGroupOrScorePart().add(partList.getPartGroupOrScorePart().get(i));
            ScorePartwise.Part part = parts.get(i);
            String tempFileName = "part" + i + ".xml";
            SeparateUtils.marshalFormScorePartwise(scorePartwise,tempFileName,subPartList,part);
        }
    }

    /*
     * @description: 将 MusicXML文件，按照 Measure进行拆分,如果有左右手则进一步拆分成左右手
     *               处理顺序为 ScorePart -> Part -> Measure -> Staff
     * @param: xmlPath MusicXML文件路径
     * @return: void
     * @author: rty
     * @date: 2024/8/24 23:08
     */
    public static void separateXMLByMeasure(String xmlPath){
        ScorePartwise scorePartwise = (ScorePartwise) SeparateUtils.unmarshal(xmlPath);
        /** Part标签部分 */
        List<ScorePartwise.Part> parts = scorePartwise.getPart();

        /** PartList标签部分 */
        PartList partList = scorePartwise.getPartList();

        /** ScorePart标签部分, PartList包含多个ScorePart */
        List<Object> scorePartList = SeparateUtils.getScorePartList(partList);

        int partCount = scorePartList.size();

        /** 对Part标签处理，分开ScorePart */
        for (int i = 0; i < partCount; i++) {
            PartList subPartList = factory.createPartList();

            ScorePart scorePart = (ScorePart)scorePartList.get(i);
            /** Voice声部舍弃 */
            if (scorePart.getPartName().getValue().equals("Voice")) continue;

            /** 只复制ScorePart部分 */
            subPartList.getPartGroupOrScorePart().add(scorePart);

            /** Part的id和ScorePart是对应的，因此也需要处理 */
            ScorePartwise.Part part = parts.get(i);

            /** Part的所有小节 */
            List<ScorePartwise.Part.Measure> measures = part.getMeasure();
            /** 小节列表中的第一小节 */
            ScorePartwise.Part.Measure firstMeasure = measures.get(0);

            /** 从第一小节获取定位信息 */
            List locationInfo = (List) firstMeasure.getNoteOrBackupOrForward().stream().filter(item -> {
                return item instanceof Print || item instanceof Attributes;
            }).collect(Collectors.toList());

            /** 五线谱的数量，默认有一个五线谱 */
            int clefNumber = 1;
            for (int index = 0; index < locationInfo.size(); index++) {
                if (locationInfo.get(index) instanceof Attributes)
                    clefNumber = ((Attributes) locationInfo.get(index)).getClef().size();
            }
            /** 如果小节数小于 MEASURE_COUNT */
            if (measures.size() < MEASURE_COUNT) {
                String tempFileName = "part" + i + "_measure1" + ".xml";
                SeparateUtils.marshalFormScorePartwise(scorePartwise,tempFileName,subPartList,part);
            }

            /** 按照 MEASURE_COUNT 将小节拆分 */
            List<List<ScorePartwise.Part.Measure>> partitionedList = SeparateUtils.separateMeasureList(measures);

            /** 对拆分出来的小节进行处理，序列化为 MusicXML */
            for (int j = 0; j < partitionedList.size(); j++) {
                List<ScorePartwise.Part.Measure> measureList = partitionedList.get(j);

                /** 第一小节因为有Attributes等定位信息，所以不需要添加 */
                if (j != 0) measureList.get(0).getNoteOrBackupOrForward().addAll(0,locationInfo);

                /**  如果只有一个五线谱，则不拆分左右手 */
                if (clefNumber == 1){
                    ScorePartwise.Part subPart = factory.createScorePartwisePart();
                    List<ScorePartwise.Part.Measure> tempMeasureList = measureList;
                    subPart.setId(part.getId());
                    subPart.getMeasure().addAll(tempMeasureList);
                    String tempFileName = "part" + i + "_measure" + j +".xml";
                    SeparateUtils.marshalFormScorePartwise(scorePartwise,tempFileName,subPartList,subPart);
                }else if (clefNumber == 2){
                    /** 有两个clef即代表有两个五线谱，需要拆分左右手 */
                    List<List<ScorePartwise.Part.Measure>> staffList = SeparateUtils.separateStaffList(measureList);
                    for (int k = 0; k < staffList.size(); k++) {
                        ScorePartwise.Part subPart = factory.createScorePartwisePart();
                        List<ScorePartwise.Part.Measure> tempMeasureList = staffList.get(k);
                        subPart.setId(part.getId());
                        subPart.getMeasure().addAll(tempMeasureList);
                        String tempFileName = "part" + i + "_measure" + j + "_staff" + k +".xml";
                        SeparateUtils.marshalFormScorePartwise(scorePartwise,tempFileName,subPartList,subPart);
                    }
                }
            }
        }
    }

}
