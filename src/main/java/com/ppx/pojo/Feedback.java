package com.ppx.pojo;

import java.util.Map;

/**
 * Created by cyh on 2017/6/26.
 */
public class Feedback {

   private Map<String,Boolean> exist;
   private Integer [] chunks;

    public Map<String, Boolean> getExist() {
        return exist;
    }

    public void setExist(Map<String, Boolean> exist) {
        this.exist = exist;
    }

    public Integer[] getChunks() {
        return chunks;
    }

    public void setChunks(Integer [] chunks) {
        this.chunks = chunks;
    }
}
