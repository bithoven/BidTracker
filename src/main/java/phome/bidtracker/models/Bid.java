package phome.bidtracker.models;

/**
 *Immutable Bid Class 
 * 
*/
public class Bid {
	
    private final String userId;
    private final String itemId;
    private final double amount;
    
    /**
     * Disallows nulls as userId/ItemId and negative and 0 amount 
     * @param itemId
     * @param userId
     * @param amount
     * 
     */
    public Bid(String itemId, String userId, double amount) {
    	if(itemId == null || userId == null || amount <= 0.0d){
    		throw new IllegalArgumentException("Invalid Args - Null User/item or non positive amt passed");
    	}
        this.itemId = itemId;
        this.userId = userId;
        this.amount = amount;
    }

    public String getItemId() {
        return itemId;
    }

    public String getUserId() {
        return userId;
    }

    public double getAmount() {
        return amount;
    }
    
    @Override
    public boolean equals(Object arg0) {
    	if(arg0 == null || !(arg0 instanceof Bid)) 
    		return false;
    	
    	Bid bid  = (Bid)arg0;    	
    	return this.itemId.equals(bid.itemId)
    			&& this.userId.equals(bid.userId)
    			&& this.amount==bid.amount;
    		
    }
    
    /**
     * A simple hashcode implementation based on member variables
     */
    @Override
    public int hashCode() {
    	return this.itemId.hashCode() + this.userId.hashCode() + new Double(amount).hashCode();
    }
    
    
    
}
