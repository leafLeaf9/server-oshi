package oshi.framework.rest.server;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ServerResult implements Serializable {
    public static final int ERROR = 1;
    private static final int OK = 0;
    private int status;

    /**
     * 错误码
     */
    private int code;

    /**
     * 信息
     */
    private String message;

    /**
     * 结果
     */
    private Object data;

    public static ServerResult success() {
        return new ServerResult(OK, HttpStatus.OK.value(), "OK", null);
    }

    public static ServerResult success(String message) {
        return new ServerResult(OK, HttpStatus.OK.value(), message, null);
    }

    public static ServerResult success(Object data) {
        return new ServerResult(OK, HttpStatus.OK.value(), "OK", data);
    }

    public static ServerResult error() {
        return new ServerResult(ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), null);
    }

    public static ServerResult error(String message) {
        return new ServerResult(ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(), message, null);
    }

    public static ServerResult error(int code, String message) {
        return new ServerResult(ERROR, code, message, null);
    }

    public static ServerResult error(String message, Object data) {
        return new ServerResult(ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(), message, data);
    }

    public static ServerResult error(Object data) {
        return new ServerResult(ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), data);
    }
}
