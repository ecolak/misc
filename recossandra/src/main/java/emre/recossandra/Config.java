package emre.recossandra;

import com.netflix.astyanax.AstyanaxContext;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.connectionpool.NodeDiscoveryType;
import com.netflix.astyanax.connectionpool.impl.ConnectionPoolConfigurationImpl;
import com.netflix.astyanax.connectionpool.impl.CountingConnectionPoolMonitor;
import com.netflix.astyanax.impl.AstyanaxConfigurationImpl;
import com.netflix.astyanax.thrift.ThriftFamilyFactory;

public class Config {

	public static final String CLUSTER_NAME = "Test Cluster";
	public static final String KEYSPACE_NAME = "Recossandra";
	public static final String CONN_POOL = CLUSTER_NAME + "_" + KEYSPACE_NAME + "_Pool";
	public static final int PORT = 9160;
	public static final String URL = "127.0.0.1:9160";

	private Config() {
	}

	public static Keyspace getKeyspace() {
		AstyanaxContext<Keyspace> context = new AstyanaxContext.Builder()
				.forCluster(CLUSTER_NAME)
				.forKeyspace(KEYSPACE_NAME)
				.withAstyanaxConfiguration(
						new AstyanaxConfigurationImpl()
								.setDiscoveryType(NodeDiscoveryType.NONE))
				.withConnectionPoolConfiguration(
						new ConnectionPoolConfigurationImpl(CONN_POOL)
								.setPort(PORT).setMaxConnsPerHost(1)
								.setSeeds(URL))
				.withConnectionPoolMonitor(new CountingConnectionPoolMonitor())
				.buildKeyspace(ThriftFamilyFactory.getInstance());

		context.start();
		return context.getEntity();
	}
}
