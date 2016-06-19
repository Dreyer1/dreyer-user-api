package com.dreyer.web.common.exception;

import com.alibaba.dubbo.rpc.RpcException;
import com.dreyer.common.exception.BizException;
import com.dreyer.common.web.result.ApiResult;
import com.dreyer.common.web.result.ApiResultCodeMsg;
import com.dreyer.common.web.result.ApiResultGenerator;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * @author Dreyer
 * @description 系统全局异常的处理器，可以根据不同的异常类型，定制处理异常的方式
 * @date 2015年9月1日 下午10:53:54
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    private Logger logger = Logger.getLogger(GlobalExceptionHandler.class);

    /**
     * 业务异常处理
     *
     * @param bizException
     * @return
     */
    @ExceptionHandler({BizException.class})
    @ResponseBody
    public ApiResult bizExceptionHandler(BizException bizException) {
        logger.error("Exception", bizException);

        return ApiResultGenerator.generate(bizException.getCode(), bizException.getMsg());
    }

    /**
     * Dubbo远程服务调用异常
     *
     * @param rpcException
     * @return
     */
    @ExceptionHandler({RpcException.class})
    @ResponseBody
    public ApiResult rpcExceptionHandler(RpcException rpcException) {
        logger.error("RpcException", rpcException);

        return ApiResultGenerator.generate(ApiResultCodeMsg.SYSTEM_ERROR.getCode(), "服务异常!");
    }

    /**
     * 总异常处理
     *
     * @param exception
     * @return
     */
    @ExceptionHandler({Exception.class})
    @ResponseBody
    public ApiResult exceptionHandler(Exception exception) {
        logger.error("Exception", exception);

        return ApiResultGenerator.generate(ApiResultCodeMsg.SYSTEM_ERROR.getCode(), "系统繁忙!");
    }


}
