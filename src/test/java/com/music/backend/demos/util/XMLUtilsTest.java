package com.music.backend.demos.util;

import org.junit.jupiter.api.Test;

import java.io.File;

/**
 * @author rty
 * @version 1.0
 * @description: TODO
 * @date 2024/9/23 19:18
 */
public class XMLUtilsTest {

    @Test
    public void separateXMLByMeasureTest() {
        String workPath = System.getProperty("user.dir") + File.separator;
        System.out.println(workPath);
        String xmlPath = workPath + "src/main/resources/musicxml/MozaVeilSample.xml";
        String xsdPath = workPath + "src/main/resources/xsd/musicxml.xsd";
        String testPath = workPath + "src/main/resources/temp/temp.xml";
        
        XMLUtils.separateXMLByMeasure(xmlPath);
    }

    @Test
    public void separateXMLByPartTest() {
        String workPath = System.getProperty("user.dir") + File.separator;
        System.out.println(workPath);
        String xmlPath = workPath + "src/main/resources/musicxml/MozaVeilSample.xml";
        String xsdPath = workPath + "src/main/resources/xsd/musicxml.xsd";
        String testPath = workPath + "src/main/resources/temp/temp.xml";

        XMLUtils.separateXMLByPart(xmlPath);
    }
}
