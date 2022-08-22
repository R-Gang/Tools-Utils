package com.gang.tools.kotlin.utils

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.TextView
import androidx.annotation.ArrayRes
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.gang.tools.kotlin.Config
import com.gang.tools.kotlin.interfaces.Setter
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.UnsupportedEncodingException

/**
 * @ClassName:      ResourcesUtils
 * @Description:    获取资源
 * @Author:         haoruigang
 * @CreateDate:     2020/8/3 17:28
 */

fun getColor(@ColorRes resId: Int): Int? = mToolsContext?.getColor(resId)

/**
 * 获取字符串
 *
 * @param id
 * @param obj
 * @return
 */
fun getStringFormat(@StringRes id: Int, obj: Array<Any?>): String {
    mToolsContext?.apply {
        val string: String = resources.getString(id)
        return if (obj.isNotEmpty()) String.format(string, *obj) else string
    }
    return ""
}

fun getStrings(@ArrayRes id: Int): Array<out String>? {
    return mToolsContext?.resources?.getStringArray(id)
}

fun getString(@StringRes resId: Int): String? = mToolsContext?.getString(resId)
fun getString(@StringRes resId: Int, vararg params: Any): String? =
    mToolsContext?.getString(resId, *params)

fun getStringOrNull(@StringRes resId: Int?, vararg params: Any): CharSequence? {
    return when {
        resId == null -> {
            null
        }
        params.isNotEmpty() -> {
            mToolsContext?.getString(resId, *params)
        }
        else -> {
            mToolsContext?.getString(resId)
        }
    }
}

fun getStringOrDefault(
    @StringRes resId: Int?,
    def: CharSequence,
    vararg params: Any,
): CharSequence? {
    return when {
        resId == null -> {
            def
        }
        params.isNotEmpty() -> {
            mToolsContext?.getString(resId, *params)
        }
        else -> {
            mToolsContext?.getString(resId)
        }
    }
}


fun getDrawable(@DrawableRes resId: Int): Context? =
    mToolsContext?.apply {
        ContextCompat.getDrawable(this, resId)
    }

fun Context.getCompatDrawable(@DrawableRes id: Int?): Drawable? =
    id?.let { ContextCompat.getDrawable(this, it) }

fun getDrawableOrNull(@DrawableRes resId: Int?): Drawable? {
    return when (resId) {
        null -> {
            null
        }
        else -> {
            mToolsContext?.getCompatDrawable(resId)
        }
    }
}

fun getCompoundDrawableOrNull(@DrawableRes resId: Int?): Drawable? {
    return when (resId) {
        null -> {
            null
        }
        else -> {
            mToolsContext?.getCompoundDrawable(resId)
        }
    }
}

/**
 * 获取 CompoundDrawable 用于 [androidx.appcompat.widget.AppCompatTextView.setCompoundDrawables]
 */
fun Context.getCompoundDrawable(
    @DrawableRes id: Int?,
    width: Int? = null,
    height: Int? = null,
): Drawable? =
    id?.let {
        ContextCompat.getDrawable(this, it)?.apply {
            setBounds(0, 0, width ?: minimumWidth, height ?: minimumHeight)
        }
    }

fun Fragment.getCompoundDrawable(
    @DrawableRes id: Int?,
    width: Int? = null,
    height: Int? = null,
): Drawable? =
    context?.getCompoundDrawable(id, width, height)


/**
 * 文字中添加图片
 *
 * @param textView
 * @param imgResId
 * @param index
 * @param padding
 */
fun setTvaddDrawable(
    textView: TextView, @DrawableRes imgResId: Int,
    index: Int,
    padding: Int,
) {
    if (imgResId == -1) {
        textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
    } else {
        textView.setCompoundDrawablesWithIntrinsicBounds(
            if (index == 1) imgResId else 0,
            if (index == 2) imgResId else 0,
            if (index == 3) imgResId else 0,
            if (index == 4) imgResId else 0
        )
        textView.compoundDrawablePadding = padding
    }
}

/**
 * 获取assets下的文件
 */
fun getAssetFile(url: String): String {
    return "file:///android_asset/$url"
}

/**
 * 读取assets中的txt文件
 *
 * @return
 */
fun readAssetsText(context: Context, fileName: String?): String {
    val sb = StringBuffer("")
    try {
        val inputStream = context.assets.open(fileName!!)
        var inputStreamReader: InputStreamReader? = null
        try {
            inputStreamReader = InputStreamReader(inputStream, "UTF-8")
        } catch (e1: UnsupportedEncodingException) {
            LogUtils.tag("readAssetsText=$e1")
        }
        val reader = BufferedReader(inputStreamReader)
        var line: String?
        try {
            while (reader.readLine().also { line = it } != null) {
                sb.append(line)
                sb.append("\n")
            }
        } catch (e: IOException) {
            LogUtils.tag("readAssetsText=$e")
        }
    } catch (e1: IOException) {
    }
    return sb.toString()
}

//全局字体
val typefaceAll: Typeface by lazy {
    Typeface.createFromAsset(mToolsContext?.assets,
        Config.typefaceAll)
}

fun <T : View?, V> applyV(
    list: List<T>,
    setter: Setter<in T, V>, value: V,
) {
    var i = 0
    val count = list.size
    while (i < count) {
        setter[list[i], value] = i
        i++
    }
}