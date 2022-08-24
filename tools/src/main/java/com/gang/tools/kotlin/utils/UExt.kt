package com.gang.tools.kotlin.utils

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Activity
import android.app.ActivityOptions
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.*
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.gang.tools.kotlin.ToolsConfig
import com.gang.tools.kotlin.ToolsConfig.TAG
import com.gang.tools.kotlin.ToolsConfig.toActivityRequestCode
import com.gang.tools.kotlin.weight.ToastCustom
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult
import java.io.*
import java.lang.reflect.Array
import java.nio.charset.Charset
import java.security.MessageDigest
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.experimental.and


/**
 * @ClassName:      U
 * @Description:    java类作用描述
 * @Author:         haoruigang
 * @CreateDate:     2020/8/3 17:30
 */


/**
 * 初始化工具类
 */
fun initToolsUtils(context: Context) {
    mToolsContext = context
}

/**
 * 获取mContext
 */
var mToolsContext: Context? = null

/**
 * 判断网络是否连接
 * 需加权限 ACCESS_NETWORK_STATE
 * @param context
 * @return
 */
fun isNetConnected(): Boolean {
    mToolsContext?.let {
        val connectivity: ConnectivityManager =
            it.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = connectivity.activeNetworkInfo
        if (null != info && info.isConnected) {
            return info.state == NetworkInfo.State.CONNECTED
        }
    }
    return false
}

/**
 * 判断是否是wifi连接
 */
fun isWifiConnected(): Boolean {
    val cm =
        mToolsContext?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    return cm.activeNetworkInfo?.type == ConnectivityManager.TYPE_WIFI
}

/**
 * 打开网络设置界面
 */
fun openNetSetting(activity: Activity) {
    val intent = Intent("/")
    val cm =
        ComponentName("com.android.settings", "com.android.settings.WirelessSettings")
    intent.component = cm
    intent.action = "android.intent.action.VIEW"
    activity.startActivityForResult(intent, 0)
}


private var toast: Toast? = null

/**
 * Toast
 *
 * @param msg
 */
fun showToast(msg: String?) {
    if (ToolsConfig.isShowLog) {
        showToast(msg, Toast.LENGTH_SHORT)
    }
}

/**
 * Toast
 *
 * @param msg
 * @param duration Toast.LENGTH_LONG
 */
fun showToast(msg: String?, duration: Int) {
    if (ToolsConfig.isShowLog) {
        if (toast == null) {
            toast = Toast.makeText(mToolsContext, msg, duration)
            toast?.setGravity(Gravity.CENTER, 0, 0)
        } else {
            toast?.setText(msg)
        }
        toast?.show()
    }
}

private var toastCustom: ToastCustom? = null

// 自定义Toast
fun toastCustom(content: String) {
    if (toastCustom == null) {
        toastCustom = ToastCustom(mToolsContext)
    }
    toastCustom?.showToast(content)
}

/**
 * 隐藏软键盘
 *
 * @param argEditText
 */
fun hideSoftKeyboard(argEditText: EditText) {
    val imm =
        (mToolsContext?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
    imm.hideSoftInputFromWindow(argEditText.windowToken, 0)
}

/***
 * 隐藏软键盘
 */
fun hideKeyboard(activity: Activity) {
    val inputManager =
        activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    val view = activity.currentFocus
    if (view != null) {
        inputManager.hideSoftInputFromWindow(
            view.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }
}

/**
 * 显示键盘
 *
 */
fun showKeyBoard(view: View) {
    val imm =
        mToolsContext?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    view.requestFocus()
    imm.showSoftInput(view, 0);
}

/**
 * map转为字符串
 *
 * @param map
 * @return
 */
fun transMap2String(map: Map<*, *>): String {
    var entry: Map.Entry<*, *>
    val sb = StringBuilder()
    val iterator: Iterator<*> = map.entries.iterator()
    while (iterator.hasNext()) {
        entry = iterator.next() as Map.Entry<*, *>
        sb.append(entry.key.toString()).append("=")
            .append(if (null == entry.value) "" else entry.value.toString())
            .append(if (iterator.hasNext()) "&" else "")
    }
    return sb.toString()
}

/**
 * 获取版本号信息
 */
fun getVersionName(): String? {
    mToolsContext?.apply {
        return try {
            val packageInfo: PackageInfo =
                packageManager.getPackageInfo(
                    packageName,
                    PackageManager.GET_CONFIGURATIONS
                )
            packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            "unknown version"
        }
    }
    return null
}

/**
 * 去除特殊字符或将所有中文标号替换为英文标号
 *
 * @param str
 * @return
 */
fun stringFilter(str: String): String {
    var str = str
    str = str.replace("【".toRegex(), "[").replace("】".toRegex(), "]")
        .replace("！".toRegex(), "!").replace("：".toRegex(), ":") // 替换中文标号
    val regEx = "[『』]" // 清除掉特殊字符
    val p = Pattern.compile(regEx)
    val m = p.matcher(str)
    return m.replaceAll("").trim { it <= ' ' }
}

/**
 * 将字符串进行md5加密
 *
 * @param argString 明文
 * @return d5加密后的密文
 */
fun MD5(argString: String): String? {
    val hexDigits = charArrayOf(
        '0',
        '1',
        '2',
        '3',
        '4',
        '5',
        '6',
        '7',
        '8',
        '9',
        'a',
        'b',
        'c',
        'd',
        'e',
        'f'
    )
    return try {
        val btInput = argString.toByteArray()
        // 获得MD5摘要算法的 MessageDigest 对象
        val mdInst = MessageDigest.getInstance("MD5")
        // 使用指定的字节更新摘要
        mdInst.update(btInput)
        // 获得密文
        val md = mdInst.digest()
        // 把密文转换成十六进制的字符串形式
        val j = md.size
        val str = CharArray(j * 2)
        var k = 0
        for (byte0 in md) {
            str[k++] = hexDigits[byte0.toInt().ushr(4) and 0xf]
            str[k++] = hexDigits[(byte0 and 0xf).toInt()]
        }
        String(str)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun MD5_16(argString: String): String? {
    val hexDigits = charArrayOf(
        '0',
        '1',
        '2',
        '3',
        '4',
        '5',
        '6',
        '7',
        '8',
        '9',
        'a',
        'b',
        'c',
        'd',
        'e',
        'f'
    )
    return try {
        val btInput = argString.toByteArray()
        // 获得MD5摘要算法的 MessageDigest 对象
        val mdInst = MessageDigest.getInstance("MD5")
        // 使用指定的字节更新摘要
        mdInst.update(btInput)
        // 获得密文
        val md = mdInst.digest()
        // 把密文转换成十六进制的字符串形式
        val j = md.size
        val str = CharArray(j * 2)
        var k = 0
        for (byte0 in md) {
            str[k++] = hexDigits[byte0.toInt().ushr(4) and 0xf]
            str[k++] = hexDigits[(byte0 and 0xf).toInt()]
        }
        val strResult = String(str)
        strResult.substring(8, 24)
        //return str.toString();
    } catch (e: Exception) {
        Log.i("----", e.toString())
        e.printStackTrace()
        null
    }
}

/**
 * 判断字符串格式是否为手机号码
 *
 * @param phoneNumber
 * @return
 */
fun isPhoneNum(phone: String): Boolean {
    val regex =
        "^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(166)|(17[0,1,3,5,6,7,8])|(18[0-9])|(19[8|9]))\\d{8}$"
    return if (phone.length != 11) {
        showToast("手机号应为11位数")
        false
    } else {
        val p = Pattern.compile(regex)
        val m: Matcher = p.matcher(phone)
        val isMatch: Boolean = m.matches()
        LogUtils.e(TAG, "isPhone: 是否正则匹配$isMatch")
        isMatch
    }
}

//判断输入的格式是否为手机号
fun isPhone(phone: String): Boolean {
    val regex = "^1[3456789]\\d{9}$"
    return if (phone.length != 11) {
        showToast("手机号应为11位数")
        false
    } else {
        val p = Pattern.compile(regex)
        val m: Matcher = p.matcher(phone)
        val isMatch: Boolean = m.matches()
        isMatch
    }
}

/**
 * 判断字符串格式是否为邮箱
 *
 * @param email
 * @return
 */
fun isEMail(email: String): Boolean {
    return !TextUtils.isEmpty(email) && email.matches("^[a-zA-Z0-9_-_.]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$" as Regex)
}

/**
 * 描述：将null转化为“”.
 *
 * @param str 指定的字符串
 * @return 字符串的String类型
 */
fun parseEmpty(str: String): String {
    var str = str
    if ("null" == str.trim { it <= ' ' }) {
        str = ""
    }
    return str.trim { it <= ' ' }
}

/**
 * 描述：是否只是字母和数字.
 *
 * @param str 指定的字符串
 * @return 是否只是字母和数字:是为true，否则false
 */
fun isNumberLetter(str: String): Boolean {
    var isNoLetter = false
    val expr = "^[A-Za-z0-9]+$" as Regex
    if (str.matches(expr)) {
        isNoLetter = true
    }
    return isNoLetter
}

/**
 * 描述：是否只是数字.
 *
 * @param str 指定的字符串
 * @return 是否只是数字:是为true，否则false
 */
fun isNumber(str: String): Boolean {
    var isNumber = false
    val expr = "^[0-9]+$" as Regex
    if (str.matches(expr)) {
        isNumber = true
    }
    return isNumber
}

/**
 * 描述：是否是中文.
 *
 * @param str 指定的字符串
 * @return 是否是中文:是为true，否则false
 */
fun isChinese(str: String): Boolean {
    var isChinese = true
    val chinese = "[\u0391-\uFFE5]" as Regex
    if (!isEmpty(str)) { //获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1
        for (i in str.indices) { //获取一个字符
            val temp = str.substring(i, i + 1)
            //判断是否为中文字符
            if (temp.matches(chinese)) {
            } else {
                isChinese = false
            }
        }
    }
    return isChinese
}

/**
 * 描述：是否包含中文.
 *
 * @param str 指定的字符串
 * @return 是否包含中文:是为true，否则false
 */
fun isContainChinese(str: String): Boolean {
    var isChinese = false
    val chinese = "[\u0391-\uFFE5]" as Regex
    if (!isEmpty(str)) { //获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1
        for (i in str.indices) { //获取一个字符
            val temp = str.substring(i, i + 1)
            //判断是否为中文字符
            if (temp.matches(chinese)) {
                isChinese = true
            } else {
            }
        }
    }
    return isChinese
}

/**
 * 判断对象是否为空
 *
 * @param obj 对象
 * @return `true`: 为空<br></br>`false`: 不为空
 */
fun isEmpty(obj: Any?): Boolean {
    if (obj == null) {
        return true
    }
    if (obj is String && obj.toString().isEmpty()) {
        return true
    }
    if (obj is Editable && obj.toString().isEmpty()) {
        return true
    }
    if (obj is CharSequence && obj.toString().isEmpty()) {
        return true
    }
    if (obj.javaClass.isArray && Array.getLength(obj) == 0) {
        return true
    }
    if (obj is Collection<*> && obj.isEmpty()) {
        return true
    }
    if (obj is Map<*, *> && obj.isEmpty()) {
        return true
    }
    if (obj is SparseArray<*> && obj.size() == 0) {
        return true
    }
    if (obj is SparseBooleanArray && obj.size() == 0) {
        return true
    }
    if (obj is SparseIntArray && obj.size() == 0) {
        return true
    }
    if (obj is SparseLongArray && obj.size() == 0) {
        return true
    }
    return false
}

/**
 * 判断对象是否非空
 *
 * @param obj 对象
 * @return `true`: 非空<br></br>`false`: 空
 */
fun isNotEmpty(obj: Any?): Boolean {
    return !isEmpty(obj)
}

/**
 * 对比时间
 *
 * @param time 对象
 * @return `true`: 非空<br></br>`false`: 空
 */
fun timeEqual(time: String?): String {
    val type = "0" //0 时间相等  1传入的时间大于本地时间  -1传入的时间小于本地时间
    val curTime = System.currentTimeMillis() / 1000
    return if (curTime < java.lang.Long.valueOf(time!!)) { //比较
        "1"
    } else if (curTime > java.lang.Long.valueOf(time)) {
        "-1"
    } else {
        "0"
    }
}

/**
 * 替换空字符串
 *
 * @param s
 * @return
 */
fun getReplaceTrim(s: String): String {
    return s.trim { it <= ' ' }.replace(" ", "")
}

/**
 * 字符串（含中文）转16进制
 *
 * @param str
 * @return
 */
fun string2byte(str: String): ByteArray {
    var ok = ByteArray(0)
    try {
        ok = str.toByteArray(charset("UTF-8"))
    } catch (e: UnsupportedEncodingException) {
        e.printStackTrace()
    }
    return ok
}

/**
 * 16进制转字符串（含中文）
 *
 * @param bytes
 * @return
 */
fun bety2string(bytes: ByteArray): String {
    return String(bytes, Charset.forName("UTF-8"))
}

/**
 * 将系统表情转化为字符串
 *
 * @param s
 * @return
 */
fun getEmoji2String(s: String): String {
    val length = s.length
    var context = ""
    //循环遍历字符串，将字符串拆分为一个一个字符
    for (i in 0 until length) {
        val codePoint = s[i]
        //判断字符是否是emoji表情的字符
        if (isEmojiCharacter(codePoint)) { //如果是将以大括号括起来
            val emoji =
                "{" + Integer.toHexString(codePoint.toInt()) + "}"
            context += emoji
            continue
        }
        context += codePoint
    }
    return context
}

/**
 * 是否包含表情
 *
 * @param codePoint
 * @return 如果不包含 返回false,包含 则返回true
 */
private fun isEmojiCharacter(codePoint: Char): Boolean {
    return !(codePoint.toInt() == 0x0 || codePoint.toInt() == 0x9 || codePoint.toInt() == 0xA
            || codePoint.toInt() == 0xD
            || codePoint.toInt() in 0x20..0xD7FF
            || codePoint.toInt() in 0xE000..0xFFFD || codePoint.toInt() in 0x10000..0x10FFFF)
}

/**
 * 将表情描述转换成表情
 *
 * @param str
 * @return
 */
fun getEmoji(str: String): String {
    var string = str
    val rep = "\\{(.*?)\\}"
    val p = Pattern.compile(rep)
    val m = p.matcher(string)
    while (m.find()) {
        val s1 = m.group().toString()
        val s2 = s1.substring(1, s1.length - 1)
        var s3: String
        try {
            s3 = (s2.toInt(16).toChar()).toString()
            string = string.replace(s1, s3)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    return string
}

/**
 * 检查图片类型
 */
fun pictureToVideo(pictureType: String): Int {
    if (!TextUtils.isEmpty(pictureType)) {
        if (pictureType.startsWith("video") ||
            pictureType.startsWith("mp4")
        ) {
            return ToolsConfig.TYPE_VIDEO
        } else if (pictureType.startsWith("audio")) {
            return ToolsConfig.TYPE_AUDIO
        } else if (pictureType.contains("pdf")) {
            return ToolsConfig.TYPE_PDF
        } else if (pictureType.contains("xls")) {
            return ToolsConfig.TYPE_XLS
        } else if (pictureType.contains("doc")) {
            return ToolsConfig.TYPE_DOC
        } else if (pictureType.contains("zip")) {
            return ToolsConfig.TYPE_ZIP
        } else if (pictureType.contains("txt")) {
            return ToolsConfig.TYPE_TXT
        } else {
            return ToolsConfig.TYPE_FILE
        }
    }
    return ToolsConfig.TYPE_IMAGE
}

/**
 * TextView实现部分文字可点击
 *
 * @param textView
 * @param range start end
 */
fun setSpannable(
    textView: TextView,
    vararg range: Range<Int>,
    click: ClickableSpans,
    flags: Int = Spanned.SPAN_EXCLUSIVE_EXCLUSIVE,
) {
    val spanUser = SpannableStringBuilder(textView.text)
    spanUser.apply {
        range.forEach {
            if (it.upper <= length) {
                val subStr = substring(it.lower, it.upper)
                setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        click.clickable(widget, subStr)
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        super.updateDrawState(ds)
                        click.updateDrawState(ds)
                    }
                }, it.lower, it.upper, flags)
            }
        }
        //设置相应点击事件
        textView.movementMethod = LinkMovementMethod.getInstance()
        textView.text = this
    }
}

interface ClickableSpans {
    fun clickable(view: View, key: String)

    fun updateDrawState(ds: TextPaint) {
        //下划线
        ds.isUnderlineText = false
        //删除线
        ds.isStrikeThruText = false
    }
}

//-----------------以下为引入------------------

//当前毫秒
var currentMilliSecond = 0L
    get() {
        return System.currentTimeMillis()
    }

//显示view
fun <T : View> T.show() {
    visibility = View.VISIBLE
}

//隐藏view
fun <T : View> T.gone() {
    visibility = View.GONE
}

//隐藏view,但站位
fun <T : View> T.hide() {
    visibility = View.INVISIBLE
}

fun View.visible(show: Boolean) {
    this.visibility = if (show) View.VISIBLE else View.GONE
}

/*fun View.visible() {
    this.visibility = View.VISIBLE
}*/

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.isShow() = this.visibility == View.VISIBLE


var vClickTime = 0L

//view点击
fun View.vClick(action: () -> Unit) {
    setOnClickListener {
        if (currentMilliSecond - vClickTime >= 500) {
            vClickTime = currentMilliSecond
            action()
        }
    }
}

//view点击
fun View.vLClick(action: () -> Unit) {
    setOnLongClickListener {
        if (currentMilliSecond - vClickTime >= 500) {
            vClickTime = currentMilliSecond
            action()
        }
        true
    }
}

var first: Long = 0L

/**
 * 防连点startActivity
 */
inline fun <reified T : Activity> Activity.toActivity(vararg params: kotlin.Pair<String, Any?>) {
    if (System.currentTimeMillis() - first > 500L) {
        first = System.currentTimeMillis()
        startActivity<T>(*params)
    }
}

/**
 * 防连点startActivity（自定义View中使用）
 */
inline fun <reified T : Activity> View.toActivity(vararg params: kotlin.Pair<String, Any?>) {
    if (System.currentTimeMillis() - first > 500L) {
        first = System.currentTimeMillis()
        context.startActivity<T>(*params)
    }
}

/**
 * 防连点淡入淡出toActivityAnimation
 */
fun Activity.toActivityAnimation(intent: Intent, vararg views: Pair<View?, String>) {
    if (System.currentTimeMillis() - first > 500L) {
        first = System.currentTimeMillis()
        val toBundle =
            ActivityOptions.makeSceneTransitionAnimation(this, *views)
                .toBundle()
        startActivity(intent, toBundle)
    }
}

/**
 * 防连点toActivityForResult
 */
inline fun <reified T : Activity> Activity.toActivityForResult(
    requestCode: Int = toActivityRequestCode,
    vararg params: kotlin.Pair<String, Any?>,
) {
    if (System.currentTimeMillis() - first > 500L) {
        first = System.currentTimeMillis()
        startActivityForResult<T>(requestCode, *params)
    }
}

// 闪烁动画
fun flash(view: View, duration: Long = 1000L) {
    val alphaAnimation1 = ObjectAnimator.ofFloat(view, "alpha", 0.1f, 1f)
    alphaAnimation1.repeatCount = ValueAnimator.INFINITE
    alphaAnimation1.repeatMode = ValueAnimator.REVERSE
    alphaAnimation1.duration = duration
    alphaAnimation1.start()
}


fun getStartOfDay(): Date {
    val calendar = Calendar.getInstance()
    calendar.time = Date()
    calendar[Calendar.HOUR_OF_DAY] = 0
    calendar[Calendar.MINUTE] = 0
    calendar[Calendar.SECOND] = 0
    calendar[Calendar.MILLISECOND] = 0
    return calendar.time
}

// 获取几天前
fun getDaysAgo(date: Date): Long {
    val diff = getStartOfDay().time - date.time
    return if (diff < 0) {
        // if the input date millisecond > today's 12:00am millisecond it is today
        // (this won't work if you input tomorrow)
        0
    } else {
        TimeUnit.MILLISECONDS.toDays(diff)
    }
}