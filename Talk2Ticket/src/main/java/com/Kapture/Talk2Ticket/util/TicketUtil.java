package com.Kapture.Talk2Ticket.util;

public class TicketUtil {
    public static String convertToWavIfNecessary(String filePath) throws Exception {
        String fileExtension = filePath.substring(filePath.lastIndexOf('.') + 1).toLowerCase();
        if ("wav".equals(fileExtension)) {
            return filePath; // No conversion needed
        }

        // Convert to .wav using ffmpeg
        String wavFilePath = filePath.substring(0, filePath.lastIndexOf('.')) + ".wav";
        ProcessBuilder processBuilder = new ProcessBuilder(
                "ffmpeg", "-i", filePath, "-ar", "16000", "-ac", "1", wavFilePath);
        Process process = processBuilder.start();

        // Wait for the process to complete
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Failed to convert audio file to WAV format.");
        }

        return wavFilePath;
    }
}
