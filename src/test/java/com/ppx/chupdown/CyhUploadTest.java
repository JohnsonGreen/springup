package com.ppx.chupdown;

import com.google.gson.Gson;
import com.ppx.pojo.Feedback;
import org.junit.*;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockMultipartHttpServletRequest;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/** 
* CyhUpload Tester. 
* 
* @author <Authors name> 
* @since <pre>七月 2, 2017</pre> 
* @version 1.0 
*/ 
public class CyhUploadTest {

    private CyhUpload cyhUpload= new CyhUpload();
    private static String fileName = "sdfdsf.docx";
    private static MockMultipartFile file;
    private String filePath = "H:\\upload\\";
    private CyhParaters cp = new CyhParaters(null,"sdjfkdkfjdkfjkds",null,null,".docx",null,null);
    private Feedback feedback = new Feedback();
    Gson gson = new Gson();


@BeforeClass
public static void beforeClass() throws Exception {
    InputStream contentStream = new FileInputStream("H:\\_\\数学建模大作业.docx");
    file = new MockMultipartFile(fileName, contentStream);
}


@AfterClass
public static void afterClass() throws Exception {

} 

/** 
* 
* Method: upload(String filePath, MultipartFile file, CyhParaters cp) 
* 
*/ 
@Test
public void testUpload() throws Exception {
    Map<String,Boolean>  map = new HashMap<String,Boolean>();
    map.put("error", false);
    feedback.setExist(map);
    Feedback feedback2 = cyhUpload.upload(filePath,file,cp);
    Assert.assertEquals(gson.toJson(feedback),gson.toJson(feedback2));
} 

/** 
* 
* Method: download(String fileName, HttpServletResponse response) 
* 
*/ 
@Test
public void testDownload() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: getFileInfo() 
* 
*/ 
@Test
public void testGetFileInfo() throws Exception { 
//TODO: Test goes here...
} 

/** 
* 
* Method: compare(Object a, Object b) 
* 
*/ 
@Test
public void testCompare() throws Exception { 
//TODO: Test goes here... 
} 


/** 
* 
* Method: cancelUpload(String mergeDir) 
* 
*/ 
@Test
public void testCancelUpload() throws Exception { 
//TODO: Test goes here... 
/* 
try { 
   Method method = CyhUpload.getClass().getMethod("cancelUpload", String.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/ 
} 

/** 
* 
* Method: setFileInfo(MultipartFile file, CyhParaters cp) 
* 
*/ 
@Test
public void testSetFileInfo() throws Exception { 
//TODO: Test goes here... 
/* 
try { 
   Method method = CyhUpload.getClass().getMethod("setFileInfo", MultipartFile.class, CyhParaters.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/ 
} 

/** 
* 
* Method: mergeSupport(String mergeDir, String filePath, String fileMd5, String suffix) 
* 
*/ 
@Test
public void testMergeSupport() throws Exception { 
//TODO: Test goes here... 
/* 
try { 
   Method method = CyhUpload.getClass().getMethod("mergeSupport", String.class, String.class, String.class, String.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/ 
} 

/** 
* 
* Method: mergeFiles(String filePath, String fileMd5, String suffix, File[] files, Boolean isChunked) 
* 
*/ 
@Test
public void testMergeFiles() throws Exception { 
//TODO: Test goes here... 
/* 
try { 
   Method method = CyhUpload.getClass().getMethod("mergeFiles", String.class, String.class, String.class, File[].class, Boolean.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/ 
} 

/** 
* 
* Method: delete(File file) 
* 
*/ 
@Test
public void testDelete() throws Exception { 
//TODO: Test goes here... 
/* 
try { 
   Method method = CyhUpload.getClass().getMethod("delete", File.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/ 
} 

/** 
* 
* Method: verify(String filePath, String suffix, String fileMd5, Integer chunk, Long chunkSize) 
* 
*/ 
@Test
public void testVerify() throws Exception { 
//TODO: Test goes here... 
/* 
try { 
   Method method = CyhUpload.getClass().getMethod("verify", String.class, String.class, String.class, Integer.class, Long.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/ 
} 

} 
