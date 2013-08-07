package TokenGenerator;
import javax.microedition.rms.*;
import java.io.*;

/**
 *
 * @author bruj0
 */
public class StockDB {

    RecordStore rs = null;

    public StockDB(String fileName) {
        try {
            rs = RecordStore.openRecordStore(fileName, true);
        } catch(RecordStoreException rse) {
            rse.printStackTrace();
        }
    }
    public void saveRecord(String record) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream outputStream = new DataOutputStream(baos);
        try {
            outputStream.writeUTF(record);
        }
        catch (IOException ioe) {
            System.out.println(ioe);
            ioe.printStackTrace();
        }
        byte[] b = baos.toByteArray();
        try {
            if(rs.getNumRecords() >=1) {
                rs.setRecord(1, b, 0, b.length);
            }
            else {
                rs.addRecord(b,0, b.length);
            }
        }
        catch (RecordStoreException rse) {
            System.out.println(rse);
            rse.printStackTrace();
        }
    }
    public void close() {
        try {
            if (rs.getNumRecords() == 0) {
                String fileName = rs.getName();
                RecordStore.deleteRecordStore(fileName);
            }
            rs.closeRecordStore();
        } catch(Exception e) {}
    }
    public String readRecord(int index) {
        byte [] recData = null;
        int len = 0;
        try {
            if(rs.getNumRecords() >= index) {
                recData = new byte[rs.getRecordSize(index)];
                len = rs.getRecord(index, recData, 0);
            }
            else {
                return new String();
            }
        } catch (Exception e){}
        return new String(recData, 0, len);
    }
}
