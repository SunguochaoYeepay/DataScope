package com.datascope.infrastructure.util;

import java.util.concurrent.Callable;
import java.util.function.Predicate;
import lombok.extern.slf4j.Slf4j;

/** 重试工具类 */
@Slf4j
public class RetryUtil {

  /**
   * 执行带重试的操作
   *
   * @param operation 要执行的操作
   * @param maxAttempts 最大重试次数
   * @param retryOnException 判断是否需要重试的异常条件
   * @param delayMs 重试间隔(毫秒)
   * @param <T> 返回值类型
   * @return 操作结果
   * @throws Exception 如果重试后仍然失败
   */
  public static <T> T retry(
      Callable<T> operation, int maxAttempts, Predicate<Exception> retryOnException, long delayMs)
      throws Exception {

    int attempts = 0;
    Exception lastException = null;

    while (attempts < maxAttempts) {
      try {
        if (attempts > 0) {
          log.info("Retrying operation, attempt {}/{}", attempts + 1, maxAttempts);
          Thread.sleep(delayMs);
        }
        return operation.call();
      } catch (Exception e) {
        lastException = e;
        attempts++;

        if (!retryOnException.test(e) || attempts >= maxAttempts) {
          log.error("Operation failed after {} attempts", attempts, e);
          throw e;
        }

        log.warn(
            "Operation failed (attempt {}/{}), will retry in {} ms: {}",
            attempts,
            maxAttempts,
            delayMs,
            e.getMessage());
      }
    }

    throw new RuntimeException(
        "Operation failed after " + maxAttempts + " attempts", lastException);
  }

  /**
   * 执行带重试的操作(使用默认参数)
   *
   * @param operation 要执行的操作
   * @param <T> 返回值类型
   * @return 操作结果
   * @throws Exception 如果重试后仍然失败
   */
  public static <T> T retryWithDefaults(Callable<T> operation) throws Exception {
    return retry(
        operation,
        3, // 默认重试3次
        e -> true, // 默认对所有异常进行重试
        1000 // 默认延迟1秒
        );
  }
}
