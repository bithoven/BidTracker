package phome.bidtracker.store;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Lock free implementation of IUserBidHistory interface.
 * @see BidStoreLockFree 
 * 
 * @author phome
 *
 */
public class UserBidHistoryStoreLockFreeImpl implements UserBidHistoryStore
{
	private final ConcurrentMap<String, Set<String>> userstore;  

	/**
	 * Instantiates the user history store with a CHM with concurrency level based on CPU cores available
	 * 
	 */
	public UserBidHistoryStoreLockFreeImpl() {
		int concurrencyLevel = Runtime.getRuntime().availableProcessors() * 2;
		userstore = new ConcurrentHashMap<String, Set<String>>(100, 0.75f, concurrencyLevel);
	}


	/**
	 * 
	 * Saves the ItemId to the list of Items for the user.
	 * If item is already there in the list the User store remains unupdated
	 * 
	 * <br><br> In event of conflict on the same user the lock free algorithm kicks in 
	 * to retry in a convergent way. However, this is highly unlikely by the use case
	 * i.e. A regular user is not expected to place bids simultaneously.
	 * However, a lock free algorithm is highly beneficial in such scenarios as well, since in majority of the cases
	 * ops will proceed on the first iteration itself.
	 * 
	 * <br> Note that we short circuit the iteration as soon as we find that item is already in the Set.
	 * <br>Item can be pre existing in the set because of:
	 * <br> A previous bid on the same item
	 * <br>OR 
	 * <br> Concurrent bid by the same user on the same item, which is highly unlikely for a regular user
	 *  but is a genuine pattern Denial Of Service attackers try
	 * 
	 * @see BidStoreLockFree	  
	 * 
	 * @param userId
	 * @param itemId
	 * 
	 */
	@Override
	public void save(final String userId, final String itemId) {
		Set<String> currItems = userstore.get(userId);
		Set<String> updatableCopy = prepareUpdatableCopy(currItems);
		boolean success = false;

		//loop until the insert passes or the replace passes
		//exit loop on success OR short circuit if item is already added
		while(!success && updatableCopy.add(itemId))
		{	    		    	
			if(currItems == null ){
				currItems = userstore.putIfAbsent(userId, updatableCopy);				
				success = currItems == null;      
				if(!success){
					updatableCopy = prepareUpdatableCopy(currItems);
				}
			}
			else{
				success = userstore.replace(userId, currItems, updatableCopy);
				if(!success){
					currItems = userstore.get(itemId);
					updatableCopy = prepareUpdatableCopy(currItems);	
				}				
			} 

		};  	        
	}

	/**
	 * Returns a unmodifiable view of the items set for a given user
	 * 
	 * @param userId
	 * @return
	 * 
	 */
	@Override
	public Set<String> itemsForUser(final String userId){
		Set<String> currItems = userstore.get(userId);
		return currItems == null? null : Collections.unmodifiableSet(currItems);
	}

	
	/**
	 * creates a copy of the items 
	 * If null then an empty Set is returned
	 * @param currItems
	 * @return
	 * 
	 */
	private Set<String> prepareUpdatableCopy(Set<String> currItems){
		Set<String> updatableCopy;
		if(currItems == null){
			updatableCopy = new HashSet<String>(); 

		}else{
			updatableCopy = new HashSet<String>(currItems); 
		}
		return updatableCopy;
	}


}
