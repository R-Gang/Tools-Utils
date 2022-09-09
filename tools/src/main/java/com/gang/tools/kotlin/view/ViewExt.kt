package com.gang.tools.kotlin.view

import android.app.Activity
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.view.MarginLayoutParamsCompat
import androidx.core.view.children
import com.gang.tools.kotlin.dimension.statusBarHeight
import com.gang.tools.kotlin.utils.LogUtils
import com.gang.tools.kotlin.utils.getColor

/**
 * View相关扩展：
 *
 * 创建者: hrg
 * 创建时间: 2020/6/8 10:35 AM
 */

inline var View.widthValue: Int
    get() = width
    set(value) {
        layoutParams = layoutParams.apply {
            width = value
        }
    }

inline var View.heightValue: Int
    get() = height
    set(value) {
        layoutParams = layoutParams.apply {
            height = value
        }
    }

inline var View.marginLeft: Int
    get() = (layoutParams as? ViewGroup.MarginLayoutParams)?.leftMargin ?: 0
    set(value) {
        layoutParams = (layoutParams as? ViewGroup.MarginLayoutParams)?.apply {
            leftMargin = value
        }
    }

inline var View.marginTop: Int
    get() = (layoutParams as? ViewGroup.MarginLayoutParams)?.topMargin ?: 0
    set(value) {
        layoutParams = (layoutParams as? ViewGroup.MarginLayoutParams)?.apply {
            topMargin = value
        }
    }

inline var View.marginRight: Int
    get() = (layoutParams as? ViewGroup.MarginLayoutParams)?.rightMargin ?: 0
    set(value) {
        layoutParams = (layoutParams as? ViewGroup.MarginLayoutParams)?.apply {
            rightMargin = value
        }
    }


inline var View.marginBottom: Int
    get() = (layoutParams as? ViewGroup.MarginLayoutParams)?.bottomMargin ?: 0
    set(value) {
        layoutParams = (layoutParams as? ViewGroup.MarginLayoutParams)?.apply {
            bottomMargin = value
        }
    }

fun View.setMargin(rect: Rect) {
    layoutParams = (layoutParams as? ViewGroup.MarginLayoutParams)?.apply {
        setMargins(rect.left, rect.top, rect.right, rect.bottom)
    }
}

/**
 * @see ViewGroup.MarginLayoutParams
 * @see MarginLayoutParamsCompat.getMarginStart
 */
inline var View.marginStart: Int
    get() {
        val lp = layoutParams
        return if (lp is ViewGroup.MarginLayoutParams) MarginLayoutParamsCompat.getMarginStart(lp) else 0
    }
    set(value) {
        val lp = layoutParams
        layoutParams = (lp as? ViewGroup.MarginLayoutParams)?.apply {
            MarginLayoutParamsCompat.setMarginStart(lp, value)
        }
    }

/**
 * @see ViewGroup.MarginLayoutParams
 * @see MarginLayoutParamsCompat.getMarginEnd
 */
inline var View.marginEnd: Int
    get() {
        val lp = layoutParams
        return if (lp is ViewGroup.MarginLayoutParams) MarginLayoutParamsCompat.getMarginEnd(lp) else 0
    }
    set(value) {
        val lp = layoutParams
        layoutParams = (lp as? ViewGroup.MarginLayoutParams)?.apply {
            MarginLayoutParamsCompat.setMarginEnd(lp, value)
        }
    }

/**
 * view 转 Bitmap
 */
fun View.toBitmap(): Bitmap {
    val dstHeight = if (this is ScrollView) {
        contentHeight()
    } else {
        height
    }
    val bitmap = Bitmap.createBitmap(width, dstHeight, Bitmap.Config.RGB_565)
    val canvas = Canvas(bitmap)
    canvas.save()
    draw(canvas)
    canvas.restore()
    return bitmap
}

/**
 * 获取 [ScrollView] 内子View的绘制总高度，包括屏幕外不可见的部分
 */
fun ScrollView.contentHeight(): Int {
    var contentHeight = 0
    children.forEach {
        contentHeight += it.height
    }
    return contentHeight
}

/**
 * TextView设置文本颜色和文字，入参为color资源ID
 *
 */
fun TextView.setTextColorRes(@ColorRes resId: Int): TextView {
    setTextColor(getColor(resId))
    return this
}

/**
 * TextView设置文本颜色和文字，入参为color资源ID
 */
fun View.setTextStyle(content: String, @ColorRes resId: Int): View {
    if (this is TextView) {
        setTextColor(getColor(resId))
        text = content
    }
    return this
}

/**
 * TextView设置设置四个方向的 [Drawable]
 * [padding] compoundDrawablePadding 单位px
 */
fun TextView.setCompoundDrawablesAndPadding(
    @DrawableRes leftResId: Int = 0,
    @DrawableRes topResId: Int = 0,
    @DrawableRes rightResId: Int = 0,
    @DrawableRes bottomResId: Int = 0,
    padding: Int = 0,
): TextView {
    setCompoundDrawablesWithIntrinsicBounds(
        leftResId, topResId, rightResId, bottomResId
    )
    compoundDrawablePadding = if (padding > 0) padding else 0
    return this
}


fun View?.isLocationOnView(x: Float, y: Float): Boolean {
    return this?.run {
        val location = IntArray(2)
        getLocationOnScreen(location)
        LogUtils.e("ViewExt", "screen ${location[0]}, ${location[1]} (x, y) -> ($x, $y)")
        (x > location[0] && (x < location[0] + width)
                && y > location[1] && y < location[1] + height)
    } ?: false
}

/***
 * 滚动View
 * [increment] 滚动增量。为负值时，反向。
 */
fun ScrollView?.scroll(increment: Int) {
    this?.apply {
        if (childCount > 0) {
            val scrollY = scrollY
            val afterY = scrollY + increment
            val limitHeight = getChildAt(0).height - height
            if (increment > 0) {
                // 向下滚动
                val offsetY = if (afterY <= limitHeight) {
                    afterY
                } else {
                    limitHeight
                }
                smoothScrollTo(0, offsetY)
            } else if (increment < 0) {
                // 向上滚动
                val offsetY = if (afterY >= 0) {
                    afterY
                } else {
                    0
                }
                smoothScrollTo(0, offsetY)
            }
            LogUtils.d("ViewExt",
                "ScrollView >>> scrollY = $scrollY :: increment = $increment :: limitHeight = $limitHeight")
        }
    }
}

/**
 * 判断View是否与目标rect部分重叠
 */
fun View?.isInRect(rect: Rect): Boolean {
    return this?.run {
        (top in rect.top..rect.bottom || bottom in rect.top..rect.bottom)
                && (left in rect.left..rect.right || right in rect.left..rect.right)
    } ?: false
}

/**
 * 从父视图删除View
 */
fun View?.removeFromParent() {
    (this?.parent as? ViewGroup)?.removeView(this)
}

/**
 * 获取文本视图一行文本的高度
 */
fun TextView.getTextHeight(): Int {
    val metricsInt = paint.fontMetricsInt
//    "Text :: textSize = $textSize :: metricsInt $metricsInt".i()
    return metricsInt.descent - metricsInt.ascent
}

/**
 * 获取文本视图指定行数的高度
 */
fun TextView.getTextHeightWithLines(lines: Int = 1): Int {
    return if (lines > 0) {
        paddingTop + lineHeight * lines + paddingBottom
    } else {
        paddingTop + paddingBottom
    }
}

/**
 * 兼容 v7 包获取 Activity
 */
fun View?.getActivity(): Activity? {
    return this?.context?.run {
        var context = this
        var activity: Activity? = null
        if (context is ContextWrapper) {
            if (context is Activity) {
                activity = context
            } else {
                context = context.baseContext
                if (context is Activity) {
                    activity = context
                }
            }
        }
        activity
    }
}

/**
 * 设置状态栏高度的上边距
 */
fun View?.marginTopWithStatusBar() {
    this?.marginTop = statusBarHeight
}
