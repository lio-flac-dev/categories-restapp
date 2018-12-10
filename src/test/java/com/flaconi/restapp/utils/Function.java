package com.flaconi.restapp.utils;

import org.springframework.security.crypto.codec.Hex;

import javax.xml.bind.DatatypeConverter;

/**
 * Class to register some specific MySQL fucntions into H2 for testing purposes
 */
public class Function {
    public static String binToUuid(byte[] value) {
        if (value == null) {
            return null;
        }
        String stringValue = bin2hex(value);
        return stringValue.substring(0, 8) + "-" + stringValue.substring(8, 12) + "-" + stringValue.substring(12, 16) + "-" + stringValue.substring(16, 20) + "-" + stringValue.substring(20);

    }

    public static byte[] uuidToBin(String value) {
        if (value == null) {
            return null;
        }
        return hex2bin(value.replace("-", ""));
    }


    /**
     * Decodes a hexadecimally encoded binary string.
     * <p>
     * Note that this function does <em>NOT</em> convert a hexadecimal number to
     * a binary number.
     *
     * @param hex Hexadecimal representation of data.
     * @return The byte[] representation of the given data.
     * @throws NumberFormatException If the hexadecimal input string is of odd
     *                               length or invalid hexadecimal string.
     */
    public static byte[] hex2bin(String hex) throws NumberFormatException {
        if (hex.length() % 2 > 0) {
            throw new NumberFormatException("Hexadecimal input string must have an even length.");
        }
        byte[] r = new byte[hex.length() / 2];
        for (int i = hex.length(); i > 0; ) {
            r[i / 2 - 1] = (byte) (digit(hex.charAt(--i)) | (digit(hex.charAt(--i)) << 4));
        }
        return r;
    }

    private static int digit(char ch) {
        int r = Character.digit(ch, 16);
        if (r < 0) {
            throw new NumberFormatException("Invalid hexadecimal string: " + ch);
        }
        return r;
    }

    public static String bin2hex(byte[] in) {
        StringBuilder sb = new StringBuilder(in.length * 2);
        for (byte b : in) {
            sb.append(
                    forDigit((b & 0xF0) >> 4)
            ).append(
                    forDigit(b & 0xF)
            );
        }
        return sb.toString();
    }

    public static char forDigit(int digit) {
        if (digit < 10) {
            return (char) ('0' + digit);
        }
        return (char) ('A' - 10 + digit);
    }
}
