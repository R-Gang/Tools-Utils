package com.tools.kotlin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.gang.tools.kotlin.utils.toastCustom
import com.tools.kotlin.R
import com.tools.kotlin.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var mainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 原始方式
        // setContentView(R.layout.activity_main)

        /*
        方式1 视图绑定
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        val view = mainBinding.root
        setContentView(view)
        */

        /*
        方式2 数据绑定
        */
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)


        mainBinding.mainData = this

    }

    fun Custom() {
        toastCustom("Tools-Utils")
    }

    fun onToUpdateApp() {

    }


    private var headImage: String? = ""
    fun aliyunOss() {
        /*val mImageName =
            DateUtils.getCurTimeLong("yyyyMMddHHmmss") + UserManager.INSTANCE.userData.user_id + ".jpg"
        //Url
        if (mImageName != "") {
            headImage = Config.OSS_URL + mImageName
        }*/
        // 阿里云使用方式
        // AliYunOss.getInstance(this)?.upload(mImageName, "", null)
    }

}