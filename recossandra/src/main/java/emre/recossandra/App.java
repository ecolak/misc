package emre.recossandra;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import emre.recossandra.dao.ProductDao;
import emre.recossandra.model.Product;
import emre.recossandra.model.Product.Color;

public class App 
{
    public static void main(String... args) throws Exception {
        createTestProducts(3);
        System.out.println("3 products created");
        ProductDao.theInstance().truncate();
        System.out.println("Column family Products truncated");
    }
    
    private static Long createTestProduct() throws Exception {
		long productId = System.currentTimeMillis();
		Product p = new Product();
		p.setProductId(productId);
		p.setProductName("Test Product " + productId);
		p.setManufacturer("Test Corp");
		p.setColor(Color.values()[new Random().nextInt(Color.values().length)]);
		ProductDao.theInstance().save(p);		
		return productId;
	}
	
	private static List<Long> createTestProducts(int num) throws Exception {
		List<Long> result = new ArrayList<Long>();
		for(int i = 0; i < num; i++) {
			result.add(createTestProduct());
		}
		return result;
	}
	
}
