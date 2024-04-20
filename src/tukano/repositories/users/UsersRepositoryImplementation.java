package tukano.repositories.users;

import java.util.List;
import tukano.api.User;
import tukano.helpers.Hibernate;

public class UsersRepositoryImplementation implements UsersRepository{

    private static UsersRepository singleton;


    public static synchronized UsersRepository getInstance(){
        if (singleton == null){
            singleton = new UsersRepositoryImplementation();
        }
        return singleton;
    }

    @Override
    public String createUser(User user) {
        if(this.getUser(user.getUserId()) == null)
            Hibernate.getInstance().persist(user);
        return user.getUserId();
    }

    @Override
    public User getUser(String userId) {
        return this.getDBUser(userId);
    }

    @Override
    public User updateUser(String userId, User user) {
        User userToModify = this.getUser(userId);
        String userPwd = user.getPwd();
        String userEmail = user.getEmail();
        String userName = user.getDisplayName();

        if(userName != null)
            userToModify.setDisplayName(userName);
        if(userPwd != null)
            userToModify.setPwd(userPwd);
        if(userEmail != null)
            userToModify.setEmail(userEmail);

        Hibernate.getInstance().update(userToModify);

        return userToModify;
    }

    @Override
    public User deleteUser(String userId) {
        User userToRemove = this.getUser(userId);
        removeUserFollowers(userToRemove);
        removeUserFollowings(userToRemove);
        Hibernate.getInstance().delete(userToRemove);
        return userToRemove;
    }

    @Override
    public List<User> searchUsers(String pattern) {
        return Hibernate.getInstance().jpql("SELECT new User(u.userId, '', u.email, u.displayName) FROM User u WHERE LOWER(u.userId) LIKE '%"+pattern.toLowerCase()+"%'", User.class);
    }

    private User getDBUser(String userId){
        List<User> users = Hibernate.getInstance().sql("SELECT * FROM User u WHERE u.userId = "+userId+"", User.class);
        if (users.isEmpty())
            return null;
        return users.get(0);
    }

    private void removeUserFollowers(User user){
        List<String> userFollowersIds = user.getFollowers();
        for (String userId : userFollowersIds) {
            User u = getDBUser(userId);
            u.removeFollowing(user.getUserId());
            Hibernate.getInstance().update(u);
        }
    }

    private void removeUserFollowings(User user){
        List<String> usersFollowingList = user.getFollowers();
        for (String userId : usersFollowingList) {
            User u = getDBUser(userId);
            u.removeFollower(user.getUserId());
            Hibernate.getInstance().update(u);
        }
    }

    @Override
    public void removeShortFromLikes(List<String> usersIds, String shortId) {
        for(String userId: usersIds){
            User user = getDBUser(userId);
            user.removeLikedShort(shortId);
            Hibernate.getInstance().update(user);
        }
    }

    @Override
    public void changeFollowingInfo(String userId1, String userId2, boolean isFollowing) {
        Hibernate instance = Hibernate.getInstance();
        User user1 = getDBUser(userId1);
        User user2 = getDBUser(userId2);
        user1.changeFollowing(userId2, isFollowing);
        instance.update(user1);
        user2.changeFollowers(userId1, isFollowing);
        instance.update(user2);
    }
}
