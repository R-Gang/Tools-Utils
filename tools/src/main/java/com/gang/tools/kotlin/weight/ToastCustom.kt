package com.gang.tools.kotlin.weight

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.gang.tools.R


/**
 *
 * @Description:     java类作用描述
 * @Author:         haoruigang
 * @CreateDate:     2021/9/1 12:09
 */
class ToastCustom(context: Context?) : Toast(context) {

    var mContext: Context? = context

    fun view(): View {
        //获取系统的LayoutInflater
        val inflater = mContext?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        return inflater.inflate(R.layout.toast_layout, null)
    }

    fun showToast(content: String?) {
        showToast(content, view())
    }

    fun showToast(
        content: String?,
        view: View,
        duration: Int = LENGTH_SHORT,
        gravity: Int = Gravity.CENTER,
        xOffset: Int = 0,
        yOffset: Int = 0,
    ) {
        val tv_content = view.findViewById<TextView>(R.id.tv_content)
        tv_content?.text = content

        //实例化toast
        mToast = Toast(mContext)
        mToast?.view = view
        mToast?.duration = duration
        mToast?.setGravity(gravity, xOffset, yOffset)
        mToast?.show()
    }

    companion object {
        var mToast: Toast? = null
    }
}