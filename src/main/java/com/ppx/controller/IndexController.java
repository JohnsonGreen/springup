package com.ppx.controller;

import com.google.gson.Gson;
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
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by cyh on 2017/6/22.
 */
@Controller
public class IndexController {

    @RequestMapping("/")
    public String index(){
        return "index";
    }

    @RequestMapping("/progress")
    public String progress(){
        return "progress";
    }

    @RequestMapping("/webup")
    public String webup(){
        return "webupload";
    }

    @RequestMapping("/upload2"  )
    public String upload2(HttpServletRequest request, HttpServletResponse response) throws IllegalStateException, IOException {
        //创建一个通用的多部分解析器
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
        //判断 request 是否有文件上传,即多部分请求
        if(multipartResolver.isMultipart(request)){
            //转换成多部分request
            MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest)request;
            //取得request中的所有文件名
            Iterator<String> iter = multiRequest.getFileNames();
            while(iter.hasNext()){
                //记录上传过程起始时的时间，用来计算上传时间
                int pre = (int) System.currentTimeMillis();
                //取得上传文件
                MultipartFile file = multiRequest.getFile(iter.next());
                if(file != null){
                    //取得当前上传文件的文件名称
                    String myFileName = file.getOriginalFilename();
                    //如果名称不为“”,说明该文件存在，否则说明该文件不存在
                    if(myFileName.trim() !=""){
                        System.out.println(myFileName);
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
                System.out.println(finaltime - pre);
            }

        }
        return "/success";
    }


    @RequestMapping("/upload")
    @ResponseBody
    public String up( @RequestParam("fileToUpload")MultipartFile file,
                      @RequestParam(value="fileMd5",required=false) String fileMd5,
                      @RequestParam(value="chunk",required=false) String chunk,        //第几个块
                      @RequestParam(value="chunkSize",required=false)Integer chunkSize  //块的大小，用于申请内存计算块的MD5值，设计最大不能超过5M
                   ) throws IOException, NoSuchAlgorithmException,Exception{

        if(chunk != null  && chunkSize != null && chunkSize != 0){   //说明是分块上传
            byte[] uploadBytes = file.getBytes();

            /*******************************计算文件的MD5值*******************************************/
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] digest = md5.digest(uploadBytes);
            String hashString = new BigInteger(1, digest).toString(16).toUpperCase();
            /*******************************计算文件的MD5值*******************************************/

        }



        // 文件上传后的路径
        String filePath = "H:\\upload\\";
        if(fileMd5 != null){                   //包含文件的MD5,说明要校验是否包含文件
            filePath += fileMd5+"\\";
            File dir = new File(filePath);
            if(!dir.exists())   //创建hash目录
                if(!dir.mkdirs())
                    throw new Exception("目录创建不成功，请重试！");
        }

        if (file.isEmpty()) {
            return "文件为空";
        }


        // 获取文件名
        String fileName = file.getOriginalFilename();
        String[] strarr = fileName.split("\\\\");
        //String realFileName = fileName.substring(fileName.lastIndexOf("\\"));
        String realFileName = strarr[strarr.length - 1];
        System.out.println("上传的文件名为：" + fileName);
        // 获取文件的后缀名
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        System.out.println("上传的后缀名为：" + suffixName);

        // 解决中文问题，liunx下中文路径，图片显示问题
        // fileName = UUID.randomUUID() + suffixName;
        File dest = new File(filePath + realFileName);
        // 检测是否存在目录
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }
        try {
            file.transferTo(dest);
            return "上传成功";
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "上传失败";
    }


    @RequestMapping("/verify")
    @ResponseBody
    public  Map<String,String> verify(String fileMd5,String chunkMd5, Integer chunk, Integer chunkSize){

        Map<String,String> map = new HashMap<String,String>();
        //map.put();

        return map;
    }


    public static boolean createDir(String destDirName) {
        File dir = new File(destDirName);
        if (dir.exists()) {
            System.out.println("创建目录" + destDirName + "失败，目标目录已经存在");
            return false;
        }
        if (!destDirName.endsWith(File.separator)) {
            destDirName = destDirName + File.separator;
        }
        //创建目录
        if (dir.mkdirs()) {
            System.out.println("创建目录" + destDirName + "成功！");
            return true;
        } else {
            System.out.println("创建目录" + destDirName + "失败！");
            return false;
        }
    }



}
