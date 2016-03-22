package phome.bidtracker.store;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import phome.bidtracker.models.Bid;

/**
 * 
 * This is an implementation of a Concurrent Bid Store. 
 * ConcurrentHashMap and lock free (CAS based) algorithms are employed to handle
 * following concurrency situations:<br>
 * 
 * 1. Concurrent Updates to the BidStore for different items <br>
 * 2. Concurrent Updates on the bid list for a given Item 
 *  <br>Ref: http://www.ibm.com/developerworks/library/j-jtp04186/
 * 
 * <br><br>Also uses Deque interface to store bids on a given item. 
 * Rationale:
 *  <br>A bid will only make it to the store if and only if its the highest one at given point in time.
 *      Hence the bid is auto sorted by its order of insertion. 
 *      In order to retrieve the winning bid the last bid placed has to be retrieved.
 *   	<br>Deque interface provides semantics for add() to the tail and retrieve <getLast()> from the Tail
 *   	<br>LIFO Stack would have been appropriate as well but in Java Standard Library its based on legacy
 *      implementation of Vector and hence not chosen
 * 
 */
public class BidStoreLockFree implements BidStore{
    private final ConcurrentMap<String, Deque<Bid>> bidstore; 

    
    /**
     * Instantiates the bid store with a CHM with concurrency level based on CPU cores available
     * 
     */
    public BidStoreLockFree() {
    	int concurrencyLevel = Runtime.getRuntime().availableProcessors() * 2;
        bidstore = new ConcurrentHashMap<String, Deque<Bid>>(100, 0.75f, concurrencyLevel);
    }
    

    /**
     * Attempts to save a bid given it is valid i.e. Higher than all other bids placed so far.
     * If not the bid is rejected.
     * 
     * <br>Handles concurrent updates in iterative lock free way using replace and putIfAbsent contracts from ConcurrentMap 
     * interface. At least one thread is bound to make progress in event of conflict on updates on a given item.
     * 
     * <br>Hence the iteration is self converging. 
     * Also, every iteration checks if the bid is still valid which would mean in event of being outbid the loop will 
     * short circuit.
     * In absence of any conflict the iteration will complete in the first go itself     
     * 
     * 
     */
    @Override
    public boolean maybeSave(Bid bid) {    	
        final String itemId = bid.getItemId();        
        Deque<Bid> currentBids = bidstore.get(itemId);
        boolean success = false ;
        
        //loop until the insert passes or the replace passes
        while(!success && isValidBid(currentBids, bid))//loop ONLY if the bid is still valid under latest currentBids
        {
        	Deque<Bid> updatedBids = addBid(currentBids, bid);        	
        	if(currentBids == null ){//Attempt insert 
        		currentBids = bidstore.putIfAbsent(itemId, updatedBids);
        		success = currentBids == null;        		
        	}
        	else{        		
        		success = bidstore.replace(itemId, currentBids, updatedBids); 
        		currentBids = bidstore.get(itemId);
        	}
        	
        };         
        
        return success;//will return false if loop short circuited due to isValidBid check        
    }

    /**
     * Returns the winning bid by retrieving the tail of the deque
     * Null is returned in event of no bids
     */
    @Override
    public Bid winningBid(final String itemId){
    	Deque<Bid> currentBids = bidstore.get(itemId);
    	return currentBids == null ? null : currentBids.getLast();//retrieve the tail
    }
    
    
    /**
     * Retrieves list of bids and returns a read only view of the list to the caller.
     * 
     */
    @Override
    public Iterable<Bid> bidsForItem(final String itemId){
    	Deque<Bid> currentBids = bidstore.get(itemId);
    	return currentBids ==  null ? null : 
    		Collections.unmodifiableCollection(currentBids);    	
    }
    
    
    /**
     * Adds the new bid to a current list of Bids
     * If the current list of bids is null a new list containing just the new bid is createD
     * @param currentBids
     * @param bid
     * @return
     */
    private Deque<Bid> addBid(Deque<Bid> currentBids, Bid bid){
    	Deque<Bid> updatedBids;
        if(currentBids == null){
        	updatedBids = new ArrayDeque<Bid>();
        	updatedBids.add(bid);
        }else{
        	updatedBids = new ArrayDeque<Bid>(currentBids); //TODO Why shallow copying
        	updatedBids.add(bid);	
        }
        return updatedBids;
    }
    
    /**
     * A bid is valid only if it oubids the last highest bid
     * @param currentBids
     * @param bid
     * @return
     */
    private boolean isValidBid(Deque<Bid>currentBids, Bid bid){    	
    	final Bid winningBid = winningBid(bid.getItemId());
    	boolean isValid = 
    			winningBid == null || //No current bid hence incoming bid valid
    			bid.getAmount() > winningBid.getAmount();//Only valid if greater than 
    	return isValid;		
    }
    
    
    
    
    
    
}
