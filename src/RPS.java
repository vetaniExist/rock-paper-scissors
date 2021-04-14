import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;

public class RPS {
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    private String[] turns;
    private int winLine;

    RPS(String[] args) throws Exception {
        if (args.length < 3) {
            throw new Exception("Arguments length must be >= 3. Example: rock paper scissors");
        }
        if (args.length % 2 == 0) {
            throw new Exception("Arguments length must be odd. Example: rock paper scissors");
        }

        HashSet unicue = new HashSet<>(Arrays.asList(args));
        if (unicue.size() != args.length) {
            throw new Exception("Arguments must be unicue.Example: rock paper scissors");
        }

        turns = args;
        winLine = (args.length - 1) / 2;
    }

    public void playGame() {
        byte[] key = generateSecretKey();
        Mac hmac = generateHmac(key);

        int computerTurn = getComputerTurn();

        byte[] res = hmac.doFinal(turns[computerTurn].getBytes(StandardCharsets.UTF_8));
        System.out.println("HMAC: " + bytesToHex(res));

        Scanner scanner = new Scanner(System.in);
        int userTurn = getUserTurn(scanner);
        scanner.close();

        System.out.println(getWinner(userTurn, computerTurn));
        System.out.println("HMAC key(hex): " + bytesToHex(key));
    }

    public byte[] generateSecretKey() {
        byte bytes[] = new byte[16];
        new SecureRandom().nextBytes(bytes);
        return bytes;
    }

    public Mac generateHmac(byte[] key) {
        Mac hmac = null;
        try {
            hmac = Mac.getInstance("HmacSHA256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        SecretKey secret_key = new SecretKeySpec(key, "HmacSHA256");
        try {
            hmac.init(secret_key);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return hmac;
    }

    public int getComputerTurn() {
        return new SecureRandom().nextInt(turns.length);
    }

    public int getUserTurn(Scanner scanner) {
        System.out.println("make turn: ");
        for (int i = 0; i < turns.length; i += 1) {
            System.out.println(i + 1 + ": " + turns[i]);
        }
        System.out.println("0 - exit");
        if (!scanner.hasNextInt()) {
            scanner.next();
            return getUserTurn(scanner);
        }
        int userTurn = scanner.nextInt(10) - 1;
        if (userTurn == -1) {
            System.exit(0);
        } else if (userTurn < turns.length) {
            return userTurn;
        }
        return getUserTurn(scanner);
    }

    public String getWinner(int userTurn, int computerTurn) {
        System.out.println("Your turn: " + turns[userTurn]);
        System.out.println("Computer turn: " + turns[computerTurn]);
        if (userTurn == computerTurn) {
            return "draw";
        }
        int delta = userTurn - computerTurn;
        if (delta > 0 && delta <= winLine){
            return "You win!";
        }
        return "Computer win!";
    }

    public static String bytesToHex(byte[] bytes) {
        // https://stackoverflow.com/questions/9655181/how-to-convert-a-byte-array-to-a-hex-string-in-java
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
}