package dev.itseatham.hcaptcha.license;

import dev.itseatham.hcaptcha.hCaptcha;
import dev.itseatham.hcaptcha.util.CC;
import org.bukkit.plugin.Plugin;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import java.util.Enumeration;
import java.util.Scanner;

public class License {

    private Plugin plugin;
    private String productKey;
    private String server;
    private String authorization;

    private static final String UNKNOWN = "unknown";
    private static String OS = System.getProperty("os.name").toLowerCase();

    public License(Plugin plugin, String licenseKey, String validationServer, String authorization) {
        this.plugin = plugin;
        this.productKey = licenseKey;
        this.server = validationServer;
        this.authorization = authorization;
    }

    public boolean verify() {
        hCaptcha.getInstance().getServer().getConsoleSender().sendMessage(CC.translate("&8[&c!&8]-----------------[Anti-Piracy]&8-----------------[&c!&8]"));
        hCaptcha.getInstance().getServer().getConsoleSender().sendMessage(CC.translate(" &c|- &7Please wait while your license is verified."));
        hCaptcha.getInstance().getServer().getConsoleSender().sendMessage(CC.translate("    License: " + productKey));
        hCaptcha.getInstance().getServer().getConsoleSender().sendMessage(" ");
        String[] respo = isValid();
        hCaptcha.getInstance().getServer().getConsoleSender().sendMessage(respo[0]);
        if (respo[0].equals("2")) {
            hCaptcha.getInstance().getServer().getConsoleSender().sendMessage(CC.translate(" &a|- Success! Your license is valid."));
            hCaptcha.getInstance().getServer().getConsoleSender().sendMessage(CC.translate("&8---------------------------------------------------"));
            hCaptcha.getInstance().getServer().getConsoleSender().sendMessage(" ");
            return true;
        } else if (respo[0].equals("3")) {
            /*System.out.println(" |- Your license is valid, but version is outdated.");
            System.out.println(" ");
            System.out.println(" |- Your version: " + plugin.getDescription().getVersion());
            System.out.println(" |- Latest version: " + respo[1].split("#")[1]);
            System.out.println("-------------------------------------------------");*/
            hCaptcha.getInstance().getServer().getConsoleSender().sendMessage(CC.translate(" &a|- Success! Your license is valid."));
            hCaptcha.getInstance().getServer().getConsoleSender().sendMessage(CC.translate("&8---------------------------------------------------"));
            hCaptcha.getInstance().getServer().getConsoleSender().sendMessage(" ");
            return true;
        } else {
            hCaptcha.getInstance().getServer().getConsoleSender().sendMessage(CC.translate(" &4|- Uh oh! Your license is invalid."));
            hCaptcha.getInstance().getServer().getConsoleSender().sendMessage(" ");
            hCaptcha.getInstance().getServer().getConsoleSender().sendMessage(CC.translate(" &c|- Reason: " + respo[1]));
            hCaptcha.getInstance().getServer().getConsoleSender().sendMessage(CC.translate("&8---------------------------------------------------"));
            hCaptcha.getInstance().getServer().getConsoleSender().sendMessage(" ");
            return false;
        }
    }

    private String requestServer(String productKey) throws IOException {
        URL url = new URL(server);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", "uLicense");
        con.setDoInput(true);
        con.setDoOutput(true);
        con.setUseCaches(false);
        String hwid = getMac();

        String outString = "{\"hwid\":\"password\",\"license\":\"key\",\"plugin\":\"NiceCar\",\"version\":\"dogpoop\"}";
        //Align HWID again here if someone tries to spoof it
        outString = outString
                .replaceAll("password", getHWID())
                .replaceAll("key", productKey)
                .replaceAll("NiceCar", plugin.getName())
                .replaceAll("dogpoop", plugin.getDescription().getVersion());

        byte[] out = outString.getBytes(StandardCharsets.UTF_8);
        int length = out.length;

        con.setFixedLengthStreamingMode(length);
        con.setRequestProperty("Authorization", this.authorization);
        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        con.connect();

        try(OutputStream os = con.getOutputStream()) {
            os.write(out);
        }

        if(!url.getHost().equals(con.getURL().getHost())) return "successful_authentication";

        try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            return response.toString();
        }
    }

    private String requestServerHTTPS(String productKey) throws IOException {
        URL url = new URL(server);
        HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", "uLicense");
        con.setDoInput(true);
        con.setDoOutput(true);
        con.setUseCaches(false);
        String hwid = getMac();

        String outString = "{\"hwid\":\"password\",\"license\":\"key\",\"plugin\":\"NiceCar\",\"version\":\"dogpoop\"}";
        //Align HWID again here if someone tries to spoof it
        outString = outString
                .replaceAll("password", getHWID())
                .replaceAll("key", productKey)
                .replaceAll("NiceCar", plugin.getName())
                .replaceAll("dogpoop", plugin.getDescription().getVersion());

        byte[] out = outString.getBytes(StandardCharsets.UTF_8);
        int length = out.length;

        con.setFixedLengthStreamingMode(length);
        con.setRequestProperty("Authorization", this.authorization);
        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        con.connect();

        try(OutputStream os = con.getOutputStream()) {
            os.write(out);
        }

        if(!url.getHost().equals(con.getURL().getHost())) return "successful_authentication";

        try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            return response.toString();
        }
    }

    public String[] isValid() {
        try {
            String response;
            if(server.contains("http")) {
                response = requestServer(productKey);
            } else {
                response = requestServerHTTPS(productKey);
            }

            if(!response.contains("{")) {
                return new String[]{"1", "ODD_RESULT"};
            }

            int respLength = response.length();

            String hash = null;
            String version = null;

            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(response);
            String neekeri = json.get("msg").toString();
            String status = json.get("status").toString();
            if(status.contains("success")) {
                hash = json.get("neekeri").toString();
                version = json.get("version").toString();
            }

            if(version != null && !version.equals(plugin.getDescription().getVersion())) {
                return new String[]{"3", "OUTDATED_VERSION#" + version};
            }

            if(hash != null && version != null) {
                String[] aa = hash.split("694201337");

                String hashed = aa[0];

                String decoded = new String(Base64.getDecoder().decode(hashed));

                if(!decoded.equals(productKey.substring(0, 2) + productKey.substring(productKey.length() - 2) + authorization.substring(0, 2))) {
                    return new String[]{"1", "FAILED_AUTHENTICATION"};
                }

                String time = String.valueOf(Instant.now().getEpochSecond());
                String unix = time.substring(0, time.length() - 2);

                long t = Long.parseLong(unix);
                long hashT = Long.parseLong(aa[1]);

                if(Math.abs(t - hashT) > 1) {
                    return new String[]{"1", "FAILED_AUTHENTICATION"};
                }
            }

            int statusLength = status.length();

            if(!isValidLength(statusLength)) {
                return new String[]{"1", neekeri};
            }

            final boolean valid = status.contains("success") && response.contains("success") && String.valueOf(statusLength).equals("7");

            return new String[]{valid ? "2" : "1", neekeri};
        } catch (IOException | ParseException ex) {
            ex.printStackTrace();
            return new String[]{"1", "ERROR"};
        }
    }

    public boolean isValidLength(int reps) {
        return reps == 7;
    }

    //Spoofed methods to trick cracker

    public boolean isValidLength22(int reps) {
        return reps == 11;
    }

    public boolean isValidLength222(int reps) {
        return reps == 44;
    }

    public boolean isValidLength2222(int reps) {
        return reps == 48;
    }

    public String getMac() throws SocketException {
        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
        StringBuilder sb = new StringBuilder();
        while (networkInterfaces.hasMoreElements()) {
            NetworkInterface ni = networkInterfaces.nextElement();
            byte[] hardwareAddress = ni.getHardwareAddress();
            if (hardwareAddress != null) {
                for (byte address : hardwareAddress) {
                    sb.append(String.format("%02X", address));
                }
                return sb.toString();
            }
        }

        return null;
    }

    public static String getHWID() {
        try {
            if (isWindows()) {
                return getWindowsIdentifier();
            } else if (isMac()) {
                return getMacOsIdentifier();
            } else if (isLinux()) {
                return getLinuxMacAddress();
            } else {
                return UNKNOWN;
            }
        } catch (Exception e) {
            return UNKNOWN;
        }
    }

    private static boolean isWindows() {
        return (OS.contains("win"));
    }

    private static boolean isMac() {
        return (OS.contains("mac"));
    }

    private static boolean isLinux() {
        return (OS.contains("inux"));
    }

    private static String getLinuxMacAddress() throws FileNotFoundException, NoSuchAlgorithmException {
        File machineId = new File("/var/lib/dbus/machine-id");
        if (!machineId.exists()) {
            machineId = new File("/etc/machine-id");
        }
        if (!machineId.exists()) {
            return UNKNOWN;
        }

        Scanner scanner = null;
        try {
            scanner = new Scanner(machineId);
            String id = scanner.useDelimiter("\\A").next();
            return hexStringify(sha256Hash(id.getBytes()));
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
    }

    private static String getMacOsIdentifier() throws SocketException, NoSuchAlgorithmException {
        NetworkInterface networkInterface = NetworkInterface.getByName("en0");
        byte[] hardwareAddress = networkInterface.getHardwareAddress();
        return hexStringify(sha256Hash(hardwareAddress));
    }

    private static String getWindowsIdentifier() throws IOException, NoSuchAlgorithmException {
        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec(new String[]{"wmic", "csproduct", "get", "UUID"});

        String result = null;
        InputStream is = process.getInputStream();
        Scanner sc = new Scanner(process.getInputStream());
        try {
            while (sc.hasNext()) {
                String next = sc.next();
                if (next.contains("UUID")) {
                    result = sc.next().trim();
                    break;
                }
            }
        } finally {
            is.close();
        }

        return result == null ? UNKNOWN : hexStringify(sha256Hash(result.getBytes()));
    }

    /**
     * Compute the SHA-256 hash of the given byte array
     *
     * @param data the byte array to hash
     * @return the hashed byte array
     * @throws NoSuchAlgorithmException
     */

    public static byte[] sha256Hash(byte[] data) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        return messageDigest.digest(data);
    }

    /**
     * Convert a byte array to its hex-string
     *
     * @param data the byte array to convert
     * @return the hex-string of the byte array
     */

    public static String hexStringify(byte[] data) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte singleByte : data) {
            stringBuilder.append(Integer.toString((singleByte & 0xff) + 0x100, 16).substring(1));
        }

        return stringBuilder.toString();
    }


}
