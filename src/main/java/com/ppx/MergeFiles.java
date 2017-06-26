package com.ppx;

import static java.lang.System.out;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;

public class MergeFiles {

	 public static final int BUFSIZE = 1024 * 1024 * 5;
	  
	 public static void mergeFiles(String outFile, File[] files) {
	  FileChannel outChannel = null;
	  out.println("Merge " + Arrays.toString(files) + " into " + outFile);
	  try {
	   outChannel = new FileOutputStream(outFile).getChannel();
	   for(File f : files){
		 String absolutePath = f.getAbsolutePath();
	  
		FileChannel fc = new FileInputStream(absolutePath).getChannel(); 
	    ByteBuffer bb = ByteBuffer.allocate(BUFSIZE);
	    while(fc.read(bb) != -1){
	     bb.flip();
	     outChannel.write(bb);
	     bb.clear();
	    }
	    fc.close();
	   }
	   out.println("Merged!! ");
	  } catch (IOException ioe) {
	   ioe.printStackTrace();
	  } finally {
	   try {if (outChannel != null) {outChannel.close();}} catch (IOException ignore) {}
	  }
	 }
	 
	 
	 //下面代码是将D盘的1.txt 2.txt 3.txt文件合并成out.txt文件。
	 public static void main(String[] args) {
		 // 文件上传后的路径
	     String filePath = "H:\\upload\\";
	     String fileMd5 = "42cc41cb61bbdd4e983150fd92e7c644";
	     String suffix = "mp4";
		 File exdir =  new File(filePath+fileMd5);
		 File[] fileArray = exdir.listFiles();
	     mergeFiles(filePath+fileMd5+"."+suffix, fileArray);
	 }

}
