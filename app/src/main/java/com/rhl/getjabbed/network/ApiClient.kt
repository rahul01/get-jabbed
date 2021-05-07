package com.rhl.getjabbed.network

import com.rhl.getjabbed.model.auth.AuthToken
import com.rhl.getjabbed.model.auth.Mobile
import com.rhl.getjabbed.model.auth.Otp
import com.rhl.getjabbed.model.auth.Transaction
import com.rhl.getjabbed.model.beneficiary.Beneficiaries
import com.rhl.getjabbed.model.bookingdetails.Appointment
import com.rhl.getjabbed.model.bookingdetails.BookingDetails
import com.rhl.getjabbed.model.session.CenterList
import com.rhl.getjabbed.model.state.DistrictList
import com.rhl.getjabbed.model.state.StateList
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*


object ApiClient {
    private const val BASE_URL = "https://cdn-api.co-vin.in/api/"
    var okHttpClientBuilder: OkHttpClient.Builder = OkHttpClient.Builder()
    var okHttpClient = createHttpClient(okHttpClientBuilder)

    private val RETROFIT: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service = RETROFIT.create(ApiEndpointInterface::class.java)


    private fun createHttpClient(okHttpClientBuilder: OkHttpClient.Builder): OkHttpClient? {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        okHttpClientBuilder.addInterceptor(logging)
        okHttpClientBuilder.addInterceptor(Interceptor { chain: Interceptor.Chain ->
            val original = chain.request()
            val requestBuilder: Request.Builder = original.newBuilder()
            requestBuilder.addHeader(
                "User-Agent",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36"
            )
            requestBuilder.method(original.method, original.body)
            val request: Request = requestBuilder.build()
            chain.proceed(request)
        })

        return okHttpClientBuilder.build()
    }
}

@JvmSuppressWildcards
interface ApiEndpointInterface {
    @POST("v2/auth/generateMobileOTP")
    fun login(@Body mobile: Mobile): Call<Transaction>

    @POST("v2/auth/validateMobileOtp") // data = {"otp": sha256(str(OTP).encode('utf-8')).hexdigest(), "txnId": txnId}
    fun verification(@Body otp: Otp): Call<AuthToken>

    @GET("v2/appointment/beneficiaries")
    fun getBeneficiaries(@Header("Authorization") authHeader: String): Call<Beneficiaries>

    @GET("v2/admin/location/states")
    fun getStates(): Call<StateList>

    @GET("v2/admin/location/districts/{stateId}")
    fun getDistricts(@Path("stateId") stateId: String?): Call<DistrictList>

    @GET("v2/appointment/sessions/calendarByDistrict")
    fun getSession(
        @QueryMap params: Map<String, Any>,
        @Header("Authorization") authHeader: String
    ): Call<CenterList>

    @POST("v2/appointment/schedule")
    fun bookSession(
        @Body bookingDetails: BookingDetails,
        @Header("Authorization") authHeader: String
    ): Call<Appointment>
}