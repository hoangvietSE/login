package com.example.anothertimdatxe.network

import android.text.TextUtils
import com.example.anothertimdatxe.entity.UserData
import com.example.anothertimdatxe.request.LoginRequest
import com.google.gson.Gson
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//singleton
object RetrofitManager {
    fun createRetrofit(baseUrl: String): Retrofit {
        var retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
        return retrofit
    }

    private val apiService = createRetrofit("http://api.timdatxe.com/").create(ApiService::class.java)

    fun loginUser(callBack: ICallBack<UserData>, request: LoginRequest): Disposable {
        val subscriber = getSubcribler(callBack)
        var requestBody = createPostRequest(request)
        var disposable: Disposable = apiService.loginUser(requestBody)
                //Observable: I/O thread
                //Observer: Main Thread
                //get return data in main thread
                //AndroidSchedulers.mainThread(): allow in UI thread
                .observeOn(AndroidSchedulers.mainThread())
                //request will be execute in I/O thread
                //Schedulers.io(): network call, file, database...
                .subscribeOn(Schedulers.io())
                .subscribeWith(subscriber)
        return disposable
    }

    private fun <T> getSubcribler(callBack: ICallBack<T>): DisposableSingleObserver<BaseResult<T>> {
         return object : DisposableSingleObserver<BaseResult<T>>() {
            override fun onSuccess(t: BaseResult<T>) {
                if (t.status == 200)
                    callBack.onSuccess(t.data)
                else
                    if (TextUtils.isEmpty(t.msg))
                        callBack.onError(ApiException(""))
                    else callBack.onError(ApiException(t.msg.toString()))

            }

            override fun onError(e: Throwable) {
                callBack.onError(ApiException(e.message!!))
            }
         }
    }

    private fun createPostRequest(request: Any): RequestBody {
        val jsonInString = Gson().toJson(request)
        return RequestBody.create(MultipartBody.FORM, jsonInString)//data is divided to many part
    }
}