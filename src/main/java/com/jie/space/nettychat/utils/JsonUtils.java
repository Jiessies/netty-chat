package com.jie.space.nettychat.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by huangmingjie on 2017/10/17.
 */
@Slf4j
public class JsonUtils {
    private JsonUtils() {
    
    }
    
    /**
     * Jackson Object转JsonString.
     */
    public static String jacksonString(Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            //log.error("--> JsonProcessingException[AnalysisLogAspect - toJsonString]", object);
            return "JsonProcessingException[AnalysisLogAspect - toJsonString]";
        }
    }
}
