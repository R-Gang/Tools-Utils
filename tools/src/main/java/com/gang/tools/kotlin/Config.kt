package com.gang.tools.kotlin

import androidx.databinding.ktx.BuildConfig

/**
 *
 * @ProjectName:    tools
 * @Package:        com.gang.tools.kotlin
 * @ClassName:      Config
 * @Description:    项目配置参数
 * @Author:         haoruigang
 * @CreateDate:     2020/8/10 17:26
 */
object Config {

    const val TAG = "Tools-Utils"

    /**
     * startActivityForResult requestCode
     */
    const val toActivityRequestCode = 0x000001


    // 文件类型
    const val TYPE_ALL = 0
    const val TYPE_IMAGE = 1
    const val TYPE_VIDEO = 2
    const val TYPE_AUDIO = 3
    const val TYPE_PDF = 4
    const val TYPE_XLS = 5
    const val TYPE_DOC = 6
    const val TYPE_TXT = 7
    const val TYPE_ZIP = 8
    const val TYPE_FILE = 9

    // 设置全局字体("HYXinRenWenSongW.ttf")
    var typefaceAll = "HYXinRenWenSongW.ttf"

    // 是否显示日志
    var isShowLog = BuildConfig.DEBUG

}