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
    private List<String> follows;

    @ManyToMany(mappedBy = "follows")
    private List<String> followers;

	private List<String> likedShorts;

	public User() {}
	
	public User(String userId, String pwd, String email, String displayName) {
		this.pwd = pwd;
		this.email = email;
		this.userId = userId;
		this.displayName = displayName;
		this.follows = new LinkedList<>();
		this.followers = new LinkedList<>();
		this.likedShorts = new LinkedList<>();
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

	public List<String> getFollowers(){
		return followers;
	}

	public List<String> getFollowing(){
		return followers;
	}

	public void removeFollowing(String userId){
		follows.remove(userId);
	}
	
	public void removeFollower(String userId){
		followers.remove(userId);
	}

	public void removeLikedShort(String shortId){
		likedShorts.remove(shortId);
	}

	public void changeFollowing(String userToFollow, boolean isFollowing){
		if(isFollowing && !follows.contains(userToFollow))
			follows.add(userToFollow);
		else if (!isFollowing)
			follows.remove(userToFollow);

	}

	public void changeFollowers(String userFollowingMe, boolean isFollowing){
		if(isFollowing && !followers.contains(userFollowingMe))
			followers.add(userFollowingMe);
		else if (!isFollowing)
			followers.remove(userFollowingMe);

	}
}
