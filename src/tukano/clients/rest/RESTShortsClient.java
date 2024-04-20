package tukano.clients.rest;

import java.net.URI;
import java.util.List;
import tukano.api.Short;
import tukano.helpers.Result;
import tukano.api.rest.RestShorts;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.client.WebTarget;
import tukano.controlers.shorts.Shorts;

public class RESTShortsClient extends RESTClient implements Shorts{

        /**
	 * Stores the target server
	 */
	final WebTarget target;


	/**
	 * Constructor
	 * @param serverURI server URI
	 */
	public RESTShortsClient(URI serverURI ) {
		super( serverURI );
		target = client.target( serverURI ).path( RestShorts.PATH );
	}


    @Override
    public Result<Short> createShort(String userId, String password) {
        return super.reTry(() -> client_createShort(userId, password));
    }

    @Override
    public Result<Void> deleteShort(String shortId, String password) {
        return super.reTry(() -> client_deleteShort(shortId, password));
    }

    @Override
    public Result<Short> getShort(String shortId) {
        return super.reTry(() -> client_getShort(shortId));
    }

    @Override
    public Result<List<String>> getShorts(String userId) {
        return super.reTry(() -> client_getShorts(userId));
    }

    @Override
    public Result<Void> follow(String userId1, String userId2, boolean isFollowing, String password) {
        return super.reTry(() -> client_follow(userId1, userId2, isFollowing, password));
    }

    @Override
    public Result<List<String>> followers(String userId, String password) {
        return super.reTry(() -> client_followers(userId, password));
    }

    @Override
    public Result<Void> like(String shortId, String userId, boolean isLiked, String password) {
        return super.reTry(() -> client_like(shortId, userId, isLiked, password));
    }

    @Override
    public Result<List<String>> likes(String shortId, String password) {
        return super.reTry(() -> client_likes(shortId, password));
    }

    @Override
    public Result<List<String>> getFeed(String userId, String password) {
        return super.reTry(() -> client_getFeed(userId, password));
    }

    //Private methods

    /**
	 * Creates a new short, generating its unique identifier. 
	 * The result short will include the blob storage location where the media should be uploaded.
	 * 
	 * @param userId - the owner of the new short
	 * @param password - the password of owner of the new short
	 * @return (OK, Short) if the short was created;
	 * NOT FOUND, if the owner of the short does not exist;
	 * FORBIDDEN, if the password is not correct;
	 * BAD_REQUEST, otherwise.
	 */
    private Result<Short> client_createShort(String userId, String password) {
        Response r = target.path( userId )
                .queryParam(RestShorts.PWD, password).request()
				.accept(MediaType.APPLICATION_JSON)
				.post(null);
		return super.toJavaResult(r, Short.class);
    }

    /**
	 * Deletes a given Short.
	 * 
	 * @param shortId the unique identifier of the short to be deleted
	 * @return (OK,void), 
	 * 	NOT_FOUND if shortId does not match an existing short
	 * 	FORBIDDEN, if the password is not correct;
	 */
    private Result<Void> client_deleteShort(String shortId, String password) {
        Response r = target.path( shortId )
				.queryParam(RestShorts.PWD, password).request()
				.accept(MediaType.APPLICATION_JSON)
				.delete();
		return super.toJavaResult(r, Void.class);
    }
    
    /**
	 * Retrieves a given Short.
	 * 
	 * @param shortId the unique identifier of the short to be retrieved
	 * @return (OK,Short), 
	 * 	NOT_FOUND if shortId does not match an existing short
	 */
    private Result<Short> client_getShort(String shortId) {
        Response r = target.path( shortId ).request()
				.accept(MediaType.APPLICATION_JSON)
				.get();
		return super.toJavaResult(r, Short.class);
    }

    /**
	 * Retrieves the list of identifiers of the shorts created by the given user.
	 * 
	 * @param userId the user that owns the requested shorts
	 * @return (OK, List<String>|empty list) or NOT_FOUND if the user does not exist
	 */
    private Result<List<String>> client_getShorts(String userId) {
        Response r = target.path( userId ).path(RestShorts.SHORTS)
                .request()
				.accept(MediaType.APPLICATION_JSON)
				.get();
		return super.toJavaResult(r, new GenericType<List<String>>(){});
    }

    /**
	 * Causes a user to follow the shorts of another user.
	 * 
	 * @param userId1     the user that will follow or cease to follow the
	 *                    followed user
	 * @param userId2     the followed user
	 * @param isFollowing flag that indicates the desired end status of the
	 *                    operation
	 * @param password 	  the password of the follower
	 * @return (OK,), 
	 * 	NOT_FOUND if any of the users does not exist
	 *  FORBIDDEN if the password is incorrect
	 */
    private Result<Void> client_follow(String userId1, String userId2, boolean isFollowing, String password) {
        Response r = target.path( userId1 ).path(userId2).path(RestShorts.FOLLOWERS)
                .queryParam(RestShorts.PWD, password)
                .request()
				.accept(MediaType.APPLICATION_JSON)
				.post(Entity.entity(isFollowing, MediaType.APPLICATION_JSON));
		return super.toJavaResult(r, Void.class);
    }

    /**
	 * Retrieves the list of users following a given user
	 * @param userId - the followed user
	 * @param password - the password of the followed user
	 * @return (OK, List<String>|empty list) the list of users that follow another user, or empty if the user has no followers 
	 * NOT_FOUND if the user does not exists
	 * FORBIDDEN if the password is incorrect
	 */
    private Result<List<String>> client_followers(String userId, String password) {
        Response r = target.path( userId ).path(RestShorts.FOLLOWERS)
                .queryParam(RestShorts.PWD, password).request()
				.accept(MediaType.APPLICATION_JSON)
				.get();
		return super.toJavaResult(r, new GenericType<List<String>>(){});
    }

    /**
	 * Adds or removes a like to a short
	 * 
	 * @param shortId  - the identifier of the post
	 * @param userId  - the identifier of the user
	 * @param isLiked - a flag with true to add a like, false to remove the like
	 * @return (OK,void) if the like was added/removed; 
	 * 	NOT_FOUND if either the short or the like being removed does not exist, 
	 *  CONFLICT if the like already exists.
	 *  FORBIDDEN if the password of the user is incorrect
	 *  BAD_REQUEST, otherwise
	 */
    private Result<Void> client_like(String shortId, String userId, boolean isLiked, String password) {
        Response r = target.path( shortId ).path(userId).path(RestShorts.LIKES)
                .queryParam(RestShorts.PWD, password)
                .request()
				.accept(MediaType.APPLICATION_JSON)
				.post(Entity.entity(isLiked, MediaType.APPLICATION_JSON));
		return super.toJavaResult(r, Void.class);
    }

    /**
	 * Returns all the likes of a given short
	 * 
	 * @param shortId the identifier of the short
	 * @param password the password of the owner of the short
	 * @return (OK,List<String>|empty list), 
	 * NOT_FOUND if there is no Short with the given shortId
	 * FORBIDDEN if the password is incorrect
	 */
    private Result<List<String>> client_likes(String shortId, String password) {
        Response r = target.path( shortId ).path(RestShorts.LIKES)
                .queryParam(RestShorts.PWD, password).request()
				.accept(MediaType.APPLICATION_JSON)
				.get();
		return super.toJavaResult(r, new GenericType<List<String>>(){});
    }
    
    /**
	 * Returns the feed of the user, sorted by age. The feed is the list of shorts made by
	 * the users followed by the user.
	 * 
	 * @param userId user of the requested feed
	 * @param password the password of the user
	 * @return (OK,List<String>|empty list)
	 * 	NOT_FOUND if the user does not exists
	 *  FORBIDDEN if the password is incorrect
	 */
    private Result<List<String>> client_getFeed(String userId, String password) {
        Response r = target.path( userId ).path(RestShorts.FEED)
                .request()
				.accept(MediaType.APPLICATION_JSON)
				.get();
		return super.toJavaResult(r, new GenericType<List<String>>(){});
    }

}
