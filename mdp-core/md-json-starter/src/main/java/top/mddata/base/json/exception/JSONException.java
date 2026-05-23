package top.mddata.base.json.exception;


import top.mddata.base.exception.BaseUncheckedException;
import top.mddata.base.exception.code.ExceptionCode;

import java.io.Serial;

/**
 * JSON 异常
 *
 * @author henhen
 * @since 2026年05月22日
 */
public class JSONException extends BaseUncheckedException {

    @Serial
    private static final long serialVersionUID = 1L;

    public JSONException(String message) {
        super(ExceptionCode.JSON_PARSE_ERROR.getCode(), message);
    }

    public JSONException(Throwable cause) {
        super(ExceptionCode.JSON_PARSE_ERROR.getCode(), ExceptionCode.JSON_PARSE_ERROR.getMsg(), cause);
    }

    public JSONException(String message, Throwable cause) {
        super(ExceptionCode.JSON_PARSE_ERROR.getCode(), message, cause);
    }
}
