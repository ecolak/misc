package emre.recossandra.dao;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.netflix.astyanax.ExceptionCallback;
import com.netflix.astyanax.MutationBatch;
import com.netflix.astyanax.connectionpool.OperationResult;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.model.ColumnFamily;
import com.netflix.astyanax.model.ColumnList;
import com.netflix.astyanax.model.Row;
import com.netflix.astyanax.model.Rows;
import com.netflix.astyanax.serializers.LongSerializer;

import emre.recossandra.Config;

public class ProductLikesDao {

	public static final ColumnFamily<Long, Long> CF_PRODUCT_LIKES = new ColumnFamily<Long, Long>(
			"ProductLikes", // Column Family Name
			LongSerializer.get(), // Key Serializer
			LongSerializer.get()); // Column Serializer
	
	public static void save(Long visitorId, Long productId, boolean likeOrNot) throws ConnectionException {
		// Inserting data
		MutationBatch m = Config.getKeyspace().prepareMutationBatch();

		m.withRow(CF_PRODUCT_LIKES, visitorId)
				.putColumn(productId, likeOrNot);

		OperationResult<Void> result = m.execute();
	}
	
	public static void deleteByVisitorId(Long visitorId) throws ConnectionException {
		// Inserting data
		MutationBatch m = Config.getKeyspace().prepareMutationBatch();
		m.withRow(CF_PRODUCT_LIKES, visitorId).delete();
		OperationResult<Void> result = m.execute();
	}
	
	public static void truncate() throws ConnectionException {
		Config.getKeyspace().truncateColumnFamily(CF_PRODUCT_LIKES);
	}
	
	public static Map<Long, Boolean> getByVisitorId(Long visitorId) throws ConnectionException { 
		Map<Long, Boolean> result = new HashMap<Long, Boolean>();
		OperationResult<ColumnList<Long>> oResult = Config.getKeyspace()
				.prepareQuery(CF_PRODUCT_LIKES).getKey(visitorId).execute();
		ColumnList<Long> columns = oResult.getResult();
		Collection<Long> colNames = columns.getColumnNames();
		
		if(colNames != null && colNames.size() > 0) {
			for(Long productId : colNames) {
				result.put(productId, columns.getBooleanValue(productId, null));
			}
		}
		
		return result;
	}
	
	public static Map<Long, Map<Long, Boolean>> getAll() throws ConnectionException {
		Map<Long, Map<Long, Boolean>> result = new HashMap<Long, Map<Long, Boolean>>();
		OperationResult<Rows<Long, Long>> rows = Config.getKeyspace()
				.prepareQuery(CF_PRODUCT_LIKES).getAllRows().setRowLimit(100)
				.setExceptionCallback(new ExceptionCallback() {
					public boolean onException(ConnectionException e) {
						System.err.println(e.getMessage());
						return false;
					}
				}).execute();

		for (Row<Long, Long> row : rows.getResult()) {
			ColumnList<Long> columns = row.getColumns();
			Long visitorId = row.getKey();
			Collection<Long> colNames = columns.getColumnNames();

			if(colNames != null && colNames.size() > 0) {
				for(Long productId : colNames) {
					Boolean likeOrNot = columns.getBooleanValue(productId, null);
					Map<Long, Boolean> productIdMap = result.get(visitorId);
					if(productIdMap == null) {
						productIdMap = new HashMap<Long, Boolean>();
						result.put(visitorId, productIdMap);
					} 
					productIdMap.put(productId, likeOrNot);
				}
			}
			
		}
		return result;
	}
}
