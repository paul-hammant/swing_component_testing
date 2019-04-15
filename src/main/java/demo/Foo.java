package demo;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class Foo {

    private static final class EchoDotWebSocketDotOrgListener extends WebSocketListener {
        private static final int NORMAL_CLOSURE_STATUS = 1000;
        private WebSocket webSocket;

        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            this.webSocket = webSocket;
            System.out.println("onOpen : webSocket object-id:" + System.identityHashCode(webSocket) + threadString());
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            System.out.println("onMessage text : " + text + threadString());

        }

        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {
            System.out.println("onMessage bytes : " + bytes.hex() + threadString());
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            webSocket.close(NORMAL_CLOSURE_STATUS, null);
            System.out.println("onClosing : " + code + " / " + reason + threadString());
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            System.out.println("onFailure : " + t.getMessage() + threadString());
        }
    }

    public static void main(String[] args) throws InterruptedException {

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder().url("ws://echo.websocket.org").build();
        EchoDotWebSocketDotOrgListener listener = new EchoDotWebSocketDotOrgListener();
        WebSocket ws = client.newWebSocket(request, listener);

        System.out.println("WebSocket setup, webSocket object-id: " + System.identityHashCode(ws));

        System.out.println("Main" + threadString());

        Thread.sleep(2000);

        listener.webSocket.send("Hello, it's Paul !");
        Thread.sleep(2000);
        listener.webSocket.send("What's up ?");
        Thread.sleep(2000);
        listener.webSocket.send(ByteString.decodeHex("deadbeef"));
        Thread.sleep(2000);
        listener.webSocket.close(listener.NORMAL_CLOSURE_STATUS, "Goodbye !");
        Thread.sleep(2000);

        client.dispatcher().executorService().shutdown();

    }

    private static String threadString() {
        return " Thread = " + System.identityHashCode(Thread.currentThread());
    }

}
