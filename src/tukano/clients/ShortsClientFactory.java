package tukano.clients;

import static tukano.helpers.Constants.*;

import java.net.URI;
import tukano.controlers.shorts.Shorts;
import tukano.clients.rest.RESTShortsClient;

public class ShortsClientFactory {
    
    public static Shorts get(URI serverURI) {
        var uriString = serverURI.toString();

        if (uriString.endsWith(REST))
            return new RESTShortsClient(serverURI);
        else if (true)
            return null;
        else
            throw new RuntimeException("Unknown service type..." + uriString);
    }
}
