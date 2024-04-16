package tukano.repositories.users;

import java.util.Map;
import java.util.List;
import tukano.api.User;
import java.util.HashMap;
import java.util.LinkedList;

public class UsersRepositoryImplementation implements UsersRepository{

    private static UsersRepository singleton;

    private final Map<String, User> users = new HashMap<>();

    public static synchronized UsersRepository getInstance(){
        if (singleton == null){
            singleton = new UsersRepositoryImplementation();
        }
        return singleton;
    }

    @Override
    public String createUser(User user) {
        if(users.putIfAbsent(user.getUserId(), user) != null){
            return null;
        }
        return user.getUserId();
    }

    @Override
    public User getUser(String userId, String pwd) {
        return users.get(userId);
    }

    @Override
    public User updateUser(String userId, String pwd, User user) {
        User userToModify = users.get(userId);
        String userPwd = user.getPwd();
        String userEmail = user.getEmail();
        String userName = user.getDisplayName();

        if(userName != null)
            userToModify.setDisplayName(userName);
        if(userPwd != null)
            userToModify.setPwd(userPwd);
        if(userEmail != null)
            userToModify.setEmail(userEmail);

        return userToModify;
    }

    @Override
    public User deleteUser(String userId, String pwd) {
        return users.remove(userId);
    }

    @Override
    public List<User> searchUsers(String pattern) {
        List<User> usersFound = new LinkedList<>();
        for(User user : users.values()){
            if(user.getUserId().toUpperCase().contains(pattern.toUpperCase())){
                User thisUser = new User(user.getUserId(), "", user.getEmail(), user.getDisplayName());
                usersFound.add(thisUser);
            }
        }
        return usersFound;
    }
    
}
