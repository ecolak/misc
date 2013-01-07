package emre.recossandra.dao;

import java.util.List;

import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;

import emre.recossandra.model.Product;

public interface IProductDao {

	public void save(Product product) throws ConnectionException;
	
	public void deleteByProductId(Long productId) throws ConnectionException;
	
	public void truncate() throws ConnectionException;

	public Product getByProductId(Long productId) throws ConnectionException;

	public List<Product> getAll() throws ConnectionException;
}
