package emre.recossandra.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.connectionpool.OperationResult;
import com.netflix.astyanax.model.ColumnList;
import com.netflix.astyanax.query.ColumnFamilyQuery;
import com.netflix.astyanax.query.RowQuery;

import emre.recossandra.model.Product;
import emre.recossandra.model.Product.Color;

public class ProductDaoTest {

	@Mock private Keyspace keyspace;
	@Mock private ColumnFamilyQuery<Long, String> cfProductsQuery;
	@Mock private RowQuery<Long, String> byIdRowQuery;
	@Mock private OperationResult<ColumnList<String>> byIdOpResult;
	@Mock private ColumnList<String> byIdColumnList;
	
	private ProductDao dao;
	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		dao = ProductDao.theInstance();
		dao.setKeyspace(keyspace);
	}
	
	@Test
	public void testGetByProductId() throws Exception {
		Long productId = 1234L;
		String productName = "Test Product";
		String manufacturer = "Test Corp";
		Mockito.when(keyspace.prepareQuery(ProductDao.CF_PRODUCT_INFO))
			.thenReturn(cfProductsQuery);
		Mockito.when(cfProductsQuery.getKey(productId)).thenReturn(byIdRowQuery);
		Mockito.when(byIdRowQuery.execute()).thenReturn(byIdOpResult);
		Mockito.when(byIdOpResult.getResult()).thenReturn(byIdColumnList);
		Mockito.when(byIdColumnList.getStringValue(ProductDao.PRODUCT_NAME, null))
			.thenReturn(productName);
		Mockito.when(byIdColumnList.getStringValue(ProductDao.MANUFACTURER, null))
		   .thenReturn(manufacturer);
		Mockito.when(byIdColumnList.getIntegerValue(ProductDao.COLOR, -1))
		   .thenReturn(Color.PINK.ordinal());
		Mockito.when(byIdColumnList.size()).thenReturn(1);
		
		Product result = dao.getByProductId(productId);
        assertNotNull(result);
        assertEquals(productId, result.getProductId());
        assertNotNull(result.getProductName());
        assertEquals(result.getProductName(), productName);
        assertEquals(manufacturer, result.getManufacturer());
        assertNotNull(result.getColor());
    }

}
