package id.husna.learnsocketandroid

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.GsonBuilder
import com.tinder.scarlet.Scarlet
import com.tinder.scarlet.messageadapter.gson.GsonMessageAdapter
import com.tinder.scarlet.streamadapter.rxjava2.RxJava2StreamAdapterFactory
import com.tinder.scarlet.websocket.ShutdownReason
import com.tinder.scarlet.websocket.WebSocketEvent
import com.tinder.scarlet.websocket.okhttp.OkHttpWebSocket
import id.husna.learnsocketandroid.api.SocketServiceApi
import id.husna.learnsocketandroid.api.Subscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor

class MainActivity : AppCompatActivity() {

    private val BITCOIN_TICKER_SUBSCRIBE_MESSAGE = Subscribe(
        productIds = listOf("BTC-USD"),
        channels = listOf("ticker")
    )

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .build()

    private val gson = GsonBuilder()
        .serializeNulls()
        .create()
    private val protocol = OkHttpWebSocket(
        okHttpClient,
        OkHttpWebSocket.SimpleRequestFactory(
            { Request.Builder().url("wss://ws-feed.gdax.com").build() },
            { ShutdownReason.GRACEFUL }
        )
    )

    private val configuration = Scarlet.Configuration(
        messageAdapterFactories = listOf(GsonMessageAdapter.Factory(gson)),
        streamAdapterFactories = listOf(RxJava2StreamAdapterFactory())
    )

    private val service = Scarlet(protocol, configuration).create<SocketServiceApi>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener {
            doSubscribeSocket()
        }
    }

    @SuppressLint("CheckResult")
    private fun doSubscribeSocket() {

        service.observeOnConnectionOpenedEvent()
            .filter{ it is WebSocketEvent.OnConnectionOpened }
            .subscribe({
                service.sendSubscribe(BITCOIN_TICKER_SUBSCRIBE_MESSAGE)
            }, {e->e.printStackTrace()})

        service.observeTicker()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({ ticker ->
                val text = "BTC: ${ticker.price} at ${ticker.time}"
                Log.d("WEBSOCKET",text)
                txt.text = text
            }, {e -> e.printStackTrace()})
    }
}
