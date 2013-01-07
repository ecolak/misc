package emre.recossandra.dao;

import java.util.ArrayList;
import java.util.List;

import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.MutationBatch;
import com.netflix.astyanax.connectionpool.OperationResult;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.model.ColumnFamily;
import com.netflix.astyanax.model.ColumnList;
import com.netflix.astyanax.model.Row;
import com.netflix.astyanax.model.Rows;
import com.netflix.astyanax.serializers.LongSerializer;
import com.netflix.astyanax.serializers.StringSerializer;

import emre.recossandra.Config;
import emre.recossandra.model.Product;
import emre.recossandra.model.Product.Color;

public class ProductDao implements IProductDao {

	private static final ProductDao instance = new ProductDao();
	
	public static final String PRODUCTS = "Products";
	public static final String PRODUCT_NAME = "productName";
	public static final String COLOR = "color";
	public static final String MANUFACTURER = "manufacturer";
	
	public static final ColumnFamily<Long, String> CF_PRODUCT_INFO = new ColumnFamily<Long, String>(
			PRODUCTS, // Column Family Name
			LongSerializer.get(), // Key Serializer
			StringSerializer.get()); // Column Serializer

	private Keyspace keyspace;
	
	private ProductDao() {
		this.keyspace = Config.getKeyspace();
	}
	
	public static ProductDao theInstance() {
		return instance;
	}
	
	public void setKeyspace(Keyspace keyspace) {
		this.keyspace = keyspace;
	}
	
	public void save(Product product) throws ConnectionException {
		// Inserting data
		MutationBatch m = keyspace.prepareMutationBatch();

		m.withRow(CF_PRODUCT_INFO, product.getProductId())
				.putColumn(PRODUCT_NAME, product.getProductName(), null)
				.putColumn(MANUFACTURER, product.getManufacturer(), null)
				.putColumn(COLOR, product.getColor().ordinal(), null);

		OperationResult<Void> result = m.execute();
	}
	
	public void deleteByProductId(Long productId) throws ConnectionException {
		// Inserting data
		MutationBatch m = keyspace.prepareMutationBatch();
		m.withRow(CF_PRODUCT_INFO, productId).delete();
		OperationResult<Void> result = m.execute();
	}
	
	public void truncate() throws ConnectionException {
		Config.getKeyspace().truncateColumnFamily(CF_PRODUCT_INFO);
	}

	public Product getByProductId(Long productId) throws ConnectionException {
		Product result = null;
		OperationResult<ColumnList<String>> oResult = keyspace
				.prepareQuery(CF_PRODUCT_INFO).getKey(productId).execute();
		ColumnList<String> columns = oResult.getResult();

		if (columns != null && columns.size() > 0) {
			result = new Product();
			result.setProductId(productId);
			result.setProductName(columns.getStringValue(PRODUCT_NAME, null));
			result.setManufacturer(columns.getStringValue(MANUFACTURER, null));
			int c = columns.getIntegerValue(COLOR, -1);
			result.setColor(Color.values()[c]);
		}
		return result;
	}

	public List<Product> getAll() throws ConnectionException {
		List<Product> result = new ArrayList<Product>();
		/*OperationResult<Rows<Long, String>> rows = keyspace
				.prepareQuery(CF_PRODUCT_INFO).getAllRows().setRowLimit(100)
				.setExceptionCallback(new ExceptionCallback() {
					public boolean onException(ConnectionException e) {
						System.err.println(e.getMessage());
						return false;
					}
				}).execute();*/

		OperationResult<Rows<Long, String>> rows = keyspace
				.prepareQuery(CF_PRODUCT_INFO).getAllRows().setRowLimit(100)
				.execute();
		
		for (Row<Long, String> row : rows.getResult()) {
			Product p = new Product();
			p.setProductId(row.getKey());
			ColumnList<String> columns = row.getColumns();
			p.setProductName(columns.getStringValue(PRODUCT_NAME, null));
			p.setManufacturer(columns.getStringValue(MANUFACTURER, null));
			int c = columns.getIntegerValue(COLOR, -1);
			if(c >= 0)
				p.setColor(Color.values()[c]);
			result.add(p);
		}
		return result;
	}
}
