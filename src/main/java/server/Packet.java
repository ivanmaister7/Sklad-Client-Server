package server;

import lombok.Data;

import javax.crypto.Cipher;
import java.nio.ByteBuffer;
import java.util.Arrays;

@Data
public class Packet {
    public static final int CONST_PACKET_BYTE = Byte.BYTES * 2 + Long.BYTES + Integer.BYTES + Short.BYTES * 2;
    public static final Byte bMagic = 0x13;

    private Byte bSrc;
    private Long bPktId;
    private Integer wLen;
    private Short crc16_1;
    private Message bMsg;
    private Short crc16_2;
    private boolean isValid;

    public Packet(Byte bSrc,Long bPktId,Message message){
        this.bSrc = bSrc;
        this.bPktId = bPktId;
        message.encrypt(message.getKey());
        this.bMsg = message;
        wLen = CONST_PACKET_BYTE + getBMsg().getCrypto().length + Message.CONST_BYTES;

        crc16_1 = (short) new CRC16(new String(ByteBuffer.allocate(CONST_PACKET_BYTE - Short.BYTES * 2)
                .put(bMagic)
                .put(bSrc)
                .putLong(bPktId)
                .putInt(wLen).array())).getCrc16();

        crc16_2 = (short) new CRC16(new String(ByteBuffer.allocate(getBMsg().getCrypto().length)
                .put(getBMsg().getCrypto()).array())).getCrc16();
        isValid = true;

    }

    public Packet(byte[] bytePacket) throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytePacket);
        Byte check = byteBuffer.get();
        if(!check.equals(bMagic)){
            throw new Exception("Invalid bMagic");
        }
        bSrc = byteBuffer.get();
        bPktId = byteBuffer.getLong();
        wLen = byteBuffer.getInt();
        crc16_1 = byteBuffer.getShort();

        bMsg = new Message();
        bMsg.setCType(byteBuffer.getInt());
        bMsg.setBUserId(byteBuffer.getInt());
        byte[] message = new byte[getWLen() - CONST_PACKET_BYTE - Message.CONST_BYTES];
        byteBuffer.get(message);
        bMsg.setMessage(new String(message));
        bMsg.setCrypto(message);
        bMsg.decrypt(Message.getKey());

        crc16_2 = byteBuffer.getShort();

        Short crc16_1_check = (short) new CRC16(new String(ByteBuffer.allocate(CONST_PACKET_BYTE - Short.BYTES * 2)
                .put(bMagic)
                .put(bSrc)
                .putLong(bPktId)
                .putInt(wLen).array())).getCrc16();
        Message m = new Message(getBMsg().getCType(),getBMsg().getBUserId(),getBMsg().getMessage());
        m.encrypt(Message.getKey());

        Short crc16_2_check = (short) new CRC16(new String(ByteBuffer.allocate(m.getCrypto().length)
                .put(m.getCrypto()).array())).getCrc16();

        isValid = true;
        if(!crc16_1.equals(crc16_1_check) || !crc16_2.equals(crc16_2_check)){
          isValid = false;
           //throw new Exception("CRC16 failed");
        }

    }

    public Packet() {

    }

    public byte[] toByte(){
        return ByteBuffer.allocate(getWLen())
                .put(bMagic)
                .put(bSrc)
                .putLong(bPktId)
                .putInt(wLen)
                .putShort(crc16_1)
                .put(bMsg.toByte2())
                .putShort(crc16_2).array();
    }

    public static void main(String[] args) throws Exception {
        Packet p = new Packet((byte) 1,2L,new Message(3,4,"test"));
        System.out.println(p);
        Packet pp = new Packet(p.toByte());
        System.out.println(pp);
    }
}
