package phome.bidtracker.tracker;

import java.util.Set;

import phome.bidtracker.models.Bid;
import phome.bidtracker.store.BidStore;
import phome.bidtracker.store.UserBidHistoryStore;

/**
 * A bid tracker implementation which can be composed/dependency injected
 * with IBidStore and IUserBidHistoryStore implementations
 * 
 */
public class BidTrackerImpl implements BidTracker {

    private final BidStore bidStore;    
    private final UserBidHistoryStore userItemsStore;    

    /**
     * Instantiates a BidTracker with a bid store and a user history store
     * Disallows null args
     * @param bidStore
     * @param userItemsStore
     */
    public BidTrackerImpl(BidStore bidStore, UserBidHistoryStore userItemsStore) {
    	if(bidStore == null || userItemsStore == null){
    		throw new IllegalArgumentException("Cannot instantiate BidTracker with null args");
    	}
        this.bidStore = bidStore;
        this.userItemsStore = userItemsStore;
    }

    /**
     * Attempts to save the bid to bid store
     * If successful updates the User History Store 
     * @param ItemId
     * @param UserId
     * @param amt
     * @return true if bid was placed <br>
     * 		   false if bid was declined
     */
    @Override
    public boolean bid(String itemId, String userId, double amt){    	
    	Bid bid = new Bid( itemId, userId, amt);    	
        boolean wasSaved = bidStore.maybeSave(bid);
        
        if(wasSaved)
        	userItemsStore.save(userId, itemId);
        
        return wasSaved;
    }

    /**
	 * Fetches the current winning bid for an item from the bid store
	 * @param itemId
	 * @return
	 * 		winning bid <br> 
	 * 		null if no bid 
	 */
    @Override
    public Bid getWinningBid(final String itemId) {
        return bidStore.winningBid(itemId);		
    }

    /**
     * Retrieves List of bids placed on an item from the bid store
     * @param itemId
     * @return
     * 		List of exiting bids <br> 
     * 		null if no bid
     */
    @Override
    public Iterable<Bid> getBidsforItem(final String itemId) {
        return bidStore.bidsForItem(itemId);
    }
    
    /**
     * Retrieves Set of Unique Items on which the User has placed bids from the User history store
     * 
     * @param userId
     * @return
     * 		Set of items for the user <br>
     * 		null if no bids placed by the user
     * 
     */
    @Override
    public Set<String> getItemsforUser(final String userId) {
    	return userItemsStore.itemsForUser(userId);
     }


}
