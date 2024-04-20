package tukano.controlers.shorts;

import static tukano.helpers.Constants.USERS_SERVICE;

import java.net.URI;
import java.util.List;
import java.util.logging.Logger;

import tukano.api.Short;
import tukano.api.User;
import tukano.clients.UsersClientFactory;
import tukano.controlers.users.Users;
import tukano.helpers.Discovery;
import tukano.helpers.Result;
import tukano.repositories.shorts.ShortsRepository;
import tukano.repositories.shorts.ShortsRepositoryImplementation;
import tukano.resources.rest.RESTShortsResource;

public class ShortsController implements Shorts{

    
    private static final Discovery discovery = Discovery.getInstance();

    private static Logger Log = Logger.getLogger(RESTShortsResource.class.getName());

    private static ShortsRepository shortsRepository = ShortsRepositoryImplementation.getInstance();

    private Users usersClient;

    public ShortsController(){}

    @Override
    public Result<Short> createShort(String userId, String password) {
        Log.info("createShort from: " + userId + " with pass "+ password);
        if(userId == null || password == null){
            Log.info("Input strings invalid.");
            return Result.error(Result.ErrorCode.BAD_REQUEST);
        }

        var userExistsResult = doesUserExist(userId,password);
        if(!userExistsResult.isOK()){
            return Result.error(userExistsResult.error());
        }
        return Result.ok(shortsRepository.createShort(userId, password));
    }

    @Override
    public Result<Void> deleteShort(String shortId, String password) {
        var getShortResult = getShort(shortId);
        if(!getShortResult.isOK()){
            return Result.error(getShortResult.error());
        }
        Short shortToDelete = getShortResult.value();
        String ownerId = shortToDelete.getOwnerId();

        var userExistsResult = doesUserExist(ownerId,password);
        if(!userExistsResult.isOK()){
            return Result.error(userExistsResult.error());
        }
        List<String> usersLiked = shortToDelete.getLikes();
        removeLikedShort(usersLiked, shortId);
        return Result.ok(null);

    }

    @Override
    public Result<Short> getShort(String shortId) {
        Short shortRetrieved = shortsRepository.getShort(shortId);
        if(shortRetrieved == null){
            Log.info("No user with given ID.");
            return Result.error(Result.ErrorCode.NOT_FOUND);
        }
        return Result.ok(shortRetrieved);

    }

    @Override
    public Result<List<String>> getShorts(String userId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getShorts'");
    }

    @Override
    public Result<Void> follow(String userId1, String userId2, boolean isFollowing, String password) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'follow'");
    }

    @Override
    public Result<List<String>> followers(String userId, String password) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'followers'");
    }

    @Override
    public Result<Void> like(String shortId, String userId, boolean isLiked, String password) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'like'");
    }

    @Override
    public Result<List<String>> likes(String shortId, String password) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'likes'");
    }

    @Override
    public Result<List<String>> getFeed(String userId, String password) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getFeed'");
    }

    //Private Methods

    private URI makeURI(String service){
        URI uri = discovery.knownUrisOf(service, 1)[0];
        return uri;
    }

    /**
     * Checks if a user exists in the RESTUserServer
     * (note: use when pwd access is given)
     * @param userId user's id 
     * @param pwd user's pwd
     * @return Object of type Result<Void>
     */
    private Result<Void> doesUserExist(String user, String pwd) {
        // Makes a REST request
        usersClient = UsersClientFactory.get(makeURI(USERS_SERVICE));
        Result<User> result = usersClient.getUser(user, pwd);

        if(!result.isOK()){
            return Result.error(result.error());
        }
        return Result.ok();
    }

    /**
     * Checks if a given user exists in the RESTUsersServer
     * (note: use when pwd access is not given)
     * @param userId user's id
     * @return Object of type Result<Void>
     */
    private Result<Void> doesUserExist(String userId){
        // Makes a REST request
        usersClient = UsersClientFactory.get(makeURI(USERS_SERVICE));
        Result<List<User>> result = usersClient.searchUsers(userId);

        if(!result.isOK()){
            return Result.error(result.error());
        }

        List<User> usersFound = result.value();

        if(usersFound != null){
            for (User u : usersFound) {
                if (u.getUserId().equals(userId)){
                    return Result.ok();
                }
            }
        }

        Log.info("User does not exist.");
        return Result.error(Result.ErrorCode.NOT_FOUND);
    }
    
    /**
     * Removes the short from the users liked shorts list
     * @param usersIds list of ids of the users who liked the short
     * @param shortID id of the short to be removed from the lists of the users
     */
    private void removeLikedShort(List<String> usersIds, String shortID){
        usersClient = UsersClientFactory.get(makeURI(USERS_SERVICE));
        usersClient.removeShortFromLikes(usersIds, shortID);
    }


}
