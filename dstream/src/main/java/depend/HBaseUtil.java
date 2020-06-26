package depend;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;

/**
 * @author duifsuefi
 */
public class HBaseUtil {
    private Configuration hadoopConf =  null;
    private Configuration hbaseConf = null;
    private Connection conn = null;
    Admin admin=null;
    private HBaseUtil(){
        hadoopConf=new Configuration();
        hbaseConf = HBaseConfiguration.create(hadoopConf);
        try {
            conn = ConnectionFactory.createConnection(hbaseConf);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            admin=conn.getAdmin();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static HBaseUtil instance=null;
    public static synchronized HBaseUtil getInstance(){
        if(instance==null) instance=new HBaseUtil();
        return instance;
    }

    public HTable gettable(String tableName) throws IOException {
        HTable table = (HTable)conn.getTable(TableName.valueOf(tableName));
        return table;
    }

    public void put(String tableName,String rowKey,String cf,String column,Long value) throws IOException {
        HTable table=gettable(tableName);
        Put put=new Put(Bytes.toBytes(rowKey));
        put.add(Bytes.toBytes(cf),Bytes.toBytes(column),Bytes.toBytes(value));
        table.put(put);
    }

}
