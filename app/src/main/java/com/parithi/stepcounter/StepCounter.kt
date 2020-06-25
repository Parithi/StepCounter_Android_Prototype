package com.parithi.stepcounter

import android.app.Application
import android.content.Context
import timber.log.Timber
import timber.log.Timber.DebugTree

class StepCounter : Application() {

    companion object {
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }

    }
}