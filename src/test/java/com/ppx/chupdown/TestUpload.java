package com.ppx.chupdown;

import com.google.gson.Gson;
import com.ppx.pojo.Feedback;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mock;
import org.springframework.mock.web.MockMultipartFile;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/** 文件下载到本地时，需要修改文件的绝对路径absolutePath，才能找到文件
 * 注意每次测试完成后需要删除上传的文件
 * Created by cyh on 2017/7/2.
 */
@RunWith(IDECompatibleParameterized.class)
//@RunWith(Parameterized.class)
public class TestUpload {

    private CyhUpload cyhUpload= new CyhUpload();
    private static String fileName = "cet6.mp3";

    private static  String absolutePath = "G:\\WorkSpace\\ShiXunNew\\springbootnewup\\src\\test\\java\\com\\ppx\\chupdown";


    private String filePath = absolutePath + "\\upload\\";
    Gson gson = new Gson();

    private MockMultipartFile file;
    private CyhParaters cp;
    private Feedback feedback;

    public TestUpload(MockMultipartFile file,CyhParaters cp, Feedback feedback) {
        super();
        this.file = file;
        this.cp = cp;
        this.feedback = feedback;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {

        MockMultipartFile[] files = new MockMultipartFile[4];
        Feedback [] feedbacks = new Feedback[6];
        CyhParaters [] cyhParaterss = new CyhParaters[6];

        try{
            files[0] = new MockMultipartFile(fileName,  new FileInputStream(absolutePath +"\\871199ccd36a1d65c66ad01364b0abcc\\441086217AB0CF828D51B9C64E616F21._0.tmp"));
            files[1] = new MockMultipartFile(fileName,  new FileInputStream(absolutePath + "\\871199ccd36a1d65c66ad01364b0abcc\\7C6B3B2FB9A241C13F3474032FB05015._1.tmp"));
            files[2] = new MockMultipartFile(fileName,  new FileInputStream(absolutePath + "\\871199ccd36a1d65c66ad01364b0abcc\\35279689CD87C347CBD134CF22C89CD1._2.tmp"));
            files[3] = new MockMultipartFile(fileName,  new FileInputStream(absolutePath + "\\871199ccd36a1d65c66ad01364b0abcc\\F1FCEA9D826895E121AAAC2D53B2F2D6._3.tmp"));
        }catch(Exception e){
          e.printStackTrace();
        }

        HashMap<String,Boolean>  map0 = new HashMap<String,Boolean>();
        HashMap<String,Boolean>  map1 = new HashMap<String,Boolean>();
        HashMap<String,Boolean>  map2 = new HashMap<String,Boolean>();
        HashMap<String,Boolean>  map3 = new HashMap<String,Boolean>();
        HashMap<String,Boolean>  map4 = new HashMap<String,Boolean>();
        HashMap<String,Boolean>  map5 = new HashMap<String,Boolean>();

        cyhParaterss[0] = new CyhParaters(null,"e9dc2ed57e51a6b19cabb4bb3659fa85",null,null,"mp3",null,null);
        cyhParaterss[1] = new CyhParaters(null,"e9dc2ed57e51a6b19cabb4bb3659fa85",0,null,"mp3",null,null);
        cyhParaterss[2] = new CyhParaters(null,"e9dc2ed57e51a6b19cabb4bb3659fa85",1,null,"mp3",null,null);

        cyhParaterss[3] = new CyhParaters(null,"e9dc2ed57e51a6b19cabb4bb3659fa85",2,null,"mp3",null,null);
        cyhParaterss[4] = new CyhParaters(null,"e9dc2ed57e51a6b19cabb4bb3659fa85",3,null,"mp3",null,null);
        cyhParaterss[5] = new CyhParaters(null,"e9dc2ed57e51a6b19cabb4bb3659fa85",null,null,"mp3",true,null);


        map0.put("fileExist", false);map0.put("fileDirExist", false);map0.put("chunkExist", false);feedbacks[0] = new Feedback(map0,null);
        map1.put("error", false);feedbacks[1]= new Feedback(map1,null);
        map2.put("fileExist", false);map2.put("fileDirExist", true);Integer [] chunks2 = new Integer[]{0,1,2,3}; feedbacks[2] = new Feedback(map2,chunks2);
        map3.put("mergeSuccess", true);feedbacks[3]= new Feedback(map3,null);
        map4.put("fileExist", true);feedbacks[4]= new Feedback(map4,null);

        Object[][] data = new Object[][] {
                {null,cyhParaterss[0],feedbacks[0]},      //验证大文件与分块文件均不存在
                {files[0],cyhParaterss[1],feedbacks[1]},  //上传分块文件
                {files[1],cyhParaterss[2],feedbacks[1]},  //上传分块文件
                {files[2],cyhParaterss[3],feedbacks[1]},  //上传分块文件
                {files[3],cyhParaterss[4],feedbacks[1]},  //上传分块文件
                {null,cyhParaterss[0],feedbacks[2]},      //上传的大文件不存在但分块文件夹与分块均存在
                {null,cyhParaterss[5],feedbacks[3]},      //合并文件
                {null,cyhParaterss[0],feedbacks[4]}       //验证文件存在
        };

        return Arrays.asList(data);
    }


    @Test
    public void testUpload() throws Exception {
        Feedback feedback2 = cyhUpload.upload(filePath,file,cp);
        Assert.assertEquals(gson.toJson(feedback),gson.toJson(feedback2));
    }

}
