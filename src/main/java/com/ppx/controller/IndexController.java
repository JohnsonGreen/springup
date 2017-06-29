package com.ppx.controller;

import com.ppx.chupdown.CyhFile;
import com.ppx.chupdown.CyhParaters;
import com.ppx.chupdown.CyhUpload;
import com.ppx.pojo.Feedback;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
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

    @RequestMapping("/upload2")
    public String upload2(HttpServletRequest request, HttpServletResponse response) throws IllegalStateException, IOException {
        //创建一个通用的多部分解析器
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
        //判断 request 是否有文件上传,即多部分请求
        if (multipartResolver.isMultipart(request)) {
            //转换成多部分request
            MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
            //取得request中的所有文件名
            Iterator<String> iter = multiRequest.getFileNames();
            while (iter.hasNext()) {
                //记录上传过程起始时的时间，用来计算上传时间
                int pre = (int) System.currentTimeMillis();
                //取得上传文件
                MultipartFile file = multiRequest.getFile(iter.next());
                if (file != null) {
                    //取得当前上传文件的文件名称
                    String myFileName = file.getOriginalFilename();
                    //如果名称不为“”,说明该文件存在，否则说明该文件不存在
                    if (myFileName.trim() != "") {
                        out.println(myFileName);
                        //重命名上传后的文件名
                        String fileName = "demoUpload" + file.getOriginalFilename();
                        //定义上传路径
                        String path = "H:/" + fileName;
                        File localFile = new File(path);
                        file.transferTo(localFile);
                    }
                }
                //记录上传该文件后的时间
                int finaltime = (int) System.currentTimeMillis();
                out.println(finaltime - pre);
            }

        }
        return "/success";
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
            @RequestParam(value = "fileToUpload", required = false) MultipartFile file,
            CyhParaters cp
    ) throws IOException, NoSuchAlgorithmException, Exception {

        String filePath = "H:\\upload\\";                          // 文件上传后的路径
        CyhUpload upload = new CyhUpload();
        Feedback feedback =  upload.upload(filePath,file,cp);  //先调用上传函数，然后才能获取文件信息---包含断点续传
       // Feedback feedback =  upload.upload(filePath,file);      //普通上传
        CyhFile fileInfo =  upload.getFileInfo();
        if(fileInfo != null){                               //可能为验证情况，只有在文件上传完成之后才能获取文件信息
            System.out.println(fileInfo);
        }
       return  feedback;
    }




}
