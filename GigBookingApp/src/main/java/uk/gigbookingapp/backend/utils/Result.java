package uk.gigbookingapp.backend.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Result {
    private Boolean success;
    private Integer code; // State code
    private String message;
    private Map<String, Object> data = new HashMap<>();

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public Result setMessage(String message) {
        this.message = message;
        return this;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public Result data(Map<String, Object> data) {
        this.data = data;
        return this;
    }

    public Result data(String key, Object value) {
        this.data.put(key, value);
        return this;
    }

    private Result(){}

    public static Result ok(){
        Result r = new Result();
        r.setSuccess(true);
        r.setCode(ResultCode.SUCCESS);
        r.setMessage("Success");
        return r;
    }

    public static Result error(){
        Result r = new Result();
        r.setSuccess(false);
        r.setCode(ResultCode.ERROR);
        r.setMessage("Error");
        return r;
    }

    public static boolean error(HttpServletResponse response) throws IOException {
        response.setContentType("application/JSON");
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(Result.error());
        response.getWriter().write(json);
        return false;
    }
    public static boolean error(HttpServletResponse response, String message) throws IOException {
        response.setContentType("application/JSON");
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(Result.error().setMessage(message));
        response.getWriter().write(json);
        return false;
    }
}
