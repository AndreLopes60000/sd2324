package tukano.api;

import java.util.LinkedList;
import java.util.List;

import jakarta.persistence.*;

public class User {
	
	//TODO maybe there is a need to add a list of liked shorts to the user

	@Id
	private String userId;
	private String pwd;
	private String email;
	private String displayName;

	@ManyToMany
    @JoinTable(
        name = "following",
        joinColumns = @JoinColumn(name = "userId"),
        inverseJoinColumns = @JoinColumn(name = "followedUserId")
    )
    private List<User> follows;

    @ManyToMany(mappedBy = "follows")
    private List<User> followers;

	public User() {}
	
	public User(String userId, String pwd, String email, String displayName) {
		this.pwd = pwd;
		this.email = email;
		this.userId = userId;
		this.displayName = displayName;
		this.follows = new LinkedList<>();
		this.followers = new LinkedList<>();
	}

	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	public String userId() {
		return userId;
	}
	
	public String pwd() {
		return pwd;
	}
	
	public String email() {
		return email;
	}
	
	public String displayName() {
		return displayName;
	}
	
	@Override
	public String toString() {
		return "User [userId=" + userId + ", pwd=" + pwd + ", email=" + email + ", displayName=" + displayName + "]";
	}

	public List<User> getFollowers(){
		return followers;
	}

	public List<User> getFollowing(){
		return followers;
	}

	public void removeFollowing(User user){
		follows.remove(user);
	}
	
	public void removeFollower(User user){
		followers.remove(user);
	}
}
