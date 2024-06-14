package project.block_chain.Test;
import java.math.BigInteger;
/**
 * Example usage
        BigInteger originalNumber = new BigInteger("199091781990917819909178");

        // Encoding the number to Base64 without padding
        String base64String = toBase64WithoutPadding(originalNumber);
        System.out.println("Base64 Representation (without padding): " + base64String);

        // Decoding the Base64 string back to the number
        BigInteger decodedNumber = decodeBase64WithoutPadding(base64String);
        System.out.println("Decoded Number: " + decodedNumber);
 */

public class Base64NoPadding{
    
    public static String toBase64(BigInteger number) {
        StringBuilder base64 = new StringBuilder();
        BigInteger radix = BigInteger.valueOf(64);

        while (number.compareTo(BigInteger.ZERO) > 0) {
            BigInteger[] quotientAndRemainder = number.divideAndRemainder(radix);
            base64.insert(0, encodeDigit(quotientAndRemainder[1].intValue()));
            number = quotientAndRemainder[0];
        }

        return base64.toString();
    }

    private static char encodeDigit(int digitValue) {
        if (digitValue >= 0 && digitValue <= 25) {
            return (char) ('A' + digitValue);
        } else if (digitValue >= 26 && digitValue <= 51) {
            return (char) ('a' + digitValue - 26);
        } else if (digitValue >= 52 && digitValue <= 61) {
            return (char) ('0' + digitValue - 52);
        } else if (digitValue == 62) {
            return '-';
        } else if (digitValue == 63) {
            return '_';
        } else {
            throw new IllegalArgumentException("Invalid digit value: " + digitValue);
        }
    }

    public static BigInteger decodeBase64(String base64String) {
        BigInteger number = BigInteger.ZERO;
        BigInteger radix = BigInteger.valueOf(64);

        // Iterate over each character in the Base64 string
        for (int i = 0; i < base64String.length(); i++) {
            char c = base64String.charAt(i);
            int digitValue = decodeDigit(c);
            number = number.multiply(radix).add(BigInteger.valueOf(digitValue));
        }

        return number;
    }

    private static int decodeDigit(char digitChar) {
        if (digitChar >= 'A' && digitChar <= 'Z') {
            return digitChar - 'A';
        } else if (digitChar >= 'a' && digitChar <= 'z') {
            return digitChar - 'a' + 26;
        } else if (digitChar >= '0' && digitChar <= '9') {
            return digitChar - '0' + 52;
        } else if (digitChar == '-') {
            return 62;
        } else if (digitChar == '_') {
            return 63;
        } else {
            throw new IllegalArgumentException("Invalid Base64 digit: " + digitChar);
        }
    }
}

