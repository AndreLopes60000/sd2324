package tukano.servers;

import java.net.URI;
import java.net.InetAddress;
import java.util.logging.Logger;

import tukano.helpers.Discovery;
import tukano.resources.rest.RESTUsersResource;
import org.glassfish.jersey.server.ResourceConfig;
import static tukano.helpers.Constants.*;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;

public class RESTUsersServer {
    private static Logger Log = Logger.getLogger(RESTUsersServer.class.getName());

    static {
        System.setProperty("java.net.preferIPv4Stack", "true");
        System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s\n");
    }


    public static void main(String[] args) {

        try {

            String serverURI = USERS_SERVICE;
            String address = InetAddress.getLocalHost().getHostAddress();
            String serverAddress = String.format(SERVER_REST_ADDRESS_FMT, address, USERS_SERVER_PORT);
            ResourceConfig config = new ResourceConfig();
            config.register(RESTUsersResource.class);
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
