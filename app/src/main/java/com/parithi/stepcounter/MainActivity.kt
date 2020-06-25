package com.parithi.stepcounter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.parithi.stepcounter.ui.main.StepCounterFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, StepCounterFragment.newInstance())
                .commitNow()
        }
    }
}