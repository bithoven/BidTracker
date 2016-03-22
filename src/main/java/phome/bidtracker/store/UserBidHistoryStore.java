package phome.bidtracker.store;

import java.util.Set;


public interface UserBidHistoryStore {
	
	/**
	 * Saves the Item to Users Active Item List if not already there
	 * @param userId
	 * @param itemId
	 */
	public void save(final String userId, final String itemId);
	
	
	/**
	 * Retrives a Set of Items on which the UserHas placed bids On
	 * @param userId
	 * @return
	 * 		- Set of items <br>
	 * 		- null if no bids on any item placed by the user
	 */
	public Set<String> itemsForUser(final String userId);

}
