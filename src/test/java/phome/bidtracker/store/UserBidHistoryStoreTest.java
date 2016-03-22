package phome.bidtracker.store;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Before;
import org.junit.Test;

import phome.bidtracker.store.UserBidHistoryStore;
import phome.bidtracker.store.UserBidHistoryStoreLockFreeImpl;
import static junit.framework.Assert.*;

public class UserBidHistoryStoreTest {
	
	private UserBidHistoryStore userHistoryStore;

	private String itemId = UUID.randomUUID() + "-ID";
	private String userId = UUID.randomUUID() + "-ID";
	
	
	private ExecutorService executor = Executors.newCachedThreadPool();
	
	@Before
	public void setup(){
		userHistoryStore = new UserBidHistoryStoreLockFreeImpl();
	}
	
	@Test
	public void test_no_items(){
		assertNull(userHistoryStore.itemsForUser(userId));
	}
	
	@Test
	public void saves_new_item_to_user_store(){
		userHistoryStore.save(userId, itemId);
		Set<String> items = userHistoryStore.itemsForUser(userId);
		
		String[] expected = {itemId};
		
		for(String item :items){
			assertEquals(expected[0], item);
		}
	}
	
	@Test
	public void does_not_save_same_item_multiple_times_to_a_user(){
		userHistoryStore.save(userId, itemId);
		userHistoryStore.save(userId, itemId);
		
		Set<String> items = userHistoryStore.itemsForUser(userId);
		
		assertEquals(1,items.size());		
	}
	
	@Test
	public void saves_distinct_items_for_a_user(){
		String itemTwo = UUID.randomUUID() + "-ID";
		userHistoryStore.save(userId, itemId);
		userHistoryStore.save(userId, itemTwo);
		
		Set<String> items = userHistoryStore.itemsForUser(userId);
		
		assertTrue(items.contains(itemId));
		assertTrue(items.contains(itemTwo));		
	}

	
	//Concurrent Tests
	
	@Test	
	public void saves_100_conccurrent_distint_item_adds_for_same_user() 
			throws InterruptedException, ExecutionException{
		
		//Callable bid task with dynamic userId but equal bid amount on the same item
		Callable<String> task =
		new Callable<String>() {
			@Override
			public String call() throws Exception {
				String currItemId = UUID.randomUUID() + "-ID";
				userHistoryStore.save(userId, currItemId);
				return currItemId;
			}
		};
		
		//Runs 100 equal bids in parallel and fetches the result for each
		List<Future<String>> futures = new ArrayList<Future<String>>();		
		for (int i = 0; i < 100; i++) {
			futures.add( executor.submit(task));
		}	
		
		List<String> savedItems = new ArrayList<String>();
		for ( Future<String> future  : futures) {
			savedItems.add(future.get());
		}
		
		Set<String> retrievedItems = userHistoryStore.itemsForUser(userId);
		
		assertEquals(100, retrievedItems.size());
		assertEquals(savedItems.size(), retrievedItems.size());
		
		for(String item: savedItems){
			assertTrue(retrievedItems.contains(item));
		}
		
	}
	
	@Test	
	public void saves_exactly_one_item_on_100_concurrent_identical_item_adds_for_same_user() 
			throws InterruptedException, ExecutionException{
		
		//Callable bid task with dynamic userId but equal bid amount on the same item
		Callable<String> task =
		new Callable<String>() {
			@Override
			public String call(){				
				userHistoryStore.save(userId, itemId);
				return itemId;
			}
		};
		
		//Runs 100 equal bids in parallel and fetches the result for each
		List<Future<String>> futures = new ArrayList<Future<String>>();		
		for (int i = 0; i < 100; i++) {
			futures.add( executor.submit(task));
		}	
		
		//make sure all tasks have executed
		for ( Future<String> future  : futures) {
			future.get();
		}
		
		Set<String> retrievedItems = userHistoryStore.itemsForUser(userId);		
		assertEquals(1, retrievedItems.size());
	}
	
	
	
	
	
	
}
