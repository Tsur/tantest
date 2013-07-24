package com.main;

//import java.io.InputStream;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;


public class SslChatLauncher {

    public static void main(String[] args) throws InterruptedException {

        Configuration config = new Configuration();
        config.setHostname("localhost");
        config.setPort(3000);

        //config.setKeyStorePassword("test1234");
        //InputStream stream = SslChatLauncher.class.getResourceAsStream("/keystore.jks");
        //config.setKeyStore(stream);

        SocketIOServer server = new SocketIOServer(config);

        server.addListeners(new ServerService());
        server.start();
    }

}
