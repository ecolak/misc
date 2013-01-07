package emre.recossandra.dao;

import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

public class ProductLikesDaoTest extends TestCase {

	public void testGetAll() throws Exception {
		Long vid1 = System.currentTimeMillis();
		Thread.sleep(10);
		Long vid2 = System.currentTimeMillis();
		Thread.sleep(10);
		Long vid3 = System.currentTimeMillis();
		Long pid1 = System.currentTimeMillis();
		Thread.sleep(10);
		Long pid2 = System.currentTimeMillis();
		Thread.sleep(10);
		Long pid3 = System.currentTimeMillis();
		
		ProductLikesDao.save(vid1, pid1, true);
		ProductLikesDao.save(vid1, pid2, true);
		ProductLikesDao.save(vid1, pid3, false);
		ProductLikesDao.save(vid2, pid2, false);
		ProductLikesDao.save(vid2, pid3, false);
		ProductLikesDao.save(vid3, pid1, true);
		
		Map<Long, Map<Long, Boolean>> resultAfter = ProductLikesDao.getAll();
        assertNotNull(resultAfter);
        assertEquals(3, resultAfter.size());
        Map<Long, Boolean> resultVid1 = resultAfter.get(vid1);
        assertNotNull(resultVid1);
        assertEquals(3, resultVid1.size());
        
        ProductLikesDao.truncate();
    }
	
	public void testGetByVisitorId() throws Exception {
		Long vid = System.currentTimeMillis();
		Long pid1 = System.currentTimeMillis();
		Thread.sleep(10);
		Long pid2 = System.currentTimeMillis();
		Thread.sleep(10);
		Long pid3 = System.currentTimeMillis();
		
		ProductLikesDao.save(vid, pid1, true);
		ProductLikesDao.save(vid, pid2, true);
		ProductLikesDao.save(vid, pid3, false);
		
		Map<Long, Boolean> resultAfter = ProductLikesDao.getByVisitorId(vid);
        assertNotNull(resultAfter);
        assertEquals(3, resultAfter.size());
        Boolean likeOrNot = resultAfter.get(pid1);
        assertNotNull(likeOrNot);
        assertTrue(likeOrNot.booleanValue());
        likeOrNot = resultAfter.get(pid2);
        assertNotNull(likeOrNot);
        assertTrue(likeOrNot.booleanValue());
        likeOrNot = resultAfter.get(pid3);
        assertNotNull(likeOrNot);
        assertFalse(likeOrNot.booleanValue());
        likeOrNot = resultAfter.get(System.currentTimeMillis());
        assertNull(likeOrNot);
        
        ProductLikesDao.truncate();
	}
	
	private void deleteProductLikes(List<Long> ids) throws Exception {
		for(Long id : ids) {
			ProductLikesDao.deleteByVisitorId(id);
		}
	}
}
