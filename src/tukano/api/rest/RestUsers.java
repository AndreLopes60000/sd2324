package tukano.api.rest;

import java.util.List;

import jakarta.ws.rs.*;
import tukano.api.User;
import jakarta.ws.rs.core.MediaType;

@Path(RestUsers.PATH)
public interface RestUsers {

	String PATH = "/users";

	String PWD = "pwd";
	String QUERY = "query";
	String USER_ID = "userId";
	String SHORT_ID = "shortId";
	String USER_ID1 = "userId1";
	String USER_ID2 = "userId2";
	
	/**
	 * Creates a new user.
	 * @param user - User to be created
	 * @return OK - the userId of the user. 
	 * 		CONFLICT - if the name already exists. 
	 * 		BAD_REQUEST - otherwise.
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	String createUser(User user);
	
	/**
	 * Obtains the information on the user identified by userId
	 * @param userId - the userId of the user
	 * @param pwd  - the password of the user
	 * @return OK and the user object, if the userId exists and password matches the existing password; 
	 *         FORBIDDEN - if the password is incorrect; 
	 *         NOT_FOUND - if no user exists with the provided userId
	 */
	@GET
	@Path("/{" + USER_ID+ "}")
	@Produces(MediaType.APPLICATION_JSON)
	User getUser(@PathParam(USER_ID) String userId, @QueryParam( PWD ) String pwd);
	
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
	@PUT
	@Path("/{" + USER_ID+ "}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	User updateUser(@PathParam( USER_ID ) String userId, @QueryParam( PWD ) String pwd, User user);
	
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
	@DELETE
	@Path("/{" + USER_ID+ "}")
	@Produces(MediaType.APPLICATION_JSON)
	User deleteUser(@PathParam(USER_ID) String userId, @QueryParam(PWD) String pwd);
	
	/**
	 * Returns the list of users for which the pattern is a substring of the userId, case-insensitive. 
	 * The password of the users returned by the query must be set to the empty string "".
	 * 
	 * @param pattern - substring to search
	 * @return OK when the search was successful, regardless of the number of hits (including 0 hits). 
	 *         BAD_REQUEST - otherwise.
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	List<User> searchUsers(@QueryParam(QUERY) String pattern);	

	/**
	 * Removes shortId from list of likedShorts of every user in userIds
	 * @param shortId - the shortId of the short
	 * @param userIds - list of ids of the users to be updated
	 * @return OK 
	 */
	@PUT
	@Path("/removeLikes/{" + SHORT_ID+ "}")
	@Consumes(MediaType.APPLICATION_JSON)
	void removeShortFromLikes(@PathParam( SHORT_ID ) String shortId, List<String> userIds);


	 /**
     * Changes the info about following relations between two users
     * @param userId1 id of the first user
     * @param userId2 id of the second user
     * @param Ok
     */
	@PUT
	@Path("/following/{" + USER_ID1 + "}/{" + USER_ID2 + "}")
	@Consumes(MediaType.APPLICATION_JSON)
	void changeFollowingInfo(@PathParam( USER_ID1 ) String user1, @PathParam( USER_ID2 ) String user2, boolean isFollowing);
	
}
