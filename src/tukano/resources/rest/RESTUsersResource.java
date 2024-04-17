package tukano.resources.rest;

import java.util.List;

import com.fasterxml.jackson.core.util.ReadConstrainedTextBuffer;

import tukano.api.User;
import tukano.api.rest.RestUsers;
import tukano.controlers.users.Users;

public class RESTUsersResource extends RESTResource implements RestUsers{

    private final UsersInteractor impl;

    public RESTUsersResource(){
        this.impl = null;

    }

    @Override
    public String createUser(User user) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createUser'");
    }

    @Override
    public User getUser(String userId, String pwd) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getUser'");
    }

    @Override
    public User updateUser(String userId, String pwd, User user) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateUser'");
    }

    @Override
    public User deleteUser(String userId, String pwd) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteUser'");
    }

    @Override
    public List<User> searchUsers(String pattern) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'searchUsers'");
    }
    
}
