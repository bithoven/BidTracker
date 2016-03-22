package phome.bidtracker.tracker;

import java.util.Set;

import phome.bidtracker.models.Bid;


/**
 * BidTracker Interface 
 * 
 */

public interface BidTracker {
    
	/**
	 * Handles bid request
	 * @param ItemId
	 * @param UserId
	 * @param amt
	 * @return <code>true</code> if bid was successfully registered <br/>
	 * 		   <code>false</code> if bid was declined and outbid
	 */
	public boolean bid(String itemId, String userId, double amt);
	
    
	/**
	 * Fetches the current winning bid for an item
	 * @param itemId
	 * @return
	 * 		winning bid <br> 
	 * 		null if no bid 
	 */
    public Bid getWinningBid(String itemId);
    
    
    /**
     * 
     * Retrieves List of bids placed on an item
     * 
     * @param itemId
     * @return
     * 		List of exiting bids <br> 
     * 		null if no bid
     */
    Iterable<Bid> getBidsforItem(String itemId);
    
    
    /**
     * Retrieves Set of Unique Items on which the User has placed bids so far.
     * 
     * @param userId
     * @return
     * 		Set of items for the user <br>
     * 		null if no bids placed by the user
     * 
     */
    Set<String> getItemsforUser(String userId);
    
}
