package com.music.backend.demos.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @author rty
 * @version 1.0
 * @description: TODO
 * @date 2024/8/25 19:12
 */
public class Utils {

    public static <T> List<T> deepCopyListUsingXml(List<T> originalList) throws JAXBException {
        // 创建JAXB上下文
        JAXBContext context = JAXBContext.newInstance(Object.class);

        // 创建Marshaller用于序列化
        Marshaller marshaller = context.createMarshaller();
        StringWriter writer = new StringWriter();
        marshaller.marshal(originalList, writer);
        String xml = writer.toString();

        // 创建Unmarshaller用于反序列化
        Unmarshaller unmarshaller = context.createUnmarshaller();
        StringReader reader = new StringReader(xml);
        List<T> deepCopy = (List<T>) unmarshaller.unmarshal(reader);

        return deepCopy;
    }
}
