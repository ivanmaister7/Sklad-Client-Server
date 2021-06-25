package server;

import lombok.*;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

@Data
@RequiredArgsConstructor
@NoArgsConstructor
@ToString(exclude = { "crypto" })
public class Message {

    @NonNull
    private Integer cType;
    @NonNull
    private Integer bUserId;
    @NonNull
    private String message;

    static Cipher cipher;
    private byte[] crypto = "EmptyMessage".getBytes();

    public static final int CONST_BYTES = Integer.BYTES * 2;

    public int getMessageBytesLength(){
        return CONST_BYTES + getMessage().getBytes().length;
    }

    public byte[] toByte(){
        return ByteBuffer.allocate(getMessageBytesLength())
                .putInt(getCType())
                .putInt(getBUserId())
                .put(getMessage().getBytes()).array();
    }
    public byte[] toByte2(){
        return ByteBuffer.allocate(Message.CONST_BYTES + crypto.length)
                .putInt(getCType())
                .putInt(getBUserId())
                .put(crypto).array();
    }

    public void encrypt(SecretKeySpec secretKey) {
        try {
            cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            crypto = cipher.doFinal(getMessage().getBytes());
            setMessage(new String(crypto));
        } catch (Exception e) { }
    }

    public void decrypt(SecretKeySpec secretKey) {
        try {
            cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            crypto = cipher.doFinal(crypto);
            setMessage(new String(crypto));
        } catch (Exception e) { }
    }

    public static SecretKeySpec getKey(){
        return new SecretKeySpec("1234567890qwerty".getBytes(), "AES");
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, NoSuchPaddingException, IllegalBlockSizeException {
        Message m = new Message(3,4,"test");

        SecretKeySpec key = m.getKey();

        m.encrypt(key);
        System.out.println(m);

        System.out.println(m.toByte().length);
        System.out.println(m.toByte2().length);
        System.out.println(Arrays.toString(m.toByte()));
        System.out.println(Arrays.toString(m.toByte2()));

        m.decrypt(key);
        System.out.println(m);

    }

}
