package org.imtp.client.entity;

/**
 * @Description
 * @Author ys
 * @Date 2024/6/21 14:37
 */
public class Emoji {


    public Emoji(String unicode){
        this(null,unicode,null,0);
    }

    public Emoji(String shortname,String unicode,String desc,int order){
        this.shortname = shortname;
        this.unicode = unicode;
        this.desc = desc;
        this.order = order;
    }

    private String shortname;

    private String unicode;

    private String desc;

    private int order;

    public String getShortname() {
        return shortname;
    }

    public void setShortname(String shortname) {
        this.shortname = shortname;
    }

    public String getUnicode() {
        return unicode;
    }

    public void setUnicode(String unicode) {
        this.unicode = unicode;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public String toString() {
        return "Emoji{" +
                "shortname='" + shortname + '\'' +
                ", unicode='" + unicode + '\'' +
                ", desc='" + desc + '\'' +
                ", order=" + order +
                '}';
    }
}
