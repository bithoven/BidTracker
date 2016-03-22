package phome.bidtracker.tracker;

import org.junit.Test;

import phome.bidtracker.models.Bid;
import phome.bidtracker.store.BidStore;
import phome.bidtracker.store.UserBidHistoryStore;
import phome.bidtracker.tracker.BidTracker;
import phome.bidtracker.tracker.BidTrackerImpl;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static junit.framework.Assert.*;


/**
 * Tests the BidTrackerImplementation
 * @author phome
 *
 */

public class BidTrackerTest {
	
	private BidStore mockedBidStore = mock(BidStore.class);
	private UserBidHistoryStore mockedUserHistoryStore = mock(UserBidHistoryStore.class);	
	private BidTracker bidTracker = new BidTrackerImpl(mockedBidStore, mockedUserHistoryStore);
	
	private String itemId = UUID.randomUUID() + "-ID";
	private String userId = UUID.randomUUID() + "-ID";
	private double bidAmount = 5.0;
	
	private Bid bid= new Bid(itemId, userId, bidAmount);
	
	
	@Test(expected = IllegalArgumentException.class)
	public void bidstore_mandatory_for_tracker(){	
		new BidTrackerImpl(null, mockedUserHistoryStore);		
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void user_history_store_mandatory_for_tracker(){	
		new BidTrackerImpl(mockedBidStore, null);		
	}
	
	
	@Test
	public void bid_placement_attempts_save_to_bidstore(){
		bidTracker.bid(itemId, userId, bidAmount);
		verify(mockedBidStore).maybeSave(bid);
	}
	
	@Test
	public void bid_placement_saves_to_user_store_only_if_bid_was_saved_to_bid_store(){		
		when(mockedBidStore.maybeSave(bid)).thenReturn(true);
		bidTracker.bid(itemId, userId, bidAmount);
		verify(mockedUserHistoryStore).save(userId, itemId);
	}
	
	@Test
	public void bid_placement_doest_not_save_to_user_store_if_bid_not_saved_to_bid_store(){		
		when(mockedBidStore.maybeSave(bid)).thenReturn(false);
		bidTracker.bid(itemId, userId, bidAmount);
		verify(mockedUserHistoryStore, never()).save(userId, itemId);
	}
	
	@Test
	public void bid_declined_if_not_saved_to_bid_store(){
		when(mockedBidStore.maybeSave(bid)).thenReturn(false);
		boolean bidAccepted = bidTracker.bid(itemId, userId, bidAmount);
		assertFalse(bidAccepted);
	}
	
	@Test
	public void bid_accepted_if_saved_to_bid_store(){
		when(mockedBidStore.maybeSave(bid)).thenReturn(true);
		boolean bidAccepted = bidTracker.bid(itemId, userId, bidAmount);
		assertTrue(bidAccepted);
	}
	
	@Test
	public void winning_bid_is_retrieved_from_bid_store(){
		when(mockedBidStore.winningBid(itemId)).thenReturn(bid);
		assertEquals(bid, bidTracker.getWinningBid(itemId));
	}
	
	@Test
	public void does_not_throw_error_when_no_winning_bid_in_store(){
		assertEquals(null, bidTracker.getWinningBid(itemId));
	}
		
	@Test
	public void bids_for_item_retrieved_from_bid_store(){
		List<Bid> bids = new ArrayList<Bid>();
		when(mockedBidStore.bidsForItem(itemId)).thenReturn(bids);
		assertEquals(bids, bidTracker.getBidsforItem(itemId));
	}
	
	
	@Test
	public void items_for_user_retrieved_from_user_history_store(){
		Set<String> items = new HashSet<String>();
		items.add(itemId);
		when(mockedUserHistoryStore.itemsForUser(userId)).thenReturn(items);
		assertEquals(items, bidTracker.getItemsforUser(userId));
	}
	
		
//    @Test
//    public void shouldReturnZeroUserItems() {
//        String userId = UUID.randomUUID() + "-ID";
//        BidTracker bidTracker = new BidTrackerImpl(mockedBidStore, mockedUserHistoryStore);
//        Set<String> userItems = bidTracker.getItemsforUser(userId);
//        assertEquals(0, userItems.size());
//    }
//    
//    
    
//
//    @Test
//    public void shouldReturnOneUserBid() {
//        String itemId = UUID.randomUUID() + "-ID";
//        String userId = UUID.randomUUID() + "-ID";
//        double bidAmount = 5.0;
//        
//        BidTracker bidTracker = new BidTrackerImpl(mockedBidStore, mockedUserHistoryStore);
//        bidTracker.bid(itemId, userId, bidAmount);
//
//        Set<String> userItems = bidTracker.getItemsforUser(userId);
//        assertEquals(1, userItems.size());
//    }
//
////    @Test
////    public void shouldReturnThreeUserBids() {
//        String userId = UUID.randomUUID() + "-ID";
//        double bidAmount = 5.0;
//        BidStoreLockFree repository = new BidStoreLockFree();
//        UserBidHistoryStoreLockFreeImpl userRepo = new UserBidHistoryStoreLockFreeImpl();
//        BidTracker bidTracker = new BidTrackerImpl(repository, userRepo);
//        bidTracker.bid(new Bid("ITEM1", userId, bidAmount));
//        bidTracker.bid(new Bid("ITEM2", userId, bidAmount+1));
//        bidTracker.bid(new Bid("ITEM3", userId, bidAmount+2));
//
//        List<String> userItems = bidTracker.getItemsforUser(userId);
//        assertEquals(3, userItems.size());    
//    }
//    
//
//    @Test
//    public void shouldReturnZeroItemBids() {
//        String itemId = UUID.randomUUID() + "-ID";
//        BidStoreLockFree repository = new BidStoreLockFree();
//        UserBidHistoryStoreLockFreeImpl userRepo = new UserBidHistoryStoreLockFreeImpl();
//        BidTracker bidTracker = new BidTrackerImpl(repository, userRepo);
//        
//        List<Bid> bids = bidTracker.getBidsforItem(itemId);
//        assertEquals(1, bids.size());
//    }
//
//    @Test
//    public void shouldReturnOneItemBid() {
//        String itemId = UUID.randomUUID() + "-ID";
//        String userId = UUID.randomUUID() + "-ID";
//        double bidAmount = 5.0;
//        BidStoreLockFree repository = new BidStoreLockFree();
//        UserBidHistoryStoreLockFreeImpl userRepo = new UserBidHistoryStoreLockFreeImpl();
//        BidTracker bidTracker = new BidTrackerImpl(repository, userRepo);
//        bidTracker.bid(new Bid(itemId, userId, bidAmount));
//
//        List<Bid> bids = bidTracker.getBidsforItem(itemId);
//        assertEquals(1, bids.size());
//    }
//
//    @Test
//    public void shouldReturnThreeItemBids() {
//        String itemId = UUID.randomUUID() + "-ID";
//        double bidAmount = 5.0;
//        BidStoreLockFree repository = new BidStoreLockFree();
//        UserBidHistoryStoreLockFreeImpl userRepo = new UserBidHistoryStoreLockFreeImpl();
//        BidTracker bidTracker = new BidTrackerImpl(repository, userRepo);
//        bidTracker.bid(new Bid(itemId, "USER1", bidAmount));
//        bidTracker.bid(new Bid(itemId, "USER2", bidAmount+1));
//        bidTracker.bid(new Bid(itemId, "USER3", bidAmount+2));
//
//        List<Bid> bids = bidTracker.getBidsforItem(itemId);
//        assertEquals(3, bids.size());
//        
//        for( Bid bid : bids) {
//            if( bid.getUserId().equalsIgnoreCase("USER1"))
//                assertEquals(bidAmount, bid.getAmount());
//            else if( bid.getUserId().equalsIgnoreCase("USER2"))
//                assertEquals(bidAmount+1, bid.getAmount());
//            else if( bid.getUserId().equalsIgnoreCase("USER3"))
//                assertEquals(bidAmount+2, bid.getAmount());
//            else
//                fail();
//        }
//    }
//
//    @Test
//    public void shouldReturnNoBidWhenNoWinningBid() {
//        String itemId = UUID.randomUUID() + "-ID";
//        BidStoreLockFree repository = new BidStoreLockFree();
//        UserBidHistoryStoreLockFreeImpl userRepo = new UserBidHistoryStoreLockFreeImpl();
//        BidTracker bidTracker = new BidTrackerImpl(repository, userRepo);
//        Bid bid = bidTracker.getWinningBid(itemId);
//        assertTrue(bid instanceof NoBid);
//
//    }
//
//    @Test
//    public void shouldReturnCorrectWinningBid() {
//        String itemId = UUID.randomUUID() + "-ID";
//        String userId = UUID.randomUUID() + "-ID";
//        double bidAmount = 5.0;
//        BidStoreLockFree repository = new BidStoreLockFree();
//        UserBidHistoryStoreLockFreeImpl userRepo = new UserBidHistoryStoreLockFreeImpl();
//        BidTracker bidTracker = new BidTrackerImpl(repository, userRepo);
//        bidTracker.bid(new Bid(itemId, userId, bidAmount));
//
//        Bid actualBid = bidTracker.getWinningBid(itemId);
//        assertEquals(bidAmount, actualBid.getAmount());
//        assertEquals(userId, actualBid.getUserId());
//
//    }
//
//    @Test
//    public void shouldSetAsWinningBidWhenBidIsGreaterThanWinning() {
//        String itemId = UUID.randomUUID() + "-ID";
//        String userId = UUID.randomUUID() + "-ID";
//        double bidAmount = 5.0;
//        BidStoreLockFree repository = new BidStoreLockFree();
//        UserBidHistoryStoreLockFreeImpl userRepo = new UserBidHistoryStoreLockFreeImpl();
//        BidTracker bidTracker = new BidTrackerImpl(repository, userRepo);
//        bidTracker.bid(new Bid(itemId, userId, bidAmount));
//
//        Bid actualBid = bidTracker.getWinningBid(itemId);
//        assertEquals(bidAmount, actualBid.getAmount());
//
//    }
//
//    @Test (expected = BidException.class)
//    public void shouldThrowExceptionWhenBidIsLowerThanWinning() {
//        String itemId = UUID.randomUUID() + "-ID";
//        String userId = UUID.randomUUID() + "-ID";
//        double bidAmount = 5.0;
//        BidStoreLockFree repository = new BidStoreLockFree();
//        UserBidHistoryStoreLockFreeImpl userRepo = new UserBidHistoryStoreLockFreeImpl();
//        BidTracker bidTracker = new BidTrackerImpl(repository, userRepo);
//        bidTracker.bid(new Bid(itemId, userId, bidAmount));
//        bidTracker.bid(new Bid(itemId, userId, bidAmount - 2));
//    }
//
//    @Test (expected = BidException.class)
//    public void shouldThrowExceptionWhenBidIsEqualToTheWinning() {
//        String itemId = UUID.randomUUID() + "-ID";
//        double bidAmount = 5.0;
//        BidStoreLockFree repository = new BidStoreLockFree();
//        UserBidHistoryStoreLockFreeImpl userRepo = new UserBidHistoryStoreLockFreeImpl();
//        BidTracker bidTracker = new BidTrackerImpl(repository, userRepo);
//        bidTracker.bid(new Bid(itemId, "USER1", bidAmount));
//        bidTracker.bid(new Bid(itemId, "USER2", bidAmount));
//    }
//

}
