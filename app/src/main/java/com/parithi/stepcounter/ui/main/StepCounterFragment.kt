package com.parithi.stepcounter.ui.main

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.parithi.stepcounter.LoginMethod
import com.parithi.stepcounter.R
import com.parithi.stepcounter.helpers.*
import com.parithi.stepcounter.helpers.GoogleFitHelper.GOOGLE_SIGN_IN_REQUEST_CODE
import com.parithi.stepcounter.models.StepData
import kotlinx.android.synthetic.main.main_fragment.*


class StepCounterFragment : Fragment(), StepCounterContract.View {

    companion object {
        fun newInstance() = StepCounterFragment()
        const val REQUIRED_STEPS = 4000
    }

    lateinit var presenter: StepCounterContract.Presenter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter = StepCounterPresenter(this)
        presenter.initialize()
    }


    override fun showLogin() {
        salutation_textview?.hide()
        step_count_recycler_view?.hide()
        data_container_view?.hide()
        sign_in_button?.show()
        app_logo_image_view?.show()
        title_text_view?.show()
        profile_image_view?.hide()

        sign_in_button?.setOnClickListener {
            presenter.login(LoginMethod.GOOGLE_SIGN_IN)
        }
    }

    override fun hideLogin() {
        salutation_textview?.show()
        step_count_recycler_view?.show()
        profile_image_view?.show()
        data_container_view?.show()
        sign_in_button?.hide()
        app_logo_image_view?.hide()
        title_text_view?.hide()
    }

    override fun showName(name: String) {
        salutation_textview?.text = String.format(getString(R.string.salutation), name)
        profile_image_view?.imageFromUrl(String.format(getString(R.string.avatar_url), name))

        profile_image_view?.setOnClickListener {
            showLogout()
        }
    }

    override fun showStepsSummary(stepDataList: List<StepData>?) {
        if (stepDataList == null) {
            step_count_recycler_view?.adapter = null
            error_text_view?.show()
        } else {
            step_count_recycler_view?.adapter =
                StepCountRecyclerViewAdapter(stepDataList.reversed())
            step_count_recycler_view?.hasFixedSize()
            if (stepDataList.isEmpty()) {
                error_text_view?.show()
            } else {
                error_text_view?.hide()
            }
        }
    }

    override fun startLogin(intent: Intent, requestCode: Int) {
        startActivityForResult(intent, requestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GOOGLE_SIGN_IN_REQUEST_CODE) {
            presenter.onLoginComplete(requestCode, data)
        }
    }

    private fun showLogout() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.logout_label))
        builder.setMessage(getString(R.string.logout_message))

        builder.setPositiveButton(R.string.logout_label) { _, _ ->
            presenter.logout(LoginMethod.GOOGLE_SIGN_IN)
            showLogin()
        }

        builder.setNegativeButton(R.string.cancel_label) { _, _ ->
            // Do Nothing
        }
        builder.show()
    }

    internal inner class StepCountRecyclerViewAdapter(private val stepDataList: List<StepData>) :
        RecyclerView.Adapter<StepCountViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StepCountViewHolder {
            return StepCountViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.step_count_row, parent, false)
            )
        }

        override fun getItemCount(): Int {
            return stepDataList.size
        }

        override fun onBindViewHolder(holder: StepCountViewHolder, position: Int) {
            val stepData = stepDataList[position]
            holder.dateTextView.text = stepData.date.toReadableDate()
            holder.stepCountTextView.text = stepData.stepCount.toInt().toString()

            val percentage = stepData.stepCount / REQUIRED_STEPS

            when {
                percentage > 0.8 -> {
                    holder.emojiTextView.text = "😀"
                }
                percentage > 0.5 -> {
                    holder.emojiTextView.text = "🙂"
                }
                else -> {
                    holder.emojiTextView.text = "😟"
                }
            }

            if (stepData.date.isToday()) {
                holder.containerView.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.containerView.context,
                        R.color.colorPrimary
                    )
                )
                holder.stepCountTextView.setTextColor(
                    ContextCompat.getColor(
                        holder.containerView.context,
                        R.color.white
                    )
                )
                holder.dateTextView.setTextColor(
                    ContextCompat.getColor(
                        holder.containerView.context,
                        R.color.white
                    )
                )
            } else {
                holder.containerView.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.containerView.context,
                        R.color.blueBackground
                    )
                )
                holder.stepCountTextView.setTextColor(
                    ContextCompat.getColor(
                        holder.containerView.context,
                        R.color.black
                    )
                )
                holder.dateTextView.setTextColor(
                    ContextCompat.getColor(
                        holder.containerView.context,
                        android.R.color.darker_gray
                    )
                )
            }
        }

    }

    internal inner class StepCountViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateTextView: TextView = itemView.findViewById(R.id.date_text_view)
        val stepCountTextView: TextView = itemView.findViewById(R.id.step_count_text_view)
        val emojiTextView: TextView = itemView.findViewById(R.id.emoji_text_view)
        val containerView: ConstraintLayout = itemView.findViewById(R.id.container_view)
    }

}