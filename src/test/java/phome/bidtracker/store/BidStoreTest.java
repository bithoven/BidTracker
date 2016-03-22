package phome.bidtracker.store;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Before;
import org.junit.Test;

import phome.bidtracker.models.Bid;
import phome.bidtracker.store.BidStore;
import phome.bidtracker.store.BidStoreLockFree;
import static junit.framework.Assert.*;

public class BidStoreTest {
	
	private BidStore bidStore;

	private String itemId = UUID.randomUUID() + "-ID";
	private String userId = UUID.randomUUID() + "-ID";
	private double bidAmount = 5.0;
	
	private Bid bid= new Bid(itemId, userId, bidAmount);
	
	private ExecutorService executor = Executors.newCachedThreadPool();
	
	// Tuple like class to hold bid and its result
	private static class ResultTuple{
		boolean result;
		Bid bid;
		public ResultTuple(boolean result, Bid bid){
			this.result = result;
			this.bid = bid;
		}
	}
	
	
	@Before
	public void setup(){
		bidStore =  new BidStoreLockFree();
	}
	
	
	@Test
	public void accepts_a_brand_new_bid(){
		assertTrue( bidStore.maybeSave(bid));
	}

	@Test
	public void rejects_duplicate_bids(){
		bidStore.maybeSave(bid);
		assertFalse( bidStore.maybeSave(bid));
	}
	
	@Test
	public void rejects_lower_bids(){
		bidStore.maybeSave(bid);
		Bid lowBid = new Bid(itemId, userId, bidAmount - 1.0d);		
		assertFalse( bidStore.maybeSave(lowBid));
	}
	
	@Test
	public void accepts_higher_bids(){
		bidStore.maybeSave(bid);
		Bid highBid = new Bid(itemId, userId, bidAmount  + 1.0d);		
		assertTrue( bidStore.maybeSave(highBid));
	}
	
	@Test
	public void rejects_equal_bid_by_another_user(){
		bidStore.maybeSave(bid);
		Bid equalBid = new Bid(itemId, UUID.randomUUID() + "-ID", bidAmount);		
		assertFalse( bidStore.maybeSave(equalBid));
	}
	
	
	@Test
	public void retrieves_saved_bids_for_an_item_in_order_as_placed(){
		bidStore.maybeSave(bid);
		Bid higherBid = new Bid(itemId, UUID.randomUUID() + "-ID", bidAmount + 1.0d);		
		bidStore.maybeSave(higherBid);
		
		Bid[] bids = {bid, higherBid};
		int cursor = 0;
		for (Bid currBid :bidStore.bidsForItem(itemId)) {
			assertEquals(currBid, bids[cursor++]);
		};
	}
	
	
	@Test
	public void retrieves_highest_bid_as_winning_bid(){
		bidStore.maybeSave(bid);
		Bid higherBid = new Bid(itemId, UUID.randomUUID() + "-ID", bidAmount + 1.0d);		
		bidStore.maybeSave(higherBid);
		assertEquals(higherBid, bidStore.winningBid(itemId));
	}
	
	@Test
	public void test_no_bids(){
		assertNull(bidStore.bidsForItem(itemId));
	}
	
	@Test
	public void test_no_winning_bid(){
		assertNull(bidStore.winningBid(itemId));
	}
	
	
	//Concurrency tests
	
	@Test
	public void accepts_exact_one_of_multiple_concurrent_equal_bids_on_same_item() throws InterruptedException, ExecutionException{
		
		//Callable bid task with dynamic userId but equal bid amount on the same item
		Callable<Boolean> task =
		new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				String currUserId = UUID.randomUUID() + "-ID";
				return bidStore.maybeSave(new Bid(itemId, currUserId, bidAmount));
			}
		};
		
		//Runs 100 equal bids in parallel and fetches the result for each
		int success = 0;
		List<Future<Boolean>> futures = new ArrayList<Future<Boolean>>();		
		for (int i = 0; i < 100; i++) {
			futures.add( executor.submit(task));
		}		
		for ( Future<Boolean> future  : futures) {
			if(future.get())
				success++;
		}
		
		assertEquals(1, success);	
	}
	
	
	@Test
	public void saves_and_retrieves_winning_bid_correctly_on_concurrent_unequal_bid_placement_on_same_item() 
			throws InterruptedException, ExecutionException{		
	
		//Callable bid task with dynamic userId but equal bid amount on the same item
		Callable<ResultTuple> task =
		new Callable<ResultTuple>() {
			@Override
			public ResultTuple call() throws Exception {				
				String currUserId = UUID.randomUUID() + "-ID";
				Bid currBid = new Bid(itemId, currUserId, bidAmount + Math.random());				
				return new ResultTuple(bidStore.maybeSave(currBid), currBid);
			}
		};
		
		//Runs 100 equal bids in parallel and make sure they complete		
		List<Future<ResultTuple>> futures = new ArrayList<Future<ResultTuple>>();		
		for (int i = 0; i < 100; i++) {
			futures.add( executor.submit(task));
		}
		
		Bid maxBid = null;
		for ( Future<ResultTuple> future  : futures) {
			ResultTuple resBidTuple = future.get();
			if(resBidTuple.result){
				if(maxBid == null || maxBid.getAmount() < resBidTuple.bid.getAmount())
					maxBid = resBidTuple.bid;
			}	
		}		
		assertEquals(maxBid, bidStore.winningBid(itemId));	
	}
	
	
	
	

}
