package phome.bidtracker.store;


import phome.bidtracker.models.Bid;

public interface BidStore {
	
    /**
     * Saves a bid to the BidStore if is higher than existing bids
     * @param bid
     */
	public boolean maybeSave(final Bid bid);
	
    
	/**
	 * Returns the current winning bid for an item
	 * @param itemId
	 * @return - winningBid  <br>
	 * 		   - <code>null</code> if no bids on the item
	 * 
	 */
    public Bid winningBid(final String itemId);
    

    /**
     * Returns all the bids placed on the item so far in the order they were placed
     * @param itemId
     * @return - Bids placed for the item <br> 
     * 		    -<code>null</code> if no bids on the item 
     */
    public Iterable<Bid> bidsForItem(final String itemId);

}
