package com.ppx.chupdown;

import com.ppx.pojo.Feedback;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * Created by cyh on 2017/7/3.
 */

public class TestDownload {

    private MockHttpServletResponse httpServletResponse = new MockHttpServletResponse();
    private static  String absolutePath = "G:\\WorkSpace\\ShiXunNew\\springbootnewup\\src\\test\\java\\com\\ppx\\chupdown";
    private String fileName = "e9dc2ed57e51a6b19cabb4bb3659fa85.mp3";

    @Test
    public void testDownload() throws Exception {
        String res =CyhUpload.download(absolutePath+"\\download\\"+fileName,httpServletResponse);
        Assert.assertEquals("success",res);
    }

}
