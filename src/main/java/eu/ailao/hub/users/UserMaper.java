package eu.ailao.hub.users;

import java.util.HashMap;

/**
 * Created by Petr Marek on 14.01.2016.
 */
public class UserMaper {
	HashMap<String, User> userMap=new HashMap<>();

	public User getUserByID(String id){
		User user=userMap.get(id);
		return user;
	}

	public void addUser(String id, User user){
		userMap.put(id,user);
	}
}
