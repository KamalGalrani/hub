package eu.ailao.hub.users;

import eu.ailao.hub.concepts.ConceptMemorizer;

import java.util.HashMap;

/**
 * Created by Petr Marek on 14.01.2016.
 * Remembers users by their id, number of users and handles creation of new users
 */
public class UserMapper {
	HashMap<Integer, User> userMap=new HashMap<>();
	int numberOfUsers=0;

	/**
	 * Returns user from hashMap of users
	 * @param id is of desired user
	 * @return user
	 */
	private User getUserByID(int id){
		User user=userMap.get(id);
		return user;
	}

	/**
	 * Adds user to hashMap of users
	 * @param user user to add
	 */
	private void addUser(User user){
		userMap.put(numberOfUsers,user);
		numberOfUsers++;
	}

	/**
	 * Gets user with id or creates new, if id doesn't exists
	 * @param userID id of user
	 * @return user
	 */
	public User getUser(String userID){
		User user;
		if (userID==null || userID.equals("")){
			user=createNewUser(numberOfUsers);
		}else{
			int id=Integer.parseInt(userID);
			user=findUserWithID(id);
		}
		return user;
	}

	/**
	 * Creates new user with id and put it into hasmMap of users
	 * @param id desired id
	 * @return user
	 */
	private User createNewUser(int id){
		User user=new User(new ConceptMemorizer(),id);
		addUser(user);
		return user;
	}

	/**
	 * Finds user with specified id, or creates one
	 * @param id id of user
	 * @return user
	 */
	private User findUserWithID(int id){
		User user=getUserByID(id);
		if (user==null){
			user=createNewUser(id);
		}
		return user;
	}
}