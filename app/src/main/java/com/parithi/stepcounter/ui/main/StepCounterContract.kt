package com.parithi.stepcounter.ui.main

import android.content.Intent
import com.parithi.stepcounter.LoginMethod
import com.parithi.stepcounter.models.StepData

interface StepCounterContract {

    interface View {
        fun showLogin()
        fun hideLogin()
        fun showName(name: String)
        fun showStepsSummary(stepDataList: List<StepData>?)
        fun startLogin(intent: Intent, requestCode: Int)
    }

    interface Presenter {
        fun initialize()
        fun login(loginMethod: LoginMethod)
        fun fetchSummaryDetails()
        fun onLoginComplete(requestCode: Int, data: Intent?)
        fun logout(loginMethod: LoginMethod)
    }

}