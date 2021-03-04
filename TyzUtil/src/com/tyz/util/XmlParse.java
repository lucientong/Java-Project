package com.tyz.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;

/**
 * XML文件解析器
 *
 * @author tyz
 */
public abstract class XmlParse {
    private static DocumentBuilder db;

    static {
        try {
            db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    public XmlParse() {}

    /**
     * 处理获取到的xml文件中的元素
     * @param element 获取到的元素
     * @param index 下标
     * @return 是否成功处理
     */
    public abstract boolean dealElement(Element element, int index);

    public void getElement(Document doc, String tag) {
        if (doc == null) {
            return;
        }
        NodeList nodeList = doc.getElementsByTagName(tag);
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element element = (Element) nodeList.item(i);
            if (!dealElement(element, i)) {
                break;
            }
        }
    }

    public void getElement(Element parent, String tag) {
        if (parent == null) {
            return;
        }
        NodeList nodeList = parent.getElementsByTagName(tag);
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element element = (Element) nodeList.item(i);
            if (!dealElement(element, i)) {
                break;
            }
        }
    }

    public static Document getDocument(String path) {
        InputStream is = Class.class.getResourceAsStream(path);
        try {
            return db.parse(is);
        } catch (SAXException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
