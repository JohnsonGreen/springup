package com.ppx.chupdown;

import com.google.gson.Gson;
import com.ppx.pojo.Feedback;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.mock.web.MockMultipartFile;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by cyh on 2017/7/2.
 */
@RunWith(IDECompatibleParameterized.class)
public class TestUpload {

    private CyhUpload cyhUpload= new CyhUpload();
    private static String fileName = "sdfdsf.docx";
    private static MockMultipartFile file;
    private String filePath = "H:\\upload\\";
    Gson gson = new Gson();

    private CyhParaters cp;
    private Feedback feedback;

    public TestUpload(CyhParaters cp, Feedback feedback) {
        super();
        this.cp = cp;
        this.feedback = feedback;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {

        CyhParaters [] cyhParaterss = new CyhParaters[6];
        cyhParaterss[0] = new CyhParaters(null,"e9dc2ed57e51a6b19cabb4bb3659fa85",null,null,".docx",null,null);
        cyhParaterss[1] = new CyhParaters(null,"e9dc2ed57e51a6b19cabb4bb3659fa85",null,null,".docx",null,null);
        cyhParaterss[2] = new CyhParaters(null,"e9dc2ed57e51a6b19cabb4bb3659fa85",null,null,".docx",null,null);
        cyhParaterss[3] = new CyhParaters(null,"e9dc2ed57e51a6b19cabb4bb3659fa85",null,null,".docx",null,null);
        cyhParaterss[4] = new CyhParaters(null,"e9dc2ed57e51a6b19cabb4bb3659fa85",null,null,".docx",null,null);
        cyhParaterss[5] = new CyhParaters(null,"e9dc2ed57e51a6b19cabb4bb3659fa85",null,null,".docx",null,null);


        Feedback [] feedbacks = new Feedback[6];
        HashMap<String,Boolean>  map = new HashMap<String,Boolean>();
        map.put("error", false);
        feedbacks[0] = new Feedback(map,null);
       // map.clear();

        Object[][] data = new Object[][] {
                {cyhParaterss[0],feedbacks[0]}
        };
        return Arrays.asList(data);
    }


    @Test
    public void testUpload() throws Exception {

        InputStream contentStream = new FileInputStream("H:\\_\\abc.docx");
        file = new MockMultipartFile(fileName, contentStream);

        Feedback feedback2 = cyhUpload.upload(filePath,file,cp);
        Assert.assertEquals(gson.toJson(feedback),gson.toJson(feedback2));
    }

}
