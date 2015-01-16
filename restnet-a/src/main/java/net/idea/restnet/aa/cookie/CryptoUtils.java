package net.idea.restnet.aa.cookie;

/**
 * Copied from https://raw.github.com/restlet/restlet-framework-java/master/modules/org.restlet.ext.crypto/src/org/restlet/ext/crypto/CookieAuthenticator.java
 * in order to be used with Restlet 2.0-M6
 * TO BE REMOVED after upgrade
 */
/**
 * Copyright 2005-2012 Restlet S.A.S.
 * 
 * The contents of this file are subject to the terms of one of the following
 * open source licenses: Apache 2.0 or LGPL 3.0 or LGPL 2.1 or CDDL 1.0 or EPL
 * 1.0 (the "Licenses"). You can select the license that you prefer but you may
 * not use this file except in compliance with one of these Licenses.
 * 
 * You can obtain a copy of the Apache 2.0 license at
 * http://www.opensource.org/licenses/apache-2.0
 * 
 * You can obtain a copy of the LGPL 3.0 license at
 * http://www.opensource.org/licenses/lgpl-3.0
 * 
 * You can obtain a copy of the LGPL 2.1 license at
 * http://www.opensource.org/licenses/lgpl-2.1
 * 
 * You can obtain a copy of the CDDL 1.0 license at
 * http://www.opensource.org/licenses/cddl1
 * 
 * You can obtain a copy of the EPL 1.0 license at
 * http://www.opensource.org/licenses/eclipse-1.0
 * 
 * See the Licenses for the specific language governing permissions and
 * limitations under the Licenses.
 * 
 * Alternatively, you can obtain a royalty free commercial license with less
 * limitations, transferable or non-transferable, directly at
 * http://www.restlet.com/products/restlet-framework
 * 
 * Restlet is a registered trademark of Restlet S.A.S.
 */

import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.restlet.engine.util.Base64;

/**
 * Simple usage of standard cipher features from JRE.
 * 
 * @author Remi Dewitte <remi@gide.net>
 * @author Jerome Louvel
 */
public final class CryptoUtils {

    /**
     * Creates a cipher for a given algorithm and secret.
     * 
     * @param algorithm
     *            The cryptographic algorithm.
     * @param secretKey
     *            The cryptographic secret.
     * @param mode
     *            The cipher mode, either {@link Cipher#ENCRYPT_MODE} or
     *            {@link Cipher#DECRYPT_MODE}.
     * @return The new cipher.
     * @throws GeneralSecurityException
     */
    private static Cipher createCipher(String algorithm, byte[] secretKey, int mode) throws GeneralSecurityException {
	Cipher cipher = Cipher.getInstance(algorithm);
	cipher.init(mode, new SecretKeySpec(secretKey, algorithm));
	return cipher;
    }

    /**
     * Decrypts a bytes array.
     * 
     * @param algo
     *            The cryptographic algorithm.
     * @param secretKey
     *            The cryptographic secret key.
     * @param encrypted
     *            The encrypted bytes.
     * @return The decrypted content string.
     * @throws GeneralSecurityException
     */
    public static String decrypt(String algo, byte[] secretKey, byte[] encrypted) throws GeneralSecurityException {
	byte[] original = doFinal(algo, secretKey, Cipher.DECRYPT_MODE, encrypted);
	return new String(original);
    }

    /**
     * Decrypts a bytes array.
     * 
     * @param algo
     *            The cryptographic algorithm.
     * @param base64Secret
     *            The cryptographic secret key, encoded as a Base64 string.
     * @param encrypted
     *            The encrypted bytes.
     * @return The decrypted content string.
     * @throws GeneralSecurityException
     */
    public static String decrypt(String algo, String base64Secret, byte[] encrypted) throws GeneralSecurityException {
	return decrypt(algo, Base64.decode(base64Secret), encrypted);
    }

    /**
     * Does final processing.
     * 
     * @param algo
     *            The cryptographic algorithm.
     * @param secretKey
     *            The cryptographic secret key.
     * @param mode
     *            The processing mode, either {@link Cipher#DECRYPT_MODE} or
     *            {@link Cipher#ENCRYPT_MODE}.
     * @param what
     *            The byte array to process.
     * @return The processed byte array.
     * @throws GeneralSecurityException
     */
    private static byte[] doFinal(String algo, byte[] secretKey, int mode, byte[] what) throws GeneralSecurityException {
	return createCipher(algo, secretKey, mode).doFinal(what);
    }

    /**
     * Encrypts a content string.
     * 
     * @param algo
     *            The cryptographic algorithm.
     * @param secretKey
     *            The cryptographic secret key.
     * @param content
     *            The content string to encrypt.
     * @return The encrypted bytes.
     * @throws GeneralSecurityException
     */
    public static byte[] encrypt(String algo, byte[] secretKey, String content) throws GeneralSecurityException {
	return doFinal(algo, secretKey, Cipher.ENCRYPT_MODE, content.getBytes());
    }

    /**
     * Encrypts a content string.
     * 
     * @param algo
     *            The cryptographic algorithm.
     * @param base64Secret
     *            The cryptographic secret, encoded as a Base64 string.
     * @param content
     *            The content string to encrypt.
     * @return The encrypted bytes.
     * @throws GeneralSecurityException
     */
    public static byte[] encrypt(String algo, String base64Secret, String content) throws GeneralSecurityException {
	return encrypt(algo, Base64.decode(base64Secret), content);
    }

    /**
     * Private constructor to ensure that the class acts as a true utility class
     * i.e. it isn't instantiable and extensible.
     */
    private CryptoUtils() {
    }
}