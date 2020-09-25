package io.wannabit.wallet.exception;

public class LogicException extends RuntimeException {

  private static final long serialVersionUID = 1L;
  private LogicErrorList errorCode;

  public static LogicException wrap(Throwable exception, LogicErrorList errorCode) {
    if (exception instanceof LogicException) {
      LogicException se = (LogicException) exception;
      if (errorCode != null && errorCode != se.getErrorCode()) {
        return new LogicException(exception.getMessage(), exception, errorCode);
      }
      return se;
    } else {
      return new LogicException(exception.getMessage(), exception, errorCode);
    }
  }

  public static LogicException wrap(Throwable exception) {
    return wrap(exception, null);
  }

  public LogicException(LogicErrorList errorCode) {
    this.errorCode = errorCode;
  }

  public LogicException(String message, LogicErrorList errorCode) {
    super(message);
    this.errorCode = errorCode;
  }

  public LogicException(Throwable cause, LogicErrorList errorCode) {
    super(cause);
    this.errorCode = errorCode;
  }

  public LogicException(String message, Throwable cause, LogicErrorList errorCode) {
    super(message, cause);
    this.errorCode = errorCode;
  }

  public LogicErrorList getErrorCode() {
    return errorCode;
  }

  public LogicException setErrorCode(LogicErrorList errorCode) {
    this.errorCode = errorCode;
    return this;
  }

}
