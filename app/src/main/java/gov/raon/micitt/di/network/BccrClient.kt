package gov.raon.micitt.di.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit


object BccrClient {
    private const val BASE_URL = "https://gee.bccr.fi.cr/Indicadores/Suscripciones/WS/"

    val instance: Retrofit by lazy {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val soapInterceptor = Interceptor { chain ->
            val request = chain.request().newBuilder()
                .header("Content-Type", "text/xml")
                .header("Accept", "application/xml")
                .build()
            chain.proceed(request)
        }


        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .addInterceptor(soapInterceptor)
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(ScalarsConverterFactory.create()) // Si también manejas respuestas como String
            .addConverterFactory(SimpleXmlConverterFactory.create())
            .build()
    }
}