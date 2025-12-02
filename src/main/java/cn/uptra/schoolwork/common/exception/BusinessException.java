package cn.uptra.schoolwork.common.exception;

import cn.uptra.schoolwork.common.result.ResultCode;

public class BusinessException extends RuntimeException {
    private final ResultCode resultCode;
    private final String message;

    public BusinessException(ResultCode resultCode, String message) {
        super(message);
        this.resultCode = resultCode;
        this.message = message;
    }

    public BusinessException(String message) {
        super(message);
        this.resultCode = ResultCode.FAILED;
        this.message = message;
    }

    public ResultCode getResultCode() {
        return resultCode;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
