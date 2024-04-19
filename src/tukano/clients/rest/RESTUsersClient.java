package tukano.clients.rest;

import java.net.URI;
import java.util.List;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import tukano.api.User;
import tukano.api.rest.RestUsers;
import tukano.controlers.users.Users;
import tukano.helpers.Result;

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
    
}