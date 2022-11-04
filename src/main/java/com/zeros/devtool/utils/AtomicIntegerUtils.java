package com.zeros.devtool.utils;

import java.util.concurrent.atomic.AtomicInteger;

public class AtomicIntegerUtils {

    private final static AtomicInteger index =new AtomicInteger(1);


    public AtomicInteger getIndex() {

        return index;
    }

    public static int getAndIncrement(){
        return  index.getAndIncrement();
    }


    public int getAndDecrement(){
        return  index.getAndDecrement();
    }


}
