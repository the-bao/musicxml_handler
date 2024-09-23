package com.music.backend.demos.util;

import org.apache.xerces.util.XMLCatalogResolver;
import org.audiveris.proxymusic.ScorePartwise;
import org.audiveris.proxymusic.util.Marshalling;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

/**
 * @author rty
 * @version 1.0
 * @description: MusicXML验证工具类
 * @date 2024/8/30 22:09
 */
public class ValidateUtils {
    public static final String catalog = ValidateUtils.class.getResource("/schema/catalog.xml").getFile();
    public static final String XSDPath = ValidateUtils.class.getResource("/xsd/musicxml.xsd").getFile();
    public static final XMLCatalogResolver catalogResolver = new XMLCatalogResolver();
    public static final String BasePath = System.getProperty("user.dir") + File.separator;

    /*
     * @description: 验证ScorePartwise对象转化的MusicXML文件是否合法
     * @param: scorePartwise
     * @return: void
     * @author: rty
     * @date: 2024/8/30 23:02
     */
    public static void validateXML(ScorePartwise scorePartwise) {
        try {
            File tempFile = ValidateUtils.createMusicXMLTempFile(scorePartwise);
            ValidateUtils.validateXML(tempFile.getAbsolutePath());
            ValidateUtils.deleteMusicXMLTempFile(tempFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * @description: 使用xsd文件对xml文件进行校验
     * @param: xmlPath xml文件路径
               xsdPath xsd文件路径
     * @return: void
     * @author: rty
     * @date: 2024/8/23 0:19
     */
    public static void validateXML(String xmlPath) {
        try {
            // XML Catalog 文件路径
            String[] catalogFilePath = new String[]{catalog};
            // XSD 文件路径（可以为空，因为将从 Catalog 文件中加载）
            String xsdFilePath = XSDPath;
            // 要校验的 XML 文件路径
            String xmlFilePath = xmlPath;

            // 设置 XML Catalog Resolver
            catalogResolver.setCatalogList(catalogFilePath);

            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

            // 设置 XML Catalog Resolver
            schemaFactory.setResourceResolver(catalogResolver);
            // 加载 XSD 文件
            File xsdFile = new File(xsdFilePath);
            Schema schema = schemaFactory.newSchema(xsdFile);

            // 创建 Validator 对象
            Validator validator = schema.newValidator();
            validator.setResourceResolver(catalogResolver);

            // 加载 XML 文件
            File xmlFile = new File(xmlFilePath);

            // 执行验证
            validator.validate(new StreamSource(xmlFile));
        } catch (SAXParseException e) {
            System.out.println("XML 文件无效，行号: " + e.getLineNumber() + ", 列号: " + e.getColumnNumber());
            e.printStackTrace();
        } catch (SAXException | IOException e) {
            System.out.println("验证过程中发生错误。");
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /*
     * @description: 将ScorePartwise对象转换为MusicXML的临时文件
     * @param: scorePartwise 需要临时转化的ScorePartwise对象
     * @return: java.io.File
     * @author: rty
     * @date: 2024/8/30 23:00
     */
    public static File createMusicXMLTempFile(ScorePartwise scorePartwise) throws IOException {
        File tempFile = File.createTempFile("temp", ".xml");
        try (OutputStream os = new FileOutputStream(tempFile)){
            /* injectSignature 是否注入ProxyMusic的标签  indentation 控制缩进的格式 */
            Marshalling.marshal(scorePartwise, os, false, 2);
        } catch (Marshalling.MarshallingException e) {
            e.printStackTrace();
        }
        return tempFile;
    }

    /*
     * @description: 直接删除临时文件
     * @param: tempFile
     * @return: void
     * @author: rty
     * @date: 2024/8/30 23:00
     */
    public static void deleteMusicXMLTempFile(File tempFile) {
        try {
            Files.deleteIfExists(tempFile.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
