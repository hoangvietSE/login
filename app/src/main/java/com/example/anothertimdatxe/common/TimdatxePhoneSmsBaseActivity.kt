package com.example.anothertimdatxe.common

import android.os.Bundle
import com.example.anothertimdatxe.base.mvp.BasePresenter
import com.example.anothertimdatxe.widget.TokenPhoneSms

abstract class TimdatxePhoneSmsBaseActivity<T : BasePresenter> : TimdatxeBaseActivity<T>(), TokenPhoneSms.SendTokenSmsListener {

    protected var mTokenPhoneSms = TokenPhoneSms(this, this)
    protected var mUserPhoneNumber: String = ""
    protected var mCheckSendSmsSuccess: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onSendSmsSuccess() {
        mCheckSendSmsSuccess = true
    }

    override fun onSendSmsFailed() {
        mCheckSendSmsSuccess = false
    }

}