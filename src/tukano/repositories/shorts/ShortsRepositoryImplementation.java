package tukano.repositories.shorts;

import static tukano.helpers.Constants.BLOBS_SERVICE;
import static tukano.helpers.Constants.getNumBlobs;

import java.net.URI;
import java.util.List;

import tukano.helpers.Hibernate;

import tukano.api.Short;
import tukano.helpers.Discovery;

public class ShortsRepositoryImplementation implements ShortsRepository{


    private static ShortsRepository singleton;
    private Discovery discovery = Discovery.getInstance();

    private int seqNumber;
    private int currentBlobUsed;


    public static synchronized ShortsRepository getInstance(){
        if (singleton == null){
            singleton = new ShortsRepositoryImplementation();
        }
        return singleton;
    }

    public ShortsRepositoryImplementation(){
        seqNumber = 0;
        currentBlobUsed = 0;
    }

    @Override
    public Short createShort(String userId, String password) {
        String shortId = getUniqueShortId();
        String blobUrl = makeURI(BLOBS_SERVICE).getPath();
        Short newShort = new Short(shortId, userId, blobUrl);
        Hibernate.getInstance().persist(newShort);
        return newShort;
    }

    @Override
    public Void deleteShort(String shortId, String password) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteShort'");
    }

    @Override
    public Short getShort(String shortId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getShort'");
    }

    @Override
    public List<String> getShorts(String userId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getShorts'");
    }

    @Override
    public Void follow(String userId1, String userId2, boolean isFollowing, String password) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'follow'");
    }

    @Override
    public List<String> followers(String userId, String password) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'followers'");
    }

    @Override
    public Void like(String shortId, String userId, boolean isLiked, String password) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'like'");
    }

    @Override
    public List<String> likes(String shortId, String password) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'likes'");
    }

    @Override
    public List<String> getFeed(String userId, String password) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getFeed'");
    }

    private String getUniqueShortId(){
        int shortId = seqNumber++;
        return String.valueOf(shortId); 
    }

     /**
     * Makes a URI for a REST request
     * @param domain domain
     * @param service service type
     * @return URI
     */
    private URI makeURI(String service){
        URI uri = discovery.knownUrisOf(service, 1)[0];
        return uri;
    }

    public int getCurrentBlob(){
        int numBlobs = getNumBlobs();
        int blobToBeUsed = currentBlobUsed;
        currentBlobUsed = (currentBlobUsed < numBlobs - 1) ? currentBlobUsed + 1 : 0;
        return blobToBeUsed;
    }
    
}
