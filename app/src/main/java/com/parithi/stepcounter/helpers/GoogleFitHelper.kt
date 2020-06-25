package com.parithi.stepcounter.helpers

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Field
import com.google.android.gms.fitness.request.DataReadRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.parithi.stepcounter.R
import com.parithi.stepcounter.StepCounter
import com.parithi.stepcounter.models.StepData
import java.util.*
import java.util.concurrent.TimeUnit

// Helper class for Google Fit
object GoogleFitHelper {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private var user: FirebaseUser? = null

    const val GOOGLE_SIGN_IN_REQUEST_CODE: Int = 1001

    private lateinit var context: Context

    fun initialize() {
        context = StepCounter.context
        firebaseAuth = FirebaseAuth.getInstance()
        user = firebaseAuth.currentUser
    }

    fun isSignInRequired(): Boolean {
        return user == null
    }

    fun getSignInIntent(): Intent {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestScopes(Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(context, gso)
        return googleSignInClient.signInIntent
    }

    fun fetchSevenDaySummary(completion: (List<StepData>?) -> Unit) {

        if (user == null) {
            completion(null); return
        }

        val cal = Calendar.getInstance()
        val now = Date()
        cal.time = now
        val endTime = cal.timeInMillis
        cal.add(Calendar.DAY_OF_YEAR, -7)
        val startTime = cal.timeInMillis

        val readRequest: DataReadRequest = DataReadRequest.Builder()
            .aggregate(DataType.AGGREGATE_STEP_COUNT_DELTA, DataType.TYPE_STEP_COUNT_DELTA)
            .bucketByTime(1, TimeUnit.DAYS)
            .enableServerQueries()
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .build()

        val signedInAccount = GoogleSignIn.getLastSignedInAccount(context)
        Fitness.getHistoryClient(context, signedInAccount!!)
            .readData(readRequest)
            .addOnSuccessListener { dataReadResponse ->
                if (dataReadResponse == null) {
                    completion(null); return@addOnSuccessListener
                }
                if (dataReadResponse.buckets == null) {
                    completion(null); return@addOnSuccessListener
                }
                val resultSet = mutableListOf<StepData>()

                for (bucket in dataReadResponse.buckets) {
                    val dataSet = bucket.getDataSet(DataType.AGGREGATE_STEP_COUNT_DELTA)!!
                    val dataPoint = dataSet.dataPoints.first() ?: return@addOnSuccessListener
                    val googleFitSteps = dataPoint.getValue(Field.FIELD_STEPS).toString().toDouble()
                    resultSet.add(
                        StepData(
                            date = Date(dataPoint.getTimestamp(TimeUnit.MILLISECONDS)),
                            stepCount = googleFitSteps
                        )
                    )
                }

                completion(resultSet)
            }
            .addOnFailureListener {
                completion(null)
            }
    }

    fun finishSignIn(data: Intent?, completion: (Boolean) -> Unit) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val signedInAccount = task.getResult(ApiException::class.java)!!
            val credential = GoogleAuthProvider.getCredential(signedInAccount.idToken!!, null)
            firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    user = firebaseAuth.currentUser
                    completion(true)
                } else {
                    completion(false)
                }
            }
        } catch (e: Exception) {
            completion(false)
        }
    }

    fun getUserName(): String? {
        return user?.displayName
    }

    fun signOut() {
        firebaseAuth.signOut()
        user = null
    }

}