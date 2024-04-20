package tukano.controlers.shorts;

import static tukano.helpers.Constants.USERS_SERVICE;

import java.net.URI;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collector;
import java.util.stream.Collectors;

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
        shortsRepository.deleteShort(shortId, password);
        return Result.ok(null);

    }

    @Override
    public Result<Short> getShort(String shortId) {
        Short shortRetrieved = shortsRepository.getShort(shortId);
        if(shortRetrieved == null){
            Log.info("No short with given ID.");
            return Result.error(Result.ErrorCode.NOT_FOUND);
        }
        return Result.ok(shortRetrieved);
    }

    @Override
    public Result<List<String>> getShorts(String userId) {
        Log.info("getShorts from: " + userId);
        if(userId == null){
            Log.info("Input string invalid.");
            return Result.error(Result.ErrorCode.BAD_REQUEST);
        }

        var userExistsResult = doesUserExist(userId);
        if(!userExistsResult.isOK()){
            return Result.error(userExistsResult.error());
        }

        return Result.ok(shortsRepository.getShorts(userId));
    }

    @Override
    public Result<Void> follow(String userId1, String userId2, boolean isFollowing, String password) {
        Log.info(userId1+" follows ("+isFollowing+") "+userId2);
        if(userId1 == null || userId2 == null || password == null){
            Log.info("Invalid inputs.");
            return Result.error(Result.ErrorCode.BAD_REQUEST);
        }
        
        var user1ExistsResult = doesUserExist(userId1, password);
        if(!user1ExistsResult.isOK()){
            return Result.error(user1ExistsResult.error());
        }

        var user2ExistsResult = doesUserExist(userId2);
        if(!user2ExistsResult.isOK()){
            return Result.error(user2ExistsResult.error());
        }

        changeFollowingInfo(userId1, userId2, isFollowing);

        return Result.ok();
    }

    @Override
    public Result<List<String>> followers(String userId, String password) {
        Log.info("Checking "+userId+" followers, with pwd "+password);
        if(userId == null || password == null){
            Log.info("Invalid inputs.");
            return Result.error(Result.ErrorCode.BAD_REQUEST);
        }

        var userExistsResult = doesUserExist(userId, password);
        if(!userExistsResult.isOK()){
            return Result.error(userExistsResult.error());
        }

        User user = userExistsResult.value();
        return Result.ok(user.getFollowers());
    }

    @Override
    public Result<Void> like(String shortId, String userId, boolean isLiked, String password) {
        Log.info("Changing "+userId+" like on short "+shortId+" with pwd "+password+" final state is: "+isLiked);
        if(shortId == null || userId == null || password == null){
            Log.info("Invalid inputs.");
            return Result.error(Result.ErrorCode.BAD_REQUEST);
        }
        
        var existingShortResult = getShort(shortId);
        if(!existingShortResult.isOK()){
            return Result.error(existingShortResult.error());
        }
        Short shortFound = existingShortResult.value();
        List<String> usersLikes = shortFound.getLikes();
        if(!isLiked && !usersLikes.contains(userId)){
            return Result.error(Result.ErrorCode.NOT_FOUND);
        }
        if(isLiked && usersLikes.contains(userId)){
            return Result.error(Result.ErrorCode.CONFLICT);
        }
        
        var existingUserResult = doesUserExist(userId, password);
        if(!existingUserResult.isOK()){
            return Result.error(existingUserResult.error());
        }

        shortsRepository.updateShortLikes(userId, shortId, isLiked);
        changeUserLikedShorts(userId, shortId, isLiked);

        return Result.ok();
    }

    @Override
    public Result<List<String>> likes(String shortId, String password) {
        var getShortResult = getShort(shortId);
        if(!getShortResult.isOK()){
            return Result.error(getShortResult.error());
        }
        Short infoShort = getShortResult.value();
        String ownerId = infoShort.getOwnerId();
        var userExistsResult = doesUserExist(ownerId, password);
        if(!userExistsResult.isOK()){
            return Result.error(userExistsResult.error());
        }

        return Result.ok(infoShort.getLikes());
    }

    @Override
    public Result<List<String>> getFeed(String userId, String password) {
        var userExistsResult = doesUserExist(userId, password);
        if(!userExistsResult.isOK()){
            return Result.error(userExistsResult.error());
        }
        List<String> followingUsers = userExistsResult.value().getFollowing();
        List<Short> allShorts = new LinkedList<>();
        for(String user : followingUsers){
            allShorts.addAll(shortsRepository.getObjectShorts(user));
        }

        return Result.ok(getOrderedShorts(allShorts));
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
    private Result<User> doesUserExist(String user, String pwd) {
        // Makes a REST request
        usersClient = UsersClientFactory.get(makeURI(USERS_SERVICE));
        Result<User> result = usersClient.getUser(user, pwd);

        if(!result.isOK()){
            return Result.error(result.error());
        }
        return result;
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


     /**
     * Changes the info about following relations between two users
     * @param userId1 id of the first user
     * @param userId2 id of the second user
     * @param isFollowing the final state of the relation between the two users
     */
    private void changeFollowingInfo(String userId1, String userId2, boolean isFollowing){
        usersClient = UsersClientFactory.get(makeURI(USERS_SERVICE));
        usersClient.changeFollowingInfo(userId1, userId2, isFollowing);
    }

    /**
     * Changes the info about the likes of a short
     * @param userId the user liking the short
     * @param shortId the short liked
     * @param isLiked if it is to add or remove the like (true if so, and false if not)
     */
    private void changeUserLikedShorts(String userId, String shortId, boolean isLiked){
        usersClient = UsersClientFactory.get(makeURI(USERS_SERVICE));
        usersClient.changeLikedShorts(userId, shortId, isLiked);
    }

    /**
     * Method to order the list of shorts
     * @param shorts shorts to be listed
     * @return an ordered list of shorts
     */
    private List<String> getOrderedShorts(List<Short> shorts){
        return shorts.stream().sorted(Comparator.comparing(Short::getTimestamp)).map(Short::getShortId).collect(Collectors.toList());
    }

}
