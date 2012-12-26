package emre.recossandra;

import emre.recossandra.dao.ProductDao;
import emre.recossandra.model.Product;
import emre.recossandra.model.Product.Color;

public class App 
{
    public static void main(String... args) throws Exception {
        Product p = new Product();
        p.setProductId(System.currentTimeMillis());
        p.setColor(Color.PINK);
        p.setManufacturer("Toshiba");
        p.setProductName("Toshiba Tablet");  
        ProductDao.save(p); 
        
        p = new Product();
        p.setProductId(System.currentTimeMillis());
        p.setColor(Color.BLACK);
        p.setManufacturer("Sony");
        p.setProductName("Sony Vaio Laptop");  
        ProductDao.save(p); 
    }
}
