package com.gang.tools.kotlin.interfaces

import android.view.View
import androidx.annotation.UiThread

/**
 *
 * @ProjectName:    gang
 * @Package:        com.gang.tools.kotlin.interfaces
 * @ClassName:      Setter
 * @Description:     java类作用描述
 * @Author:         haoruigang
 * @CreateDate:     2020/8/4 18:30
 */
/** A setter that can apply a value to a list of views.  */
interface Setter<T : View?, V> {
    /** Set the `value` on the `view` which is at `index` in the list.  */
    @UiThread
    operator fun set(view: T, value: V?, index: Int)
}