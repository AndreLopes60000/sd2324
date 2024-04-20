package tukano.repositories.shorts;

import java.util.List;
import tukano.api.Short;

public interface ShortsRepository {
    
	
	String NAME = "shorts";
	
	/**
	 * Creates a new short, generating its unique identifier. 
	 * The short will include the blob storage location where the media should be uploaded.
	 * 
	 * @param userId - the owner of the new short
	 * @param password - the password of owner of the new short
	 * @return (OK, Short) if the short was created;
	 * NOT FOUND, if the owner of the short does not exist;
	 * FORBIDDEN, if the password is not correct;
	 * BAD_REQUEST, otherwise.
	 */
	Short createShort(String userId, String password);

	/**
	 * Deletes a given Short.
	 * 
	 * @param shortId the unique identifier of the short to be deleted
	 * @return (OK,void), 
	 * 	NOT_FOUND if shortId does not match an existing short
	 * 	FORBIDDEN, if the password is not correct;
	 */
	Void deleteShort(String shortId, String password);
	
	/**
	 * Retrieves a given Short.
	 * 
	 * @param shortId the unique identifier of the short to be retrieved
	 * @return (OK,Short), 
	 * 	NOT_FOUND if shortId does not match an existing short
	 */
	Short getShort(String shortId);
	
	/**
	 * Retrieves the list of identifiers of the shorts created by the given user.
	 * 
	 * @param userId the user that owns the requested shorts
	 * @return (OK, List<String>|empty list) or NOT_FOUND if the user does not exist
	 */
	List<String> getShorts( String userId );

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
	Void like(String shortId, String userId, boolean isLiked, String password);

	/**
	 * Returns all the likes of a given short
	 * 
	 * @param shortId the identifier of the short
	 * @param password the password of the owner of the short
	 * @return (OK,List<String>|empty list), 
	 * NOT_FOUND if there is no Short with the given shortId
	 * FORBIDDEN if the password is incorrect
	 */
	List<String> likes(String shortId, String password);
    
}
