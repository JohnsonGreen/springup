package com.ppx.controller;

import com.ppx.chupdown.CyhFile;
import com.ppx.chupdown.CyhParaters;
import com.ppx.chupdown.CyhUpload;
import com.ppx.chupdown.CyhUploadException;
import com.ppx.pojo.Feedback;
import com.sun.org.apache.xalan.internal.xsltc.dom.AbsoluteIterator;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import static java.lang.System.out;

/**
 * Created by cyh on 2017/6/22.
 */
@Controller
public class IndexController {

    @RequestMapping("/")
    public String index() {
        return "index";
    }

    @RequestMapping("/progress")
    public String progress() {
        return "progress";
    }

    @RequestMapping("/webup")
    public String webup() {
        return "webupload";
    }


//    @RequestParam(value = "fileMd5", required = false) String fileMd5,          //文件的MD5码
//    @RequestParam(value = "chunk", required = false) Integer chunk,             //第几个块
//    @RequestParam(value = "chunkSize", required = false) Long chunkSize,        //块的大小
//    @RequestParam(value = "suffix", required = false) String suffix,           //文件后缀名
//    @RequestParam(value = "merge",required = false) Boolean merge,              //合并信号
//    @RequestParam(value = "cancel",required = false) Boolean cancel             //取消文件上传，删除已上传分块


    @RequestMapping("/upload")
    @ResponseBody
    public Feedback up(
            @RequestParam(value = "fileToUpload", required = false)
                    MultipartFile file, CyhParaters cp
    ) throws IOException, NoSuchAlgorithmException,CyhUploadException{
        String filePath = "H:\\upload\\";    // 文件上传后的路径
        CyhUpload upload = new CyhUpload();
        Feedback feedback =  upload.upload(filePath,file,cp);
        CyhFile fileInfo =  upload.getFileInfo();
        if(fileInfo != null){
            System.out.println(fileInfo);
        }
       return  feedback;
    }


    @RequestMapping(value="/download/{suffix}/{fileName}",method = RequestMethod.GET)
    public String downloadFile(@PathVariable("suffix")String suffix, @PathVariable("fileName") String fileName, HttpServletResponse response) {
        System.out.println(fileName+"."+suffix);
        String filePath = "H:\\upload\\";                          // 文件上传后的路径
        String absoluteName = filePath + fileName+"."+suffix;
        System.out.println(absoluteName);
       return  CyhUpload.download(absoluteName,response);
    }
}
