package tukano.servers;

import java.net.URI;
import java.net.InetAddress;
import java.util.logging.Logger;
import tukano.helpers.Discovery;
import static tukano.helpers.Constants.*;
import tukano.resources.rest.RESTShortsResource;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;


public class RESTShortsServer {

    private static Logger Log = Logger.getLogger(RESTShortsServer.class.getName());

    static {
        System.setProperty("java.net.preferIPv4Stack", "true");
        System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s\n");
    }

    public static void main(String[] args) {

        try {

            String serverURI = USERS_SERVICE;
            String address = InetAddress.getLocalHost().getHostAddress();
            String serverAddress = String.format(SERVER_REST_ADDRESS_FMT, address, SHORTS_REST_SERVER_PORT);
            ResourceConfig config = new ResourceConfig();
            config.register(RESTShortsResource.class);
            JdkHttpServerFactory.createHttpServer( URI.create(serverAddress), config);

            Discovery discovery = Discovery.getInstance();
            Log.info("Starting discovery process with message: " + serverURI + "\t"+ serverAddress);
            discovery.announce(serverURI, serverAddress);

        }
        catch (Exception e){
            Log.severe(e.getMessage() + e.getCause());
        }
    }
    
}
