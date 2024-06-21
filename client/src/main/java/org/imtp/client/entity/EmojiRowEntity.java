package org.imtp.client.entity;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * @Description
 * @Author ys
 * @Date 2024/6/18 15:22
 */
public class EmojiRowEntity {

    private final ObjectProperty<Emoji> var1;

    private final ObjectProperty<Emoji> var2;

    private final ObjectProperty<Emoji> var3;

    private final ObjectProperty<Emoji> var4;

    private final ObjectProperty<Emoji> var5;

    public EmojiRowEntity(String var1,String var2,String var3,String var4,String var5) {
        this.var1 = new SimpleObjectProperty<>(new Emoji(var1));
        this.var2 = new SimpleObjectProperty<Emoji>(new Emoji(var2));
        this.var3 = new SimpleObjectProperty<Emoji>(new Emoji(var3));
        this.var4 = new SimpleObjectProperty<Emoji>(new Emoji(var4));
        this.var5 = new SimpleObjectProperty<Emoji>(new Emoji(var5));
    }

    public Emoji getVar1() {
        return var1.get();
    }

    public ObjectProperty<Emoji> var1Property() {
        return var1;
    }

    public Emoji getVar2() {
        return var2.get();
    }

    public ObjectProperty<Emoji> var2Property() {
        return var2;
    }

    public Emoji getVar3() {
        return var3.get();
    }

    public ObjectProperty<Emoji> var3Property() {
        return var3;
    }

    public Emoji getVar4() {
        return var4.get();
    }

    public ObjectProperty<Emoji> var4Property() {
        return var4;
    }

    public Emoji getVar5() {
        return var5.get();
    }

    public ObjectProperty<Emoji> var5Property() {
        return var5;
    }
}
