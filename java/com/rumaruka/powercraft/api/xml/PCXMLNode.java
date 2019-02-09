package com.rumaruka.powercraft.api.xml;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class PCXMLNode {

    private String name;

    private List<PCXMLProperty> properties = new ArrayList<PCXMLProperty>();

    private List<PCXMLNode> childs = new ArrayList<PCXMLNode>();

    private String text = "";

    public PCXMLNode(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public void setProperty(String key, String value){
        PCXMLProperty property = getProperty(key);
        if(property==null){
            this.properties.add(new PCXMLProperty(key, value));
        }else{
            property.setValue(value);
        }
    }

    public void setProperty(PCXMLProperty property){
        ListIterator<PCXMLProperty> i = this.properties.listIterator();
        while(i.hasNext()){
            if(i.next().getKey().equals(property.getKey())){
                i.set(property);
                return;
            }
        }
        this.properties.add(property);
    }

    public PCXMLProperty getProperty(String key){
        for(PCXMLProperty property:this.properties){
            if(property.getKey().equals(key)){
                return property;
            }
        }
        return null;
    }

    public void setText(String text){
        this.text = text;
    }

    public String getText(){
        return this.text;
    }

    public void addChild(PCXMLNode child){
        if(!this.childs.contains(child))
            this.childs.add(child);
    }

    public int getChildCount(){
        return this.childs.size();
    }

    public PCXMLNode getChild(int i){
        return this.childs.get(i);
    }

    protected String save(String ls){
        String out = ls + "<"+this.name;
        for(PCXMLProperty property:this.properties){
            out += " "+property.getKey()+" = \""+property.getValue()+"\"";
        }
        if(this.childs.isEmpty() && this.text.trim().isEmpty()){
            return out + "/>";
        }
        out += ">\n";
        String ls2 = ls+"\t";
        for(PCXMLNode child:this.childs){
            out += child.save(ls2)+"\n";
        }
        if(!(this.text==null || this.text.trim().isEmpty())){
            String[] s = this.text.split("\n");
            for(String ss:s){
                out += ls2+ss+"\n";
            }
        }
        return out + ls+"</"+this.name+">";
    }

    public String save() {
        return save("");
    }

}
