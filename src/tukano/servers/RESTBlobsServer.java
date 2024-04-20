package tukano.servers;

import java.net.URI;
import java.net.InetAddress;
import java.util.logging.Logger;
import tukano.helpers.Discovery;
import static tukano.helpers.Constants.*;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import tukano.resources.rest.RESTBlobsResource;;

public class RESTBlobsServer {

    private static Logger Log = Logger.getLogger(RESTBlobsServer.class.getName());

    static {
        System.setProperty("java.net.preferIPv4Stack", "true");
        System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s\n");
    
    }

    public static void main(String[] args) {

        try {

            
            String address = InetAddress.getLocalHost().getHostAddress();
            addNumBlobs();
            int blobNum = getNumBlobs();
            String serverAddress = String.format(SERVER_REST_BLOBS_ADDRESS_FMT, address,String.valueOf(blobNum), BLOBS_REST_SERVER_PORT);
            ResourceConfig config = new ResourceConfig();
            config.register(RESTBlobsResource.class);
            JdkHttpServerFactory.createHttpServer( URI.create(serverAddress), config);
            String serverURI = BLOBS_SERVICE+String.valueOf(blobNum);
            Discovery discovery = Discovery.getInstance();
            Log.info("Starting discovery process with message: " + serverURI + "\t"+ serverAddress);
            discovery.announce(serverURI, serverAddress);

        }
        catch (Exception e){
            Log.severe(e.getMessage() + e.getCause());
        }
    }

}
