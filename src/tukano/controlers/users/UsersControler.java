package tukano.controlers.users;

import java.util.List;
import java.util.logging.Logger;
import tukano.api.User;
import tukano.api.java.Result;
import tukano.helpers.Discovery;
import tukano.repositories.users.*;
import tukano.resources.rest.RESTUsersResource;

public class UsersControler implements Users{

    private static final Discovery discovery = Discovery.getInstance();

    private static Logger Log = Logger.getLogger(RESTUsersResource.class.getName());

    private static UsersRepository usersRepository = UsersRepositoryImplementation.getInstance();

    public UsersControler(){}

    @Override
    public Result<String> createUser(User user) {
        Log.info("createUser : " + user);
        if(user.getDisplayName() == null || user.getEmail() == null || user.getPwd() == null || user.getUserId() == null){
            Log.info("User object is invalid.");
            return Result.error(Result.ErrorCode.BAD_REQUEST);
        }

        String createdId = usersRepository.createUser(user);
        if(createdId == null){
            Log.info("User with given ID already exists");
            return Result.error(Result.ErrorCode.CONFLICT);
        }

        return Result.ok(createdId);
    }

    @Override
    public Result<User> getUser(String userId, String pwd) {
        Log.info("getUser : user = " + userId + "; pwd = " + pwd);
        if(userId == null || pwd == null) {
            Log.info("Name or password null.");
            return Result.error(Result.ErrorCode.BAD_REQUEST);
        }

        User user = usersRepository.getUser(userId);
        if(user == null){
            Log.info("No user with given ID.");
            return Result.error(Result.ErrorCode.NOT_FOUND);
        }

        if(!user.getPwd().equals(pwd)){
            Log.info("Passwords don't match");
            return Result.error(Result.ErrorCode.FORBIDDEN);
        }

        return Result.ok(user);
    }

    @Override
    public Result<User> updateUser(String userId, String pwd, User user) {
        Log.info("updateUser : user = " + userId + "; pwd = " + pwd + "; user = " + user);
        if(userId == null || pwd == null) {
            Log.info("Name or password null.");
            return Result.error(Result.ErrorCode.BAD_REQUEST);
        }

        User userToUpdate = usersRepository.getUser(userId);
        if(userToUpdate == null){
            Log.info("No user with given ID.");
            return Result.error(Result.ErrorCode.NOT_FOUND);
        }

        if(!userToUpdate.getPwd().equals(pwd)){
            Log.info("Passwords don't match");
            return Result.error(Result.ErrorCode.FORBIDDEN);
        }

        User modifiedUser = usersRepository.updateUser(userId, user);
        return Result.ok(modifiedUser);
    }

    // TODO Need to remove the information associated with shorts info (followed and likes on shorts)
    @Override
    public Result<User> deleteUser(String userId, String pwd) {
        Log.info("deleteUser : user = " + userId + "; pwd = " + pwd);
        if(userId == null || pwd == null) {
            Log.info("Name or password null.");
            return Result.error(Result.ErrorCode.BAD_REQUEST);
        }

        User user = usersRepository.getUser(userId);
        if(user == null){
            Log.info("No user with given ID.");
            return Result.error(Result.ErrorCode.NOT_FOUND);
        }

        if(!user.getPwd().equals(pwd)){
            Log.info("Passwords don't match");
            return Result.error(Result.ErrorCode.FORBIDDEN);
        }

        usersRepository.deleteUser(userId);
        return Result.ok(user);
    }

    @Override
    public Result<List<User>> searchUsers(String pattern) {
        Log.info("searchUsers : pattern = " + pattern);

        // Is pattern valid?
        if(pattern == null || pattern.isEmpty()) {
            Log.info("Pattern is null or empty.");
            return Result.error(Result.ErrorCode.BAD_REQUEST);
        }
        return Result.ok(usersRepository.searchUsers(pattern));
    }
    
}
