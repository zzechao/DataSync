package com.zhouz.datasync


/**
 * @author:zhouz
 * @date: 2024/4/2 15:51
 * description：异常处理类
 */
fun <T> tryCatch(
    catchBlock: (Throwable) -> Unit = { t -> DataWatcher.logger.i("tryCatchLogcat print:", t) },
    tryBlock: () -> T
): T? {
    try {
        return tryBlock()
    } catch (t: Throwable) {
        catchBlock(t)
    }
    return null
}

suspend fun <T> tryCatchSuspend(
    catchBlock: (Throwable) -> Unit = { t -> DataWatcher.logger.i("tryCatchLogcat print:", t) },
    tryBlock: suspend () -> T
): T? {
    try {
        return tryBlock()
    } catch (t: Throwable) {
        catchBlock(t)
    }
    return null
}