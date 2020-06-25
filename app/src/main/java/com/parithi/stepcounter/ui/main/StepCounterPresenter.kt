package com.parithi.stepcounter.ui.main

import android.content.Intent
import com.parithi.stepcounter.LoginMethod
import com.parithi.stepcounter.helpers.GoogleFitHelper
import timber.log.Timber

class StepCounterPresenter(val view: StepCounterContract.View) : StepCounterContract.Presenter {

    override fun initialize() {
        GoogleFitHelper.initialize()

        if (GoogleFitHelper.isSignInRequired()) {
            view.showLogin()
            Timber.d("SHOWING LOGIN")
        } else {
            showDetails()
        }
    }

    override fun login(loginMethod: LoginMethod) {
        if (loginMethod == LoginMethod.GOOGLE_SIGN_IN) {
            val intent = GoogleFitHelper.getSignInIntent()
            view.startLogin(intent, GoogleFitHelper.GOOGLE_SIGN_IN_REQUEST_CODE)
        }
    }

    override fun onLoginComplete(requestCode: Int, data: Intent?) {
        if (requestCode == GoogleFitHelper.GOOGLE_SIGN_IN_REQUEST_CODE) {
            GoogleFitHelper.finishSignIn(data) { status ->
                if (status) {
                    showDetails()
                }
            }
        }
    }

    override fun fetchSummaryDetails() {
        GoogleFitHelper.fetchSevenDaySummary { stepDataList ->
            view.showStepsSummary(stepDataList)
        }
    }

    private fun showDetails() {
        Timber.d("HIDING LOGIN")
        view.hideLogin()
        val name = GoogleFitHelper.getUserName() ?: "Visitor"
        view.showName(name)
        fetchSummaryDetails()
    }

    override fun logout(loginMethod: LoginMethod) {
        if (loginMethod == LoginMethod.GOOGLE_SIGN_IN) {
            GoogleFitHelper.signOut()
        }
    }


}