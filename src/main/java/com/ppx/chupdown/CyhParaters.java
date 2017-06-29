package com.ppx.chupdown;

import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by cyh on 2017/6/28.
 */
public class CyhParaters {

    private Integer fileSize;         //文件大小
    private  String fileMd5;          //文件的MD5码
    private  Integer chunk;           //第几个块
    private  Long chunkSize;          //块的大小
    private  String suffix;           //文件后缀名
    private  Boolean merge;           //合并信号
    private  Boolean cancel;          //取消文件上传，删除已上传分块


    public Integer getFileSize() {
        return fileSize;
    }

    public void setFileSize(Integer fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileMd5() {
        return fileMd5;
    }

    public void setFileMd5(String fileMd5) {
        this.fileMd5 = fileMd5;
    }

    public Integer getChunk() {
        return chunk;
    }

    public void setChunk(Integer chunk) {
        this.chunk = chunk;
    }

    public Long getChunkSize() {
        return chunkSize;
    }

    public void setChunkSize(Long chunkSize) {
        this.chunkSize = chunkSize;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public Boolean getMerge() {
        return merge;
    }

    public void setMerge(Boolean merge) {
        this.merge = merge;
    }

    public Boolean getCancel() {
        return cancel;
    }

    public void setCancel(Boolean cancel) {
        this.cancel = cancel;
    }

}
