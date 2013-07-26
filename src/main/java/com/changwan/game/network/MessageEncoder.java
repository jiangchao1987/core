package com.changwan.game.network;

import java.nio.ByteOrder;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

/**
 * A {@link ProtocolEncoder} which encodes a string into a text line
 * which ends with the delimiter.
 *
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 */
public class MessageEncoder extends ProtocolEncoderAdapter {
    private int maxCacheLength = 102400;

	private char[] sendMap = new char[] { 0x4C, 0x3A, 0x2E, 0x4F, 0x81, 0xE8,
			0xE3, 0x14, 0xA6, 0xFA, 0x44, 0xCD, 0xCA, 0x6E, 0xD1, 0x9D, 0xD7,
			0x1C, 0xC3, 0x28, 0x09, 0x29, 0xD2, 0x8F, 0x06, 0xF4, 0x5E, 0x5A,
			0xB3, 0x8B, 0xD9, 0x32, 0x6A, 0x7E, 0x74, 0x42, 0x64, 0x37, 0x72,
			0xC1, 0x97, 0x59, 0x6C, 0xD5, 0x25, 0xEC, 0x38, 0x01, 0x86, 0xBC,
			0xB8, 0x66, 0xEF, 0xBD, 0xA2, 0x57, 0x04, 0xA8, 0x0D, 0x11, 0xDF,
			0xDD, 0xC9, 0x7F, 0x54, 0xE9, 0x47, 0xC6, 0x15, 0x05, 0xED, 0x36,
			0x71, 0x65, 0xC4, 0xD3, 0xD0, 0x9F, 0xF8, 0xD8, 0x0B, 0x1B, 0xF6,
			0x6D, 0x02, 0x5B, 0x63, 0xC0, 0x45, 0xA3, 0xFC, 0xB2, 0xCE, 0x99,
			0x5D, 0x3D, 0x2D, 0xAD, 0x51, 0xAA, 0x8A, 0x67, 0xD4, 0x48, 0x30,
			0xDB, 0x34, 0x70, 0x4D, 0x76, 0xBF, 0x17, 0x8C, 0xC5, 0xCF, 0x6F,
			0xDE, 0x90, 0x35, 0xB7, 0x2B, 0xA1, 0x16, 0xAE, 0x3B, 0x1E, 0x60,
			0x4E, 0x93, 0xB5, 0x5C, 0xF5, 0xA4, 0xFE, 0x07, 0x00, 0x41, 0x3F,
			0xF0, 0x78, 0xBB, 0x53, 0x55, 0x7B, 0x6B, 0x91, 0x33, 0x62, 0x0E,
			0xF7, 0x5F, 0x9E, 0x7C, 0x9B, 0xEE, 0xB6, 0x19, 0x52, 0x40, 0x98,
			0x96, 0x18, 0xF1, 0x56, 0x43, 0xAB, 0x1D, 0x73, 0xB0, 0x2C, 0x0C,
			0xFB, 0xF2, 0x0A, 0x20, 0xE1, 0x3E, 0x46, 0xE6, 0xD6, 0x50, 0x7A,
			0x7D, 0x92, 0xC8, 0xC7, 0x77, 0x87, 0xF9, 0xEA, 0xB1, 0x80, 0xBE,
			0xCB, 0x13, 0xFF, 0x26, 0xA7, 0x21, 0x95, 0x1A, 0x58, 0x88, 0x8E,
			0x12, 0xBA, 0x85, 0x3C, 0xAF, 0x22, 0x82, 0xDA, 0x8D, 0x83, 0x84,
			0x75, 0x4A, 0xDC, 0x27, 0xF3, 0x24, 0x31, 0x49, 0xA9, 0xCC, 0x9C,
			0x89, 0x61, 0xC2, 0xE0, 0x08, 0x2A, 0xB4, 0xE7, 0x23, 0xFD, 0xE4,
			0x1F, 0xEB, 0x10, 0x2F, 0x4B, 0x0F, 0x03, 0xB9, 0xE5, 0x68, 0xA5,
			0x69, 0xE2, 0x94, 0xAC, 0x79, 0xA0, 0x9A, 0x39 };
	
	private int step = 7;
    /**
     * 创建编码器
     */
    public MessageEncoder() {
    }

    /**
     * Returns the allowed maximum size of the encoded line.
     * If the size of the encoded line exceeds this value, the encoder
     * will throw a {@link IllegalArgumentException}.  The default value
     * is 102400 (100KB).
     */
    public int getMaxCacheLength() {
        return maxCacheLength;
    }

    /**
     * Sets the allowed maximum size of the encoded line.
     * If the size of the encoded line exceeds this value, the encoder
     * will throw a {@link IllegalArgumentException}.  The default value
     * is {@link Integer#MAX_VALUE}.
     */
    public void setMaxCacheLength(int maxCacheLength) {
        if (maxCacheLength <= 0) {
            throw new IllegalArgumentException("maxLineLength: " + maxCacheLength);
        }

        this.maxCacheLength = maxCacheLength;
    }

    public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
    	byte[] msg = ((byte[]) message);
    	int length = msg.length;

        if (length > maxCacheLength) {
            throw new IllegalArgumentException("Line length: " + length);
        }
    	IoBuffer ib = IoBuffer.allocate(length + 2);
    	ib.order(ByteOrder.BIG_ENDIAN);
    	ib.putChar((char) length);
    	ib.put(transform(session, msg));
    	ib.clear();

        out.write(ib);
    }

	protected byte[] transform(IoSession session, byte[] data) {
		int counter = 0;
		if (session.containsAttribute("sendPos")) {
			counter = (Integer) session.getAttribute("sendPos");
		}

		for (int i = 0; i < data.length; i++) {
			data[i] = (byte) sendMap[(counter + (short)data[i]) & 0xFF];
			counter += step;
		}

		session.setAttribute("recvPos", counter);
		return data;
	}

    public void dispose() throws Exception {
        // Do nothing
    }
}