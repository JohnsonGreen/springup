package com.ppx.chupdown;


import com.ppx.pojo.Feedback;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.*;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.util.*;

import org.springframework.web.multipart.MultipartFile;

import static java.lang.System.out;

/**
 * Created by cyh on 2017/5/26.
 */
public class CyhUpload {

    private static final String POST_METHOD = "POST";   //限定只能POST上传
    public static final String MULTIPART = "multipart/";

    protected CyhFile fileInfo;      //文件相关信息
    protected CyhParaters cp;    //提交参数

    public CyhUpload(){
    }


    /**
     *   上传函数--断点续传包含验证过程
     * @param filePath
     * @param file
     * @param cp
     * @return
     * @throws Exception
     */
    public Feedback upload(String filePath, MultipartFile file,CyhParaters cp) throws Exception {

        String fileMd5 = cp.getFileMd5();
        Integer chunk = cp.getChunk();
        Long chunkSize = cp.getChunkSize();
        String suffix = cp.getSuffix();
        Boolean merge = cp.getMerge();
        Boolean cancel = cp.getCancel();

        Feedback feedback = new Feedback();
        String mergeDir = filePath + fileMd5;

        if (cancel != null && cancel == true) {  //取消文件上传并删除
            return cancelUpload(mergeDir);
        }

        if (file == null && suffix != null && merge == null) {                           //存在suffix且合并信号为空说明是验证行为，不是上传行为
            feedback = verify(filePath, suffix, fileMd5, chunk, chunkSize);
            return feedback;
        } else if (suffix != null && merge != null && merge == true) {                   //存在suffix且合并行为为true说明是合并行为,注意先判空再比较，否则会报空指针异常
            fileInfo = setFileInfo(file, cp);                                           //合并完成后获取文件信息
            return mergeSupport(mergeDir,filePath,fileMd5,suffix);
        }

        //获取文件的hash名
        String hashName = fileMd5;   //为完整文件时
        String md5Chunk = "";
        if (fileMd5 != null && chunk != null) {   //说明是分块上传
            byte[] uploadBytes = file.getBytes();
            MessageDigest md5 = MessageDigest.getInstance("MD5");   //计算文件的MD5值
            byte[] digest = md5.digest(uploadBytes);
            md5Chunk = new BigInteger(1, digest).toString(16).toUpperCase();
            hashName = md5Chunk + "._" + chunk.toString() + ".tmp";      //分块时，文件名为分块的hash码
        } else {
            hashName += "._" + 0 + ".tmp";
        }
        String fileName = "";  //文件名
        if (fileMd5 != null) {                      //包含文件的MD5,说明要判断是否存在文件目录
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

    /**
     * 普通上传，不带断点续传的
     * @param filePath
     * @param file
     * @return
     */
    public Feedback upload(String filePath, MultipartFile file){

        return  null;
    }


    //取消文件上传删除可能存在的临时文件
    private Feedback cancelUpload(String mergeDir){
        Feedback feedback = new Feedback();
        File tpFile = new File(mergeDir + ".tmp");
        if (tpFile.exists())    //删除临时文件，合并不完整的临时文件
            delete(tpFile);
        Map<String, Boolean> map = new HashMap<String, Boolean>();
        map.put("cancelSuccess", true);
        feedback.setExist(map);
        return feedback;
    }


    /**
     * 设置文件信息
     * @param file
     * @param cp
     * @return
     */
    private CyhFile setFileInfo(MultipartFile file, CyhParaters cp) {
        if (file != null) {
            CyhFile fileInfo = new CyhFile();
            fileInfo.setContentType(file.getContentType());//获取文件MIME类型
            String originName = file.getOriginalFilename();
            String[] strArr = originName.split("\\\\");
            String fileName = strArr[strArr.length - 1];
            fileInfo.setFileName(fileName);
            String[] nameArr = fileName.split("\\.");
            fileInfo.setExtensionName(nameArr[nameArr.length - 1]);
            fileInfo.setFileSize(cp.getFileSize());
            return fileInfo;
        } else
            return null;
    }

    /**
     * 对文件合并进一步包装
     * @param mergeDir
     * @param filePath
     * @param fileMd5
     * @param suffix
     * @return
     */
    private Feedback mergeSupport(String mergeDir,String filePath,String fileMd5,String suffix){
        Feedback feedback = new Feedback();
        File exdir = new File(mergeDir);
        File[] fileArray = exdir.listFiles();
        if (fileArray.length > 1)
            Arrays.sort(fileArray, new ByNumComparator());             //对分块按照序号排序
        Boolean mergeBool = mergeFiles(filePath, fileMd5, suffix, fileArray);
        Map<String, Boolean> mergeMap = new HashMap<String, Boolean>();
        if (mergeBool) {
            delete(new File(mergeDir));       //删除文件夹下的文件
            mergeMap.put("mergeSuccess", true);
        } else {
            // throw new Exception("合并失败！");
            mergeMap.put("mergeSuccess", false);
        }
        feedback.setExist(mergeMap);
        return feedback;
    }


    /**
     *  等所有分块都上传完毕才合并，对大文件较慢
     * @param filePath
     * @param fileMd5
     * @param suffix
     * @param files
     * @return
     */
    private Boolean mergeFiles(String filePath, String fileMd5, String suffix, File[] files) {
        int BUFSIZE = 1024 * 1024 * 5;
        String mergeDir = filePath + fileMd5;
        String outFile = mergeDir + "." + suffix;
        String outTemp = mergeDir + ".tmp";       //合并时的临时文件
        File outTempFile = new File(outTemp);
        if (outTempFile.exists())                 //临时文件存在则删除
            delete(outTempFile);

        FileChannel outChannel = null;
        try {
            outChannel = new FileOutputStream(outTempFile).getChannel();
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
            if (outChannel != null) {
                outChannel.close();
            }
            outTempFile.renameTo(new File(outFile));
            out.println("Merged!! ");
            return true;
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return false;
        } finally {
        }
    }

    /**
     *  删除文件夹下的分块
     * @param file
     */
    private void delete(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++)
                delete(files[i]);
        }
        file.delete();
    }


    /**
     *  验证文件或者分块是否存在
     * @param filePath
     * @param suffix
     * @param fileMd5
     * @param chunk
     * @param chunkSize
     * @return
     */
    private Feedback verify(String filePath, String suffix, String fileMd5, Integer chunk, Long chunkSize) {

        Map<String, Boolean> map = new HashMap<String, Boolean>();
        Feedback feedback = new Feedback();
        String fileDir = filePath + fileMd5;
        File exfile = new File(fileDir + "." + suffix);
        if (exfile.exists()) {    //存在上传好的大文件
            map.put("fileExist", true);
        } else {
            map.put("fileExist", false);
            File exdir = new File(fileDir);
            if (exdir.exists()) {
                map.put("fileDirExist", true);
                File[] fileArray = exdir.listFiles();
                if (fileArray != null) {
                    if (fileArray.length > 0) {
                        Integer[] chunks = new Integer[fileArray.length];
                        for (int j = 0; j < chunks.length; j++)
                            chunks[j] = Integer.parseInt((fileArray[j].getName().split("\\.")[1]).substring(1));
                        Arrays.sort(chunks);
                        feedback.setChunks(chunks);
                    }

                } else
                    map.put("chunkExist", false);
            } else {
                map.put("fileDirExist", false);
                map.put("chunkExist", false);
            }
        }
        feedback.setExist(map);
        return feedback;
    }


    public CyhFile getFileInfo(){
        return fileInfo;
    }

    /**
     * 内部类：目的是将File数组从小到大排序
     */
    class ByNumComparator implements Comparator {
        @Override
        public int compare(Object a, Object b) {
            int orderA = Integer.parseInt(((((File) a).getName().split("\\."))[1]).substring(1));
            int orderB = Integer.parseInt(((((File) b).getName().split("\\."))[1]).substring(1));
            if (orderA > orderB)
                return 1;
            else if (orderA < orderB)
                return -1;
            else
                return 0;
        }
    }



    //产生唯一文件名
    public String geneUniqueName(String suffix){
        return UUID.randomUUID()+"."+suffix;
    }


}


