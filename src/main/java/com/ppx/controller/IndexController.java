package com.ppx.controller;

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

    @RequestMapping("/upload")
    @ResponseBody
    public Feedback up(@RequestParam(value = "fileToUpload", required = false) MultipartFile file,
                       @RequestParam(value = "fileMd5", required = false) String fileMd5,          //文件的MD5码
                       @RequestParam(value = "chunk", required = false) Integer chunk,             //第几个块
                       @RequestParam(value = "chunkSize", required = false) Long chunkSize,        //块的大小
                       @RequestParam(value = "suffix", required = false) String suffix,           //文件后缀名
                       @RequestParam(value = "merge",required = false) Boolean merge,              //合并信号
                       @RequestParam(value = "cancel",required = false) Boolean cancel             //取消文件上传，删除已上传分块
                                   ) throws IOException, NoSuchAlgorithmException, Exception {

       Feedback feedback = new Feedback();

        // 文件上传后的路径
        String filePath = "H:\\upload\\";
        String mergeDir = filePath+fileMd5;
        if (file == null && suffix != null && merge == null) {                           //存在suffix且合并信号为空说明是验证行为，不是上传行为
            feedback  = verify(filePath, suffix, fileMd5, chunk, chunkSize);
            return feedback;
        } else if (suffix != null && merge != null && merge == true) {                   //存在suffix且合并行为为true说明是合并行为,注意先判空再比较，否则会报空指针异常

            File exdir =  new File(mergeDir);
            File[] fileArray = exdir.listFiles();
            if(fileArray.length > 1)
                  Arrays.sort(fileArray, new ByNumComparator());             //对分块按照序号排序
            Boolean mergeBool = mergeFiles(mergeDir+"."+suffix, fileArray);
            Map<String,Boolean> mergeMap = new HashMap<String,Boolean>();
            if(mergeBool){
               delete(new File(mergeDir));       //删除文件夹下的文件
                mergeMap.put("mergeSuccess",true);
            }else{
               // throw new Exception("合并失败！");
                mergeMap.put("mergeSuccess",false);
            }
            feedback.setExist(mergeMap);
            return feedback;
        }

        //获取文件的hash名
        String hashName = fileMd5;   //为完整文件时
        String md5Chunk = "";
        if (fileMd5 != null && chunk != null) {   //说明是分块上传
            byte[] uploadBytes = file.getBytes();
            /*******************************计算文件的MD5值*******************************************/
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] digest = md5.digest(uploadBytes);
            md5Chunk = new BigInteger(1, digest).toString(16).toUpperCase();
            /*******************************计算文件的MD5值*******************************************/
            hashName = md5Chunk + "._" + chunk.toString() + ".tmp";   //分块时，文件名为分块的hash码
        }else{
            hashName +="._"+0+".tmp";
        }

        String fileName = "";  //文件名

        if (fileMd5 != null){                      //包含文件的MD5,说明要校验是否包含文件
            fileName = hashName;
            filePath += fileMd5 + "\\";
            File dir = new File(filePath);
            if (!dir.exists())                      //创建hash目录
                if (!dir.mkdirs())
                    throw new Exception("目录创建不成功，请重试！");
        } else {
            String[] strarr = file.getOriginalFilename().split("\\\\");              // 获取文件名
            //String realFileName = fileName.substring(fileName.lastIndexOf("\\"));
            fileName = strarr[strarr.length - 1];
            out.println("上传的文件名为：" + fileName);
            String suffixName = fileName.substring(fileName.lastIndexOf("."));          // 获取文件的后缀名
            out.println("上传的后缀名为：" + suffixName);
        }

        Map<String, Boolean> ma = new HashMap<String, Boolean>();
        if (file.isEmpty()) {
            ma.put("error", true);
            feedback.setExist(ma);
            return feedback;
        }

        // fileName = UUID.randomUUID() + suffixName;
        File dest = new File(filePath + fileName);
        // 检测是否存在目录
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }
        try {
            file.transferTo(dest);
            ma.put("error", false);
            feedback.setExist(ma);
            return feedback;

        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ma.put("error", true);
        feedback.setExist(ma);
        return feedback;
    }


    //必须等文件上传完毕才能合并，因为多线程上传文件序号不同
//    public void mergeChunk(String outFileName, File chunk) throws IOException {
//         File file = new File(outFileName);
//         if(!file.exists())
//             file.createNewFile();
//         File [] files = new File[1];
//         files[0] = chunk;
//         mergeFiles(file.getAbsolutePath(),files);
//    }


    //等所有分块都上传完毕才合并，对大文件较慢
    public Boolean mergeFiles(String outFile, File[] files) {
        int BUFSIZE = 1024 * 1024 * 5;
        FileChannel outChannel = null;
        out.println("Merge " + Arrays.toString(files) + " into " + outFile);
        try {
            outChannel = new FileOutputStream(outFile).getChannel();
            for (File f : files) {
                String absolutePath = f.getAbsolutePath();

                FileChannel fc = new FileInputStream(absolutePath).getChannel();
                ByteBuffer bb = ByteBuffer.allocate(BUFSIZE);
                while (fc.read(bb) != -1) {
                    bb.flip();
                    outChannel.write(bb);
                    bb.clear();
                }
                fc.close();
            }
            out.println("Merged!! ");
            return true;
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return false;
        } finally {
            try {
                if (outChannel != null) {
                    outChannel.close();
                }
            } catch (IOException ignore) {
            }
        }
    }

    //删除文件夹下的分块
    private void delete(File file){
       if(file.isDirectory()){
           File[] files = file.listFiles();
           for(int i=0;i < files.length;i++)
               delete(files[i]);
       }
       file.delete();
    }


    //验证文件或者分块是否存在
    private Feedback verify(String filePath, String suffix, String fileMd5, Integer chunk, Long chunkSize) {

        Map<String, Boolean> map = new HashMap<String, Boolean>();
        Feedback feedback = new Feedback();
        String fileDir = filePath+fileMd5;
        File exfile = new File(fileDir + "." + suffix);
        if (exfile.exists()) {    //存在上传好的大文件
            map.put("fileExist", true);
        } else {
            map.put("fileExist", false);
           // if (chunk != null && chunkSize != null) {
                File exdir = new File(fileDir);
                if (exdir.exists()) {
                    map.put("fileDirExist", true);
                    File[] fileArray = exdir.listFiles();
                    if(fileArray != null){
                        if(fileArray.length > 0){
                           // String [] md5Chunks = new String[fileArray.length];            //获取每个分块的md5码
                            Integer [] chunks = new Integer[fileArray.length];
                            for(int j = 0;j < chunks.length;j++)
                                chunks[j] = Integer.parseInt((fileArray[j].getName().split("\\.")[1]).substring(1));
                            Arrays.sort(chunks);
                            feedback.setChunks(chunks);
                        }

                        /**************************验证分块是否存在*****************************/
                        /*
                        int i = 0;
                        for (; i < fileArray.length; i++) {
                            String numstr = (fileArray[i].getName().split("\\."))[1];
                            Long chunkLength = fileArray[i].length();
                            if (chunk.equals(Integer.parseInt(numstr.substring(1))) && chunkSize.equals(chunkLength)) {    //分块序号相等，且分块大小相等
                                map.put("chunkExist", true);
                                break;
                            }
                        }
                        if (i == fileArray.length)
                            map.put("chunkExist", false);
                            */
                        /**************************验证分块*****************************/


                    }else
                        map.put("chunkExist", false);
                } else {
                    map.put("fileDirExist", false);
                    map.put("chunkExist", false);
                }
           // }
        }
        feedback.setExist(map);
        return feedback;
    }


    //将File数组从小到大排序
   class ByNumComparator implements Comparator {
       @Override
       public int compare(Object a, Object b) {
           int  orderA = Integer.parseInt(((((File)a).getName().split("\\."))[1]).substring(1));
           int  orderB = Integer.parseInt(((((File)b).getName().split("\\."))[1]).substring(1));
          if(orderA > orderB)
              return 1;
          else if(orderA < orderB)
              return -1;
          else
              return 0;
       }
   }




}
