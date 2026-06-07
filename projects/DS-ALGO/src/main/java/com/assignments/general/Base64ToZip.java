package com.assignments.general;

import java.io.*;
import java.nio.file.*;
import java.util.Base64;

/**
 * Base64ToZip.java
 * Usage: java Base64ToZip <input.b64.txt> [output.zip]
 *
 * Converts a base64-encoded text file back to the original zip file.
 * If output filename is not provided, defaults to <input>.zip
 */
public class Base64ToZip {

    public static void main(String[] args) throws IOException {

//        if (args.length < 1) {
//            System.out.println("Usage: java Base64ToZip <input.b64.txt> [output.zip]");
//            System.exit(1);
//        }

//        String txtPath = args[0];
//        String zipPath = args.length >= 2 ? args[1] : deriveOutputName(txtPath);
        String zipPath = "/Users/vaibhav/Documents/Vaibhav-Disk/visual-dsa/docs/rate-limiter-v3.html";
        //  String txtPath = args.length >= 2 ? args[1] : deriveOutputName(zipPath);
        String txtPath ="/Users/vaibhav/Documents/Vaibhav-Disk/visual-dsa/docs/test.txt";


        File txtFile = new File(txtPath);
        if (!txtFile.exists()) {
            System.err.println("ERROR: File not found: " + txtPath);
            System.exit(1);
        }

        // Read base64 text (strip any trailing whitespace/newlines)
        String encoded = Files.readString(Path.of(txtPath)).strip();

        // Decode base64 back to raw bytes
        byte[] rawBytes;
        try {
            rawBytes = Base64.getDecoder().decode(encoded);
        } catch (IllegalArgumentException e) {
            System.err.println("ERROR: Failed to decode base64 content: " + e.getMessage());
            System.exit(1);
            return;
        }

        // Sanity check — zip files always start with PK (0x50 0x4B)
        if (rawBytes.length < 2 || rawBytes[0] != 0x50 || rawBytes[1] != 0x4B) {
            System.out.println("WARNING: Decoded data does not look like a zip file (missing PK header). Writing anyway.");
        }

        // Write bytes to zip file
        Files.write(Path.of(zipPath), rawBytes);

        long txtSize = txtFile.length();
        long zipSize = new File(zipPath).length();

        System.out.println("✓ Decoded successfully");
        System.out.printf("  Input  : %s  (%,d bytes)%n", txtPath, txtSize);
        System.out.printf("  Output : %s  (%,d bytes)%n", zipPath, zipSize);
    }

    private static String deriveOutputName(String txtPath) {
        String lower = txtPath.toLowerCase();
        String base  = txtPath;
        if (lower.endsWith(".b64.txt")) base = txtPath.substring(0, txtPath.length() - 8);
        else if (lower.endsWith(".txt"))  base = txtPath.substring(0, txtPath.length() - 4);
        return base + ".zip";
    }
}







