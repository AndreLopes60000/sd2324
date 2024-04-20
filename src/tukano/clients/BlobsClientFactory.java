package tukano.clients;

import java.net.URI;

import tukano.clients.rest.RESTBlobsClient;
import tukano.controlers.blobs.Blobs;
import static tukano.helpers.Constants.*;

public class BlobsClientFactory {

    public static Blobs get(URI serverURI) {
        var uriString = serverURI.toString();

        if (uriString.endsWith(REST))
            return new RESTBlobsClient(serverURI);
        else if (true)
            return null;
        else
            throw new RuntimeException("Unknown service type..." + uriString);
    }
    
}
