package com.nikan.epuzzle.dto;

public class ResetTokenResponse {
    private String message;
    private String resetToken;

    public ResetTokenResponse(String message, String resetToken) {
        this.message = message;
        this.resetToken = resetToken;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getResetToken() { return resetToken; }
    public void setResetToken(String resetToken) { this.resetToken = resetToken; }
}