package tukano.helpers;

public class Constants {

    public static final String SERVER_REST_ADDRESS_FMT = "http://%s:%s/rest";

    public static final String SERVER_REST_BLOBS_ADDRESS_FMT = "http://%s%s:%s/rest";

    public static final String REST = "/rest";

    public static final String USERS_SERVICE = "users";

    public static final String SHORTS_SERVICE = "feeds";

    public static final String BLOBS_SERVICE = "blobs";

    public static final String USERS_EXTRA_ARGS = "USERS_EXTRA_ARGS";

    public static final String FEEDS_EXTRA_ARGS = "FEEDS_EXTRA_ARGS";

    public static final String USERS_REST_PORT = "USERS_REST_PORT";

    public static final String FEEDS_REST_PORT = "FEEDS_REST_PORT";

    public static final String USERS_REST_SERVER_PORT = "8080";

    public static final String SHORTS_REST_SERVER_PORT = "8081";

    public static final String BLOBS_DIRECTORY = "tukano.repositories.blobs.blobFiles";

    
    public static int numBlobs = 0;

    public static void addNumBlobs() {
        numBlobs++;
    }

    public static int getNumBlobs() {
        return numBlobs;
    }
    
}
