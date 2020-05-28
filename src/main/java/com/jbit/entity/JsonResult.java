package com.jbit.entity;

import java.util.HashMap;

/**
 * json返回对象
 */
public class JsonResult extends HashMap<String,Object> {
    public JsonResult message(String message){
        this.put("message",message);
        return  this;
    }
    public JsonResult(boolean success){
        this.put("success",success);
    }
}
