package com.parithi.stepcounter.helpers

import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.parithi.stepcounter.R
import java.text.SimpleDateFormat
import java.util.*

// Helper methods

fun View.hide() {
    this.visibility = View.GONE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.show() {
    this.visibility = View.VISIBLE
}


fun ImageView.imageFromUrl(url: String?) {
    this.setBackgroundColor(ContextCompat.getColor(this.context, R.color.light_grey))
    if (url == null) {
        setImageBitmap(null)
    } else {
        Glide.with(this.context).load(url).transition(
            DrawableTransitionOptions.withCrossFade()
        ).into(this)
    }
}

fun Date.toReadableDate(): String {
    val format = "MMM d"
    val dateFormatter = SimpleDateFormat(format)
    return dateFormatter.format(this)
}

fun Date.isToday(): Boolean {
    val today = Calendar.getInstance()
    today.time = Date()
    val checkDate = Calendar.getInstance()
    checkDate.time = this
    return isSameDay(today, checkDate)
}


fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
    return cal1[Calendar.ERA] == cal2[Calendar.ERA] && cal1[Calendar.YEAR] == cal2[Calendar.YEAR] && cal1[Calendar.DAY_OF_YEAR] == cal2[Calendar.DAY_OF_YEAR]
}