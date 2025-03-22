/**
 * 创建节流函数
 * @param fn 需要节流的函数
 * @param wait 等待时间
 */
export function throttle<T extends (...args: any[]) => any>(
  fn: T,
  wait: number
): (...args: Parameters<T>) => Promise<ReturnType<T>> {
  let lastTime = 0;
  let timer: NodeJS.Timeout | null = null;

  return function (...args: Parameters<T>): Promise<ReturnType<T>> {
    return new Promise((resolve, reject) => {
      const now = Date.now();

      if (now - lastTime >= wait) {
        if (timer) {
          clearTimeout(timer);
          timer = null;
        }
        try {
          const result = fn.apply(this, args);
          lastTime = now;
          resolve(result);
        } catch (error) {
          reject(error);
        }
      } else {
        if (timer) {
          clearTimeout(timer);
        }
        timer = setTimeout(() => {
          try {
            const result = fn.apply(this, args);
            lastTime = Date.now();
            resolve(result);
          } catch (error) {
            reject(error);
          }
        }, wait - (now - lastTime));
      }
    });
  };
}