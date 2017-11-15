package com.xmo.demo.java7.buffer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

public class Stream2Byte {

    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }
    
    public static byte[] stream2Byte(InputStream is) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int nRead;
        byte[] data = new byte[16384];

        while ((nRead = is.read(data, 0, data.length)) != -1) {
          buffer.write(data, 0, nRead);
        }

        buffer.flush();

        return buffer.toByteArray();
    }
    
    public static byte[] usingCommonIO(InputStream is) throws IOException {
        byte[] bytes = IOUtils.toByteArray(is);
        return bytes;
    }

}
