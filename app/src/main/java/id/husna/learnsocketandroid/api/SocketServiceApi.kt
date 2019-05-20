package id.husna.learnsocketandroid.api

import com.tinder.scarlet.ws.Receive
import com.tinder.scarlet.ws.Send
import com.tinder.scarlet.websocket.WebSocketEvent
import io.reactivex.Flowable

interface SocketServiceApi {

    @Send
    fun sendSubscribe(subscribe: Subscribe)
    @Receive
    fun observeTicker(): Flowable<Ticker>
    @Receive
    fun observeOnConnectionOpenedEvent(): Flowable<WebSocketEvent>
}
