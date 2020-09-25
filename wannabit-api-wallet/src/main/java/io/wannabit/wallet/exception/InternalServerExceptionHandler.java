package io.wannabit.wallet.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class InternalServerExceptionHandler {
  @ExceptionHandler(LogicException.class)
  public ResponseEntity<InternalServerExceptionVo> internalServerException(LogicException ex) {
    InternalServerExceptionVo vo = new InternalServerExceptionVo();
    vo.setErrorCode(ex.getErrorCode().getErrorCode());
    vo.setErrorMsg(ex.getErrorCode().getErrorMsg());
    return new ResponseEntity<InternalServerExceptionVo>(vo, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
