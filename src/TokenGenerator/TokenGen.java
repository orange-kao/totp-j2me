package TokenGenerator;

import java.util.Date;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;
import java.math.BigInteger;

/**
 *
 * @author bruj0
 */
public class TokenGen {
    private final int PIN_MODULO = 1000000;
    private String key = null;
    private byte[] decodedKey = null; //Base32.decode(key);

    public TokenGen(String newKey) {
        if(newKey.length() <= 0) {
            newKey="None";
        }
        key = newKey;
        decodedKey = Base32.decode(key);
    }
    public String GenToken() {
        long ts = (new Date()).getTime() / 1000;
        long T = ts / 30;
        String steps = "0";
        steps = toHexString(T).toUpperCase();
        while(steps.length() < 16 ) {
            steps = "0" + steps;
        }
        byte[] msg = hexStr2Bytes(steps);

        HMac hmac = new HMac(new SHA1Digest());
        byte[] hash = new byte[hmac.getMacSize()];

        hmac.init(new KeyParameter(decodedKey));
        hmac.update(msg, 0, msg.length);
        hmac.doFinal(hash, 0);


        int offset = hash[hash.length - 1] & 0xF;
        int binary =
            ((hash[offset] & 0x7f) << 24) |
            ((hash[offset + 1] & 0xff) << 16) |
            ((hash[offset + 2] & 0xff) << 8) |
            (hash[offset + 3] & 0xff);

        int pinValue = binary % PIN_MODULO;

        return padOutput (pinValue);
    }
    public void setKey(String newKey) {
        if(newKey.length() <= 0) {
            newKey="None";
        }
        key=newKey;
        decodedKey = Base32.decode(key);
    }

    private String padOutput(int value) {
        String result = Integer.toString(value);
        for (int i = result.length(); i < 6; i++) {
            result = "0" + result;
        }
        return result;
    }
    private static byte[] hexStr2Bytes(String hex) {
        // Adding one byte to get the right conversion
        // values starting with "0" can be converted
        byte[] bArray = new BigInteger("10" + hex,16).toByteArray();
        // Copy all the REAL bytes, not the "first"
        byte[] ret = new byte[bArray.length - 1];
        for (int i = 0; i < ret.length ; i++) {
            ret[i] = bArray[i+1];
        }
        return ret;
    }
    public static String toHexString(long l) {
        StringBuffer buf = new StringBuffer();
        String lo = Integer.toHexString((int) l);
        if (l > 0xffffffffl) {
            String hi = Integer.toHexString((int) (l >> 32));
            buf.append(hi);
            for (int i = lo.length(); i < 8; i++) {
                buf.append('0');
            }
        }
        buf.append(lo);
        return buf.toString();
    }
}
