package com.zhouz.datasync;

import org.jetbrains.annotations.NotNull;

/**
 * @author:zhouz
 * @date: 2024/4/2 19:49
 * description：规避 kotlin type mismatch. required nothing found T
 */
public class DataDifferUtil {

    public static <T extends IDataEvent> boolean checkData(@NotNull DataDiffer<? extends IDataEvent> it, T data) {
        IDataDiffer<T> differ = (IDataDiffer<T>) it.getDiffer();
        if (it.getCurData() != null && differ != null) {
            if (differ.areDataSame((T) it.getCurData(), data)) {
                return differ.isContentChange((T) it.getCurData(), data);
            }
        } else {
            return false;
        }
        return true;
    }
}
