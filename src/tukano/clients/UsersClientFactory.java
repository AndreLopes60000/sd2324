package tukano.clients;

import static tukano.helpers.Constants.*;

import java.net.URI;
import tukano.clients.rest.RESTUsersClient;
import tukano.controlers.users.Users;
/**
 * Makes either a REST or GRCP Users client depending on the server's URI information
 */
public class UsersClientFactory {

    public static Users get(URI serverURI) {
        var uriString = serverURI.toString();

        if (uriString.endsWith(REST))
            return new RESTUsersClient(serverURI);
        else if (true)
            return null;
        else
            throw new RuntimeException("Unknown service type..." + uriString);
    }
}
