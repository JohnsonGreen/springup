package com.ppx.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
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
    public JsonObject up( @RequestParam("fileToUpload")MultipartFile file,
                      @RequestParam(value="fileMd5",required=false) String fileMd5,
                      @RequestParam(value="chunk",required=false) Integer chunk,        //第几个块
                      @RequestParam(value="chunkSize",required=false) Integer chunkSize,        //第几个块
                      @RequestParam(value="suffix",required=false) String suffix ) throws IOException, NoSuchAlgorithmException,Exception{


        // 文件上传后的路径
        String filePath = "H:\\upload\\";
        if(suffix != null){
            JsonObject jo = verify( filePath, suffix, fileMd5,  chunk,  chunkSize);
            return jo;
        }

        //获取文件的hash名
        String hashName = fileMd5;   //为完整文件时
        String md5Chunk = "";
        if(fileMd5 != null && chunk != null){   //说明是分块上传
            byte[] uploadBytes = file.getBytes();
            /*******************************计算文件的MD5值*******************************************/
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] digest = md5.digest(uploadBytes);
            md5Chunk = new BigInteger(1, digest).toString(16).toUpperCase();
            /*******************************计算文件的MD5值*******************************************/
            hashName = md5Chunk+"._"+chunk.toString()+".tmp";   //分块时，文件名为分块的hash码
        }

        String fileName = "";  //文件名

        if(fileMd5 != null){                   //包含文件的MD5,说明要校验是否包含文件
            fileName = hashName;
            filePath += fileMd5+"\\";
            File dir = new File(filePath);
            if(!dir.exists())   //创建hash目录
                if(!dir.mkdirs())
                    throw new Exception("目录创建不成功，请重试！");
        }else{
            // 获取文件名
            String[] strarr = file.getOriginalFilename().split("\\\\");
            //String realFileName = fileName.substring(fileName.lastIndexOf("\\"));
            fileName = strarr[strarr.length - 1];
            System.out.println("上传的文件名为：" + fileName);
            // 获取文件的后缀名
            String suffixName = fileName.substring(fileName.lastIndexOf("."));
            System.out.println("上传的后缀名为：" + suffixName);
        }

        if (file.isEmpty()) {
            return "文件为空";
        }

        // fileName = UUID.randomUUID() + suffixName;
        File dest = new File(filePath + fileName);
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


    //验证文件或者分块是否存在
    private  JsonObject  verify(String filePath,String suffix,String fileMd5, Integer chunk, Integer chunkSize){


        JsonObject jo = new JsonObject();

        Map<String,String> map = new HashMap<String,String>();
        File exfile = new File(filePath+fileMd5+"."+suffix);
        if(exfile.exists()){    //存在上传好的大文件
           // map.put("fileExist","true");
            jo.addProperty("fileExist",true);
        }else{
          //  map.put("fileExist","false");
            jo.addProperty("fileExist",false);
            File exdir =  new File(filePath+fileMd5);
            if(exdir.exists()){
                //map.put("fileDirExist","true");
                jo.addProperty("fileDirExist",true);
                File[] fileArray = exdir.listFiles();
                int i =0;
                for(;i < fileArray.length;i++){
                    String numstr = (fileArray[i].getName().split("."))[1];
                    if(chunk.equals(Integer.parseInt(numstr.substring(1))) && chunkSize.equals(fileArray[i].length())){    //分块序号相等，且分块大小相等
                        //map.put("chunkExist","true");
                        jo.addProperty("chunkExist",true);
                        break;
                    }
                    //chunkName.split(".");
                }
                if(i == fileArray.length)
                   // map.put("chunkExist","false");
                    jo.addProperty("chunkExist",false);
            }else{
                //map.put("fileDirExist","false");
                jo.addProperty("fileDirExist",false);
                //map.put("chunkExist","false");
                jo.addProperty("chunkExist",false);
            }
        }

        return jo;
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
