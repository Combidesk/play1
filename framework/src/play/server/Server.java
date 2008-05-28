package play.server;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Properties;
import org.apache.asyncweb.common.HttpCodecFactory;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.SocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import play.Logger;
import play.Play;

public class Server {
    private SocketAcceptor acceptor;

    public Server() {
        Properties p = Play.configuration;
        int httpPort = Integer.parseInt(p.getProperty("http.port", "9000"));
        acceptor = new NioSocketAcceptor();
        acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new HttpCodecFactory()));
        acceptor.setReuseAddress(true);
        acceptor.getSessionConfig().setReuseAddress(true);
        acceptor.getSessionConfig().setReceiveBufferSize(1024 * 100);
        acceptor.getSessionConfig().setSendBufferSize(1024 * 100);
        acceptor.getSessionConfig().setTcpNoDelay(true);
        acceptor.getSessionConfig().setSoLinger(-1);
        acceptor.setBacklog(50000);
        acceptor.setHandler(new HttpHandler());
        try {
            acceptor.bind(new InetSocketAddress(httpPort));
            Logger.info("Play Listening on port " + httpPort);
        } catch (IOException e) {
            Logger.error("Play could not bind on port " + httpPort, e);
            acceptor.dispose();
        }
    }

    public static void main(String[] args) {
        File root = new File(System.getProperty("application.path"));
        Play.init(root);
        new Server();
    }
}
