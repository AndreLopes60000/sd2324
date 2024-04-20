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
        Short shortToRemove = getShort(shortId);
        Hibernate.getInstance().delete(shortToRemove);
        return null;
    }

    @Override
    public Short getShort(String shortId) {
        return getDBShort(shortId);
    }

    @Override
    public List<String> getShorts(String userId) {
        return getUserShorts(userId);
    }

    @Override
    public List<Short> getObjectShorts(String userId) {
       return Hibernate.getInstance().sql("SELECT * FROM Short s WHERE s.ownerId = "+userId+"", Short.class);
    }

    @Override
    public Void like(String shortId, String userId, boolean isLiked, String password) {
        Short shortToUpdate = getDBShort(shortId);
        if(isLiked)
            shortToUpdate.addLike(userId);
        else
            shortToUpdate.removeLike(userId);

        Hibernate.getInstance().update(shortToUpdate);
        return null;
    }

    @Override
    public List<String> likes(String shortId, String password) {
        Short wantedShort = getDBShort(shortId);
        return wantedShort.getLikes();
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
        URI uri = discovery.knownUrisOf(service+getCurrentBlob(), 1)[0];
        return uri;
    }

    private int getCurrentBlob(){
        int numBlobs = getNumBlobs();
        int blobToBeUsed = currentBlobUsed;
        currentBlobUsed = (currentBlobUsed < numBlobs - 1) ? currentBlobUsed + 1 : 0;
        return blobToBeUsed;
    }

    private Short getDBShort(String shortId){
        List<Short> shorts = Hibernate.getInstance().sql("SELECT * FROM Short s WHERE s.shortId = "+shortId+"", Short.class);
        if (shorts.isEmpty())
            return null;
        return shorts.get(0);
    }

    private List<String> getUserShorts(String userId){
        return Hibernate.getInstance().sql("SELECT shortId FROM Short s WHERE s.ownerId = "+userId+"", String.class);        
    }

    @Override
    public void updateShortLikes(String userId, String shortId, boolean isLiked) {
        Short likedShort = getDBShort(shortId);
        if(isLiked){
            likedShort.addLike(userId);
        }
        else{
            likedShort.removeLike(userId);
        }
        Hibernate.getInstance().update(likedShort);
    }

    
    
}
