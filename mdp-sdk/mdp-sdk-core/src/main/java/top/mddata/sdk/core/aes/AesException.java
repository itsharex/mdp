package top.mddata.sdk.core.aes;


/**
 * AES 加解密相关异常，带错误码。
 */
public class AesException extends Exception {

    public static final int OK = 0;
    public static final int VALIDATE_SIGNATURE_ERROR = -40001;
    public static final int PARSE_JSON_ERROR = -40002;
    public static final int COMPUTE_SIGNATURE_ERROR = -40003;
    public static final int ILLEGAL_AES_KEY = -40004;
    public static final int VALIDATE_APPID_ERROR = -40005;
    public static final int ENCRYPT_AES_ERROR = -40006;
    public static final int DECRYPT_AES_ERROR = -40007;
    public static final int ILLEGAL_BUFFER = -40008;

    private static String getMessage(int code) {
        return switch (code) {
            case VALIDATE_SIGNATURE_ERROR -> "签名验证错误";
            case PARSE_JSON_ERROR -> "json解析失败";
            case COMPUTE_SIGNATURE_ERROR -> "sha加密生成签名失败";
            case ILLEGAL_AES_KEY -> "SymmetricKey非法";
            case VALIDATE_APPID_ERROR -> "appid校验失败";
            case ENCRYPT_AES_ERROR -> "aes加密失败";
            case DECRYPT_AES_ERROR -> "aes解密失败";
            case ILLEGAL_BUFFER -> "解密后得到的buffer非法";
            default -> null;
        };
    }

    private final int code;

    public AesException(int code) {
        super(getMessage(code));
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
