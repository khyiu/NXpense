package nxpense.service.api;

public interface UserService {
	
	/**
	 * Creates a new user if the following conditions are fulfilled:<br>
	 * <ul>
	 * 	<li>the specified user email is not already used by another user</li>
	 *  <li>the specified password complies with the password pattern</li>
	 *  <li>the specified password repetition matches the specified password</li>
	 * </ul>
	 * @param email email to be associated with the new user
	 * @param password password to be associated with the new user
	 * @param passwordRepeat password confirmation
	 */
	public void createUser(String email, char [] password, char [] passwordRepeat); 
}
