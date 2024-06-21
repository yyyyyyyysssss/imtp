package org.imtp.client.entity;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * @Description
 * @Author ys
 * @Date 2024/6/18 15:22
 */
public class EmojiRowEntity {

    private final ObjectProperty<EmojiEntity> var1;

    private final ObjectProperty<EmojiEntity> var2;

    private final ObjectProperty<EmojiEntity> var3;

    private final ObjectProperty<EmojiEntity> var4;

    private final ObjectProperty<EmojiEntity> var5;

    public EmojiRowEntity(String var1,String var2,String var3,String var4,String var5) {
        this.var1 = new SimpleObjectProperty<>(new EmojiEntity(var1));
        this.var2 = new SimpleObjectProperty<EmojiEntity>(new EmojiEntity(var2));
        this.var3 = new SimpleObjectProperty<EmojiEntity>(new EmojiEntity(var3));
        this.var4 = new SimpleObjectProperty<EmojiEntity>(new EmojiEntity(var4));
        this.var5 = new SimpleObjectProperty<EmojiEntity>(new EmojiEntity(var5));
    }

    public EmojiEntity getVar1() {
        return var1.get();
    }

    public ObjectProperty<EmojiEntity> var1Property() {
        return var1;
    }

    public EmojiEntity getVar2() {
        return var2.get();
    }

    public ObjectProperty<EmojiEntity> var2Property() {
        return var2;
    }

    public EmojiEntity getVar3() {
        return var3.get();
    }

    public ObjectProperty<EmojiEntity> var3Property() {
        return var3;
    }

    public EmojiEntity getVar4() {
        return var4.get();
    }

    public ObjectProperty<EmojiEntity> var4Property() {
        return var4;
    }

    public EmojiEntity getVar5() {
        return var5.get();
    }

    public ObjectProperty<EmojiEntity> var5Property() {
        return var5;
    }
}
