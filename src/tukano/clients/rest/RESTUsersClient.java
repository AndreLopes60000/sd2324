package tukano.clients.rest;

import java.net.URI;
import java.util.List;
import tukano.api.User;
import tukano.helpers.Result;
import tukano.api.rest.RestUsers;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.GenericType;
import tukano.controlers.users.Users;
import jakarta.ws.rs.client.WebTarget;

public class RESTUsersClient extends RESTClient implements Users {


    /**
	 * Stores the target server
	 */
	final WebTarget target;


	/**
	 * Constructor
	 * @param serverURI server URI
	 */
	public RESTUsersClient(URI serverURI ) {
		super( serverURI );
		target = client.target( serverURI ).path( RestUsers.PATH );
	}

    @Override
    public Result<String> createUser(User user) {
        return super.reTry( () -> client_createUser(user));
    }

    @Override
    public Result<User> getUser(String userId, String pwd) {
        return super.reTry( () -> client_getUser(userId, pwd));
    }

    @Override
    public Result<User> updateUser(String userId, String pwd, User user) {
        return super.reTry( () -> client_updateUser(userId, pwd, user));
    }

    @Override
    public Result<User> deleteUser(String userId, String pwd) {
        return super.reTry( () -> client_deleteUser(userId, pwd));
    }

    @Override
    public Result<List<User>> searchUsers(String pattern) {
        return super.reTry( () -> client_searchUsers(pattern));
    }

	@Override
	public Result<Void> removeShortFromLikes(List<String> usersIds, String shortId) {
		return super.reTry( () -> client_removeShortFromLikes(usersIds, shortId));
	}


	
	@Override
	public Result<Void> changeFollowingInfo(String userId1, String userId2, boolean isFollowing) {
		return super.reTry( () -> client_changeFollowingInfo(userId1, userId2, isFollowing));
	}

	@Override
	public Result<Void> changeLikedShorts(String userId, String shortId, boolean isLiked) {
		return super.reTry( () -> client_changeLikedShorts(userId, shortId, isLiked));
	}

    //Private Methods

    /**
	 * Creates a new user.
	 * @param user - User to be created
	 * @return OK - the userId of the user. 
	 * 		CONFLICT - if the name already exists. 
	 * 		BAD_REQUEST - otherwise.
	 */
	private Result<String> client_createUser(User user) {
		Response r = target.request()
				.accept(MediaType.APPLICATION_JSON)
				.post(Entity.entity(user, MediaType.APPLICATION_JSON));
		return super.toJavaResult(r, String.class);
	}

	/**
	 * Obtains the information on the user identified by userId
	 * @param userId - the userId of the user
	 * @param pwd  - the password of the user
	 * @return OK and the user object, if the userId exists and password matches the existing password; 
	 *         FORBIDDEN - if the password is incorrect; 
	 *         NOT_FOUND - if no user exists with the provided userId
	 */
	private Result<User> client_getUser(String userID, String pwd) {
		Response r = target.path( userID )
				.queryParam(RestUsers.PWD, pwd).request()
				.accept(MediaType.APPLICATION_JSON)
				.get();
		return super.toJavaResult(r, User.class);
	}

	/**
	 * Modifies the information of a user. Value of null, in any field of the user argument, means the field will remain as unchanged 
	 * (the userId cannot be modified).
	 * @param userId - the userId of the user
	 * @param pwd  - password of the user
	 * @param user - Updated information
	 * @return OK and the updated user object, if the userId exists and password matches the existing password 
	 *         FORBIDDEN - if the password is incorrect 
	 *         NOT_FOUND - if no user exists with the provided userId 
	 *         BAD_REQUEST - otherwise.
	 */
	private Result<User> client_updateUser(String userID, String pwd, User user){
		Response r = target.path(userID)
				.queryParam(RestUsers.PWD, pwd)
				.request()
				.accept(MediaType.APPLICATION_JSON)
				.put(Entity.entity(user, MediaType.APPLICATION_JSON));
		return super.toJavaResult(r, User.class);
	}

	/**
	 * Deletes the user identified by userId
	 * @param userId - the userId of the user
	 * @param pwd - password of the user
	 * @return OK and the deleted user object, if the name exists and pwd matches the
	 *         existing password 
	 *         FORBIDDEN if the password is incorrect 
	 *         NOT_FOUND if no user exists with the provided name
	 *         BAD_REQUEST otherwise
	 */
	private Result<User> client_deleteUser(String userID, String pwd){
		Response r = target.path( userID )
				.queryParam(RestUsers.PWD, pwd).request()
				.accept(MediaType.APPLICATION_JSON)
				.delete();
		return super.toJavaResult(r, User.class);
	}

	/**
	 * Returns the list of users for which the pattern is a substring of the userId, case-insensitive. 
	 * The password of the users returned by the query must be set to the empty string "".
	 * 
	 * @param pattern - substring to search
	 * @return OK when the search was successful, regardless of the number of hits (including 0 hits). 
	 *         BAD_REQUEST - otherwise.
	 */
	private Result<List<User>> client_searchUsers(String pattern) {
		Response r = target.path("/").queryParam( RestUsers.QUERY, pattern).request()
				.accept(MediaType.APPLICATION_JSON)
				.get();
		return super.toJavaResult(r, new GenericType<List<User>>(){});
	}


	/**
	 * Removes shortId from list of likedShorts of every user in userIds
	 * @param shortId - the shortId of the short
	 * @param userIds - list of ids of the users to be updated
	 * @return OK 
	 */
	private Result<Void> client_removeShortFromLikes(List<String> usersIds, String shortId){
		Response r = target.path("messages/"+shortId)
				.request()
				.accept(MediaType.APPLICATION_JSON)
				.put(Entity.entity(usersIds, MediaType.APPLICATION_JSON));
		return super.toJavaResult(r, Void.class);
	}


	 /**
     * Changes the info about following relations between two users
     * @param userId1 id of the first user
     * @param userId2 id of the second user
     * @param isFollowing the final state of the relation between the two users
     */
	private Result<Void> client_changeFollowingInfo(String userId1, String userId2, boolean isFollowing){
		Response r = target.path("following").path(userId1).path(userId2)
				.request()
				.accept(MediaType.APPLICATION_JSON)
				.put(Entity.entity(isFollowing, MediaType.APPLICATION_JSON));
		return super.toJavaResult(r, Void.class);
	}

	/**
	 * Change the info about liked shorts
	 * @param userId the id of the user that like or unliked the short
	 * @param shortId the id of the short in question
	 * @param isLiked if it is to add or remove the like (true if so, and false if not)
	 * @return
	 */
	private Result<Void> client_changeLikedShorts(String userId, String shortId, boolean isLiked) {
		Response r = target.path("likedshort").path(userId).path(shortId)
				.request()
				.accept(MediaType.APPLICATION_JSON)
				.put(Entity.entity(isLiked, MediaType.APPLICATION_JSON));
		return super.toJavaResult(r, Void.class);
	}

	

	
    
}
