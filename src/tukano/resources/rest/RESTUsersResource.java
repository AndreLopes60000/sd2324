package tukano.resources.rest;

import java.util.List;

import jakarta.inject.Singleton;
import tukano.api.User;
import tukano.api.rest.RestUsers;
import tukano.controlers.users.Users;
import tukano.controlers.users.UsersControler;

@Singleton
public class RESTUsersResource extends RESTResource implements RestUsers{

    private final Users impl;

    public RESTUsersResource(){
        this.impl = new UsersControler();

    }

    @Override
    public String createUser(User user) {
        return super.fromJavaResult(impl.createUser(user));
    }

    @Override
    public User getUser(String userId, String pwd) {
        return super.fromJavaResult(impl.getUser(userId,pwd));
    }

    @Override
    public User updateUser(String userId, String pwd, User user) {
        return super.fromJavaResult(impl.updateUser(userId,pwd,user));
    }

    @Override
    public User deleteUser(String userId, String pwd) {
        return super.fromJavaResult(impl.deleteUser(userId, pwd));
    }

    @Override
    public List<User> searchUsers(String pattern) {
        return super.fromJavaResult(impl.searchUsers(pattern));
    }

    @Override
    public void removeShortFromLikes(String shortId, List<String> userIds) {
        super.fromJavaResult(impl.removeShortFromLikes(userIds, shortId));
    }

    @Override
    public void changeFollowingInfo(String user1, String user2, boolean isFollowing) {
        super.fromJavaResult(impl.changeFollowingInfo(user1, user2, isFollowing));
    }
    
}
