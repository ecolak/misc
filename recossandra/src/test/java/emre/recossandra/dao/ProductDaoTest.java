package emre.recossandra.dao;

import java.util.List;

import emre.recossandra.model.Product;
import emre.recossandra.model.Product.Color;
import junit.framework.Assert;
import junit.framework.TestCase;

public class ProductDaoTest extends TestCase {

	public void testGetAll() throws Exception {
        List<Product> result = ProductDao.getAll();
        System.out.println("Products: ");
        for(Product p : result) {
        	System.out.println(p);
        }
        Assert.assertNotNull(result);
        Assert.assertEquals(4, result.size());
    }
	
	public void testGetByProductId() throws Exception {
		long productId = System.currentTimeMillis();
		Product p = new Product();
		p.setProductId(productId);
		p.setProductName("Test Product");
		p.setManufacturer("Test Corp");
		p.setColor(Color.PINK);
		ProductDao.save(p);
		
		Product result = ProductDao.getByProductId(productId);
        Assert.assertNotNull(result);
        Assert.assertEquals(new Long(productId), result.getProductId());
        Assert.assertEquals("Test Product", result.getProductName());
        Assert.assertEquals("Test Corp", result.getManufacturer());
        Assert.assertEquals(Color.PINK, result.getColor());
    }
	
	public void testDeleteByProductId() throws Exception {
		long productId = System.currentTimeMillis();
		Product p = new Product();
		p.setProductId(productId);
		p.setProductName("Test Product 2");
		p.setManufacturer("Test Corp");
		p.setColor(Color.BLUE);
		ProductDao.save(p);
		
		Product result = ProductDao.getByProductId(productId);
        Assert.assertNotNull(result);
        ProductDao.deleteByProductId(productId);
        result = ProductDao.getByProductId(productId);
        Assert.assertNull(result);
    }
}
