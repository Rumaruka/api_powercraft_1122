package com.rumaruka.powercraft.api.xml;

import com.rumaruka.powercraft.api.PCLogger;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.File;

public class PCXMLLoader {
    public static PCXMLNode load(File file){
        try{
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(file);
            doc.getDocumentElement().normalize();
            return load((File) doc.getChildNodes().item(0));
        }catch(Exception e){
            PCLogger.severe("Error while reading xml");
            PCLogger.throwing("PCXMLLoader", "load", e);
            return null;
        }
    }

    public static PCXMLNode load(String file){
        try{
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(new ByteArrayInputStream(file.getBytes("UTF-8")));
            doc.getDocumentElement().normalize();
            return load((File) doc.getChildNodes().item(0));
        }catch(Exception e){
            PCLogger.severe("Error while reading xml");
            PCLogger.throwing("PCXMLLoader", "load", e);
            return null;
        }
    }

    private static PCXMLNode load(Node item) {
        PCXMLNode node = new PCXMLNode(item.getNodeName());
        node.setText(item.getTextContent());
        NamedNodeMap nnm = item.getAttributes();
        if(nnm!=null){
            int l = nnm.getLength();
            for(int i=0; i<l; i++){
                Node n = nnm.item(i);
                node.setProperty(n.getNodeName(), n.getNodeValue());
            }
        }
        NodeList nl = item.getChildNodes();
        for(int i=0; i<nl.getLength(); i++){
            if(!nl.item(i).getNodeName().equals("#text")){
                node.addChild(load(nl.item(i)));
            }
        }
        return node;
    }

}
