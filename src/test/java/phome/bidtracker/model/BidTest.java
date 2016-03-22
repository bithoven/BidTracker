package phome.bidtracker.model;

import java.util.UUID;

import org.junit.Test;

import phome.bidtracker.models.Bid;
import static junit.framework.Assert.*;

public class BidTest {
	
	private String itemId = UUID.randomUUID() + "-ID";
	private String userId = UUID.randomUUID() + "-ID";
	private double bidAmount = 5.0;
	
	private Bid bid= new Bid(itemId, userId, bidAmount);
	
	
	@Test(expected = IllegalArgumentException.class)
	public void disallows_null_Item(){
		new Bid(null, userId, bidAmount);
	}

	@Test(expected = IllegalArgumentException.class)
	public void disallows_null_user(){
		new Bid(itemId, null, bidAmount);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void disallows_zero_bid(){
		new Bid(itemId, userId, 0.0d);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void disallows_negative_bid(){
		new Bid(itemId, userId, -10.0d);
	}
	
	@Test
	public void equal_by_content(){
		Bid newBid = new Bid(itemId, userId, bidAmount);
		assertEquals(bid, newBid);		
	}
	
	@Test
	public void equal_by_hashcode(){
		Bid newBid = new Bid(itemId, userId, bidAmount);
		assertEquals(bid.hashCode(), newBid.hashCode());		
	}
	
	@Test
	public void unequal_by_content(){
		Bid newBid = new Bid(itemId, userId, bidAmount+1);
		assertNotSame(bid, newBid);		
	}
	
	@Test
	public void unequal_for_null(){
		assertNotSame(bid, null);		
	}
	
	
	
}
