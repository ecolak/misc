package emre.recossandra.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import junit.framework.Assert;
import junit.framework.TestCase;
import emre.recossandra.model.Product;
import emre.recossandra.model.Product.Color;

public class ProductDaoTest extends TestCase {

	public void testGetAll() throws Exception {
		List<Product> resultBefore = ProductDao.getAll();
		Assert.assertNotNull(resultBefore);
		
		List<Long> ids = createTestProducts(3);
		List<Product> resultAfter = ProductDao.getAll();

        Assert.assertNotNull(resultAfter);
        Assert.assertEquals(3 + resultBefore.size(), resultAfter.size());
        
        deleteProducts(ids);
    }
	
	public void testGetByProductId() throws Exception {
		long productId = createTestProduct();
		
		Product result = ProductDao.getByProductId(productId);
        Assert.assertNotNull(result);
        Assert.assertEquals(new Long(productId), result.getProductId());
        Assert.assertNotNull(result.getProductName());
        Assert.assertTrue(result.getProductName().startsWith("Test Product"));
        Assert.assertEquals("Test Corp", result.getManufacturer());
        Assert.assertNotNull(result.getColor());
        
        ProductDao.deleteByProductId(productId);
    }
	
	public void testDeleteByProductId() throws Exception {
		long productId = createTestProduct();
		
		Product result = ProductDao.getByProductId(productId);
        Assert.assertNotNull(result);
        ProductDao.deleteByProductId(productId);
        result = ProductDao.getByProductId(productId);
        Assert.assertNull(result);
    }
	
	private Long createTestProduct() throws Exception {
		long productId = System.currentTimeMillis();
		Product p = new Product();
		p.setProductId(productId);
		p.setProductName("Test Product " + productId);
		p.setManufacturer("Test Corp");
		p.setColor(Color.values()[new Random().nextInt(Color.values().length)]);
		ProductDao.save(p);		
		return productId;
	}
	
	private List<Long> createTestProducts(int num) throws Exception {
		List<Long> result = new ArrayList<Long>();
		for(int i = 0; i < num; i++) {
			result.add(createTestProduct());
		}
		return result;
	}
	
	private void deleteProducts(List<Long> ids) throws Exception {
		for(Long id : ids) {
			ProductDao.deleteByProductId(id);
		}
	}
}
