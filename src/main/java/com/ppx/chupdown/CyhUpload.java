package com.ppx.chupdown;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
//mport javax.servlet.jsp.PageContext;
import java.io.*;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by cyh on 2017/5/26.
 */
public class CyhUpload {

    private static final String POST_METHOD = "POST";   //限定只能POST上传
    public static final String MULTIPART = "multipart/";


    protected HttpServletRequest m_httpServletRequest;
    protected HttpServletResponse m_httpServletResponse;
    protected CyhFile file;


    //int类型为4个字节，在这里能够表示高达2^32-1个字节，而1GB为2^30个字节
    private int m_currentIndex;
    private byte m_byteArray[];
    private int m_totalBytes;    //上传上来的包括分界线在内的总大小
    private String m_filename;
    private String m_suffix;
    private String m_border;   // 各数据之间的分隔边界
    private String m_dataHeader;  //单个数据的头
    private int m_startData;   //文件数据开始的地方
    private int m_endData;     //文件数据结束的地方
    private int m_size;         //文件大小

    public CyhUpload(){
    }

    /*
    public CyhUpload(PageContext pageContext){
        m_httpServletRequest = (HttpServletRequest) pageContext.getRequest();
        m_httpServletResponse = (HttpServletResponse) pageContext.getResponse();
        m_border = new String();
        m_filename=new String();
        m_border=new String();
        m_suffix=new String();
        m_dataHeader = new String();
    }
    */

    public CyhUpload(HttpServletRequest httpServletRequest , HttpServletResponse httpServletResponse){
        m_httpServletRequest = httpServletRequest;
        m_httpServletResponse = httpServletResponse;
        m_border = new String();
        m_filename=new String();
        m_border=new String();
        m_suffix=new String();
        m_dataHeader = new String();
    }


    //上传文件
    public CyhFile upload(String path)throws CyhUploadException{
       if( m_httpServletRequest.getContentLength() > 0){
            InputStream inputStream;
            OutputStream outputStream;
            m_totalBytes = m_httpServletRequest.getContentLength();   //获取内容总大小
            m_byteArray = new byte[m_totalBytes];
            try{

                inputStream = m_httpServletRequest.getInputStream();


                File  file = new File("G:/",geneUniqueName(".txt") );
                file.createNewFile();
                outputStream = new FileOutputStream(file);
                byte buffer[] = new byte[1024];      //设置文件上传缓冲1024个字节--1KB,也就是说集齐1024个字节后才往硬盘中写入
                int len = 0;
                while((len=inputStream.read(buffer)) > 0){  //返回值len为实际读到的字节数
                    outputStream.write(buffer,0,len);
                }
                inputStream.close();
                outputStream.flush();
                outputStream.close();
            }catch(IOException e){
               throw new CyhUploadException("File load fail !");
            }
       }
        return null;
    }


    /*
    //上传文件
    public CyhFile upload(String path)throws CyhUploadException{
        if( m_httpServletRequest.getContentLength() > 0){
            InputStream inputStream;
            m_totalBytes = m_httpServletRequest.getContentLength();   //获取内容总大小
            m_byteArray = new byte[m_totalBytes];
            try{
                inputStream = m_httpServletRequest.getInputStream();
                int j;
                for(int i =0;i < m_totalBytes;i += j){
                    j = inputStream.read(m_byteArray,i,m_totalBytes - i);  //尽可能多地读入
                }

                for(boolean flag = false; !flag && m_currentIndex < m_totalBytes;m_currentIndex++){
                    if(m_byteArray[m_currentIndex] == 13) {                                   //为回车符13 == CR
                        flag = true;
                      //  m_border = new String(m_byteArray,0,m_currentIndex,"utf-8");
                    }else{
                        m_border += (char)m_byteArray[m_currentIndex];                 //  变成char之后便于使用charAt进行查找，后面会重新变回字节
                    }
                }
                if(m_currentIndex <= 1)   //没有数据，至少应该有两个字节
                    return null;

             //   for(m_currentIndex++;m_currentIndex < m_totalBytes;m_currentIndex+=2){  //两个字节对应一个char,这里针对多个文件
                   m_currentIndex++;
                   m_dataHeader =  getDataHeader();
                   boolean flag  = m_dataHeader.indexOf("filename") > 0;
                   String filename = getDataFieldValue("filename");
                   String  extensionName = getFileExt(filename);
                   String uniqueName =  geneUniqueName(extensionName);

                    CyhFile cyhFile = null;
                    getDataSection();
                   if(flag && save(path,uniqueName)){

                      cyhFile = new CyhFile();
                      cyhFile.setFieldName(getDataFieldValue("name"));
                      cyhFile.setFileName(filename);
                      cyhFile.setExtensionName(extensionName);
                      cyhFile.setContenetType(getContentType());
                      //cyhFile.setFileSize();                           //还没写
                      cyhFile.setUniqueName(uniqueName);
                      cyhFile.setFileSize((m_endData - m_startData) + 1);

                   }else{
                       throw new CyhUploadException("上传文件失败！ 请重试！");
                   }

              //  }

                inputStream.close();
                return cyhFile;

            }catch(UnsupportedEncodingException e){
                throw new CyhUploadException(" UnsupportedEncoding utf-8!");
            }catch(IOException e){
                throw new CyhUploadException("File load fail !");
            }
        }
        return null;
    }
    */


    //Header中的ContentType
    public String getContentType(){
        String s = "Content-Type: ";
        int i = m_dataHeader.indexOf(s);
        if(i >= 0){
           return  m_dataHeader.substring(i+s.length(),m_dataHeader.length() - 1);
        }
        return null;
    }

    //获取头部某一项的名字
    private String getDataFieldValue(String s1) {
        String s2 = new String();
        String s3 = new String();
        int i = 0;
        s2 = s1 + "=" + '"';  // =""
        i = m_dataHeader.indexOf(s2);
        if (i > 0) {
            int j = i + s2.length();  //
            int k = j;
            s2 = "\"";        //双引号
            int l = m_dataHeader.indexOf(s2, j);   //从j开始（包括j进行搜索）s中的第几位，这里是第一个引号的后一位
            if (k > 0 && l > 0)
                s3 = m_dataHeader.substring(k, l);  //截取域名 如 filename="sdsd.txt",subString中的endIndex参数表示想要截取到的最后一个字符的后一位，这样endIndex-beginIndex,就等于截取的字符个数了
        }
        return s3;
    }

    //获取单个分界线下的数据头
    private String getDataHeader(){
        int begin = m_currentIndex;
        int len = 0;
        for(boolean st = false; !st; ){
            if(m_byteArray[m_currentIndex] == 13 && m_byteArray[m_currentIndex + 2] == 13){  // \r  = 13   &&  \n = 10
                st = true;
               len = m_currentIndex - begin;
               m_currentIndex += 4;                  //直接到文件内容的第一项
            }else{
                m_currentIndex++;    //在这里是因为最后一次不想执行++;
            }
        }
        try {
            String dataHeader = new String(m_byteArray,begin,len,"utf-8");  //以utf-8的方式解码
            return dataHeader;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    //保存文件到指定位置
    public boolean save(String path ,String uniqueName){
        try {
            if (path == null)
                m_httpServletRequest.getSession().getServletContext().getRealPath("/");
          //  path = m_application.getRealPath("/");              //不推荐使用
            if (path.indexOf("/") != -1) {
                if (path.charAt(path.length() - 1) != '/')
                    path += "/";
            } else if (path.charAt(path.length() - 1) != '\\')
                path = path + "\\";

            if (path == null)
                throw new IllegalArgumentException(
                        "There is no specified destination file (1140).");

            File file = new File(path+uniqueName);
            FileOutputStream fileoutputstream = new FileOutputStream(file);
            fileoutputstream.write(m_byteArray, m_startData, (m_endData - m_startData) + 1);
            fileoutputstream.close();
            return true;
        }catch(IOException e) {
          e.printStackTrace();
          return false;
        }

    }


    //获取文件扩展名
    private String getFileExt(String s) {
        String s1 = new String();
        int i = 0;
        int j = 0;
        if (s == null)
            return null;
        i = s.lastIndexOf(46) + 1;    // 46为'.',最后一次出现的位置
        j = s.length();                   //
        s1 = s.substring(i, j);
        if (s.lastIndexOf(46) > 0)
            return s1;
        else
            return "";
    }



    //判断是普通表单还是带文件上传的表单，带文件上传必须使用form-data
    public static final boolean isMultipartContent(HttpServletRequest request) {
        if (!POST_METHOD.equalsIgnoreCase(request.getMethod())) { return false; }
        if (request.getContentType() == null) { return false; }
        if (request.getContentType().toLowerCase(Locale.ENGLISH).startsWith(MULTIPART)) {return true;}
        return false;
    }

    private void getDataSection() {
        boolean flag = false;
        String s = new String();
        int i = m_currentIndex;
        int j = 0;
        int k = m_border.length();
        m_startData = m_currentIndex;
        m_endData = 0;
        while (i < m_totalBytes)
            if (m_byteArray[i] == (byte) m_border.charAt(j)) {
                if (j == k - 1) {
                    m_endData = ((i - k) + 1) - 3;
                    break;
                }
                i++;
                j++;
            } else {
                i++;
                j = 0;
            }
        m_currentIndex = m_endData + k + 3;
    }

    //产生唯一文件名
    public String geneUniqueName(String suffix){
        return UUID.randomUUID()+"."+suffix;
    }

}


