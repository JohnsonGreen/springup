package com.ppx;

import com.google.gson.JsonObject;

/**
 * Created by cyh on 2017/6/25.
 */
public class Gsontest {

	  public static void main(String[] args)
{
        JsonObject jo = new JsonObject();
        jo.addProperty("sdfsd",true);
        JsonObject js = jo.getAsJsonObject("sdfsdfg");
        System.out.println(js);

}
	  
}
