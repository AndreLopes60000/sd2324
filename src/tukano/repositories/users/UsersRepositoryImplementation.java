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
    public User getUser(String userId, String pwd) {
        return this.getUser(userId);
    }

    @Override
    public User updateUser(String userId, String pwd, User user) {
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
    public User deleteUser(String userId, String pwd) {
        User userToRemove = this.getUser(userId);
        Hibernate.getInstance().delete(userToRemove);
        return userToRemove;
    }

    @Override
    public List<User> searchUsers(String pattern) {
        return Hibernate.getInstance().jpql("SELECT new User(u.userId, '', u.email, u.displayName) FROM User u WHERE LOWER(u.userId) LIKE '%"+pattern.toLowerCase()+"%'", User.class);
    }

    private User getUser(String userId){
        List<User> users = Hibernate.getInstance().sql("SELECT * FROM User u WHERE u.userId = "+userId+"", User.class);
        if (users.isEmpty())
            return null;
        return users.get(0);
    }
}
