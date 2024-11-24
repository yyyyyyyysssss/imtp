package org.imtp.desktop.util;

public class Tuple2<V1,V2> {

    private V1 v1;
    private V2 v2;

    public Tuple2(V1 v1, V2 v2){
        this.v1 = v1;
        this.v2 = v2;
    }

    public V1 getV1(){
        return this.v1;
    }

    public V2 getV2(){
        return this.v2;
    }

}
