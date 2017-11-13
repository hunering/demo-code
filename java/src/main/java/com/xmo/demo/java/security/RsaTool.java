package com.xmo.demo.java.security;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Enumeration;

public class RsaTool {

    public static void main(String[] args) {
        try {
            KeyPair keyPair = buildKeyPair();
            System.out.println("public key format: " +
                    keyPair.getPublic().getFormat());
            System.out.println("private key format: " +
                    keyPair.getPrivate().getFormat());
            saveKeyToFile(keyPair);
            PrivateKey privateKey = loadPrivateKey();
            PublicKey publicKey = loadPublicKey();

            byte[] macAddress = getFirstMacAddress();
            System.out.println("Mac address is: ");
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < macAddress.length; i++) {
                sb.append(String.format("%02X%s", macAddress[i], (i < macAddress.length - 1) ? "-" : ""));  
            }
            
            System.out.println(sb);

            sign(privateKey, macAddress, "license");
            
            //macAddress[0] = 1;
            boolean isValid = checkSign(publicKey, macAddress, "license");
            if (isValid) {
                System.out.println("license is valid");
            } else {
                System.out.println("license is NOT valid");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static byte[] getFirstMacAddress() throws SocketException {
        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();

        while (networkInterfaces.hasMoreElements()) {
            NetworkInterface network = networkInterfaces.nextElement();
            byte[] mac = network.getHardwareAddress();
            if (mac != null) {
                return mac;
            }
        }
        return null;
    }

    public boolean checkReg() {
        boolean bRet = false;
        try {

            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();

            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface network = networkInterfaces.nextElement();
                byte[] mac = network.getHardwareAddress();
            }

            KeyPair keyPair = buildKeyPair();
            keyPair.getPrivate().getFormat();

        } catch (Exception e) {

        }

        return bRet;
    }

    public static KeyPair buildKeyPair() throws NoSuchAlgorithmException {
        final int keySize = 2048;
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(keySize);
        return keyPairGenerator.genKeyPair();
    }

    public static void saveKeyToFile(KeyPair keyPair) throws IOException {
        String outFile = "private";
        FileOutputStream out = new FileOutputStream(outFile + ".key");
        out.write(keyPair.getPrivate().getEncoded());
        out.close();

        outFile = "public";
        out = new FileOutputStream(outFile + ".pub");
        out.write(keyPair.getPublic().getEncoded());
        out.close();
    }

    public static PrivateKey loadPrivateKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        String keyFile = "private.key";
        /* Read all bytes from the private key file */
        Path path = Paths.get(keyFile);
        byte[] bytes = Files.readAllBytes(path);

        /* Generate private key. */
        PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(bytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PrivateKey pvt = kf.generatePrivate(ks);
        return pvt;
    }

    public static PublicKey loadPublicKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        String keyFile = "public.pub";
        /* Read all the public key bytes */
        Path path = Paths.get(keyFile);
        byte[] bytes = Files.readAllBytes(path);

        /* Generate public key. */
        X509EncodedKeySpec ks = new X509EncodedKeySpec(bytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PublicKey pub = kf.generatePublic(ks);
        return pub;
    }

    public static void sign(PrivateKey privateKey, byte[] data, String signFile)
            throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, IOException {
        Signature sign = Signature.getInstance("SHA256withRSA");
        sign.initSign(privateKey);
        sign.update(data, 0, data.length);

        OutputStream out = null;
        try {
            out = new FileOutputStream(signFile);
            byte[] signature = sign.sign();
            out.write(signature);
        } finally {
            if (out != null)
                out.close();
        }
    }

    public static boolean checkSign(PublicKey publicKey, byte[] data, String signFile)
            throws NoSuchAlgorithmException, InvalidKeyException, IOException, SignatureException {
        Signature sign = Signature.getInstance("SHA256withRSA");
        sign.initVerify(publicKey);
        sign.update(data, 0, data.length);
        Path path = Paths.get(signFile);
        byte[] bytes = Files.readAllBytes(path);
        return sign.verify(bytes);
    }
}
