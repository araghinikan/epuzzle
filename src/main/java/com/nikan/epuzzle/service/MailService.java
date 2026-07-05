package com.nikan.epuzzle.service;

import com.nikan.epuzzle.model.ApplicationUser;
import com.nikan.epuzzle.model.MailType;
import com.nikan.epuzzle.repository.ApplicationUserRepository;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class MailService {
    private final MailSenderService mailSenderService;
    private final ApplicationUserRepository userRepository;

    public MailService(MailSenderService mailSenderService, ApplicationUserRepository userRepository) {
        this.mailSenderService = mailSenderService;
        this.userRepository = userRepository;
    }

    public void sendSignupVerification(String email, String code) {
        ApplicationUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String subject = "کد تأیید ثبت نام";
        String htmlContent = getVerificationEmailHtml(user.getUsername(), code, "ثبت نام");
        mailSenderService.sendHtmlMail(email, htmlContent, subject);
    }

    public void sendResetPasswordVerification(String email, String code) {
        ApplicationUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String subject = "کد بازیابی رمز عبور";
        String htmlContent = getVerificationEmailHtml(user.getUsername(), code, "بازیابی رمز عبور");
        mailSenderService.sendHtmlMail(email, htmlContent, subject);
    }

    public void sendChangePasswordVerification(String email, String code) {
        ApplicationUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String subject = "کد تغییر رمز عبور";
        String htmlContent = getVerificationEmailHtml(user.getUsername(), code, "تغییر رمز عبور");
        mailSenderService.sendHtmlMail(email, htmlContent, subject);
    }

    private String getVerificationEmailHtml(String username, String code, String action) {
        return "<!DOCTYPE html>\n" +
                "<html dir=\"rtl\" lang=\"fa\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>کد تأیید</title>\n" +
                "    <style>\n" +
                "        @import url('https://fonts.googleapis.com/css2?family=Vazirmatn:wght@300;400;500;600;700&display=swap');\n" +
                "        * { margin: 0; padding: 0; box-sizing: border-box; }\n" +
                "        body {\n" +
                "            font-family: 'Vazirmatn', Tahoma, Arial, sans-serif;\n" +
                "            background-color: #f8fafc;\n" +
                "            display: flex;\n" +
                "            justify-content: center;\n" +
                "            align-items: center;\n" +
                "            min-height: 100vh;\n" +
                "            padding: 20px;\n" +
                "        }\n" +
                "        .container {\n" +
                "            max-width: 600px;\n" +
                "            width: 100%;\n" +
                "            background: white;\n" +
                "            border-radius: 24px;\n" +
                "            box-shadow: 0 20px 60px rgba(0,0,0,0.08);\n" +
                "            overflow: hidden;\n" +
                "            direction: rtl;\n" +
                "        }\n" +
                "        .header {\n" +
                "            background: linear-gradient(135deg, #2563eb, #1e40af);\n" +
                "            padding: 40px 30px;\n" +
                "            text-align: center;\n" +
                "        }\n" +
                "        .header h1 { color: white; font-size: 28px; font-weight: 700; margin: 0; }\n" +
                "        .header .subtitle { color: rgba(255,255,255,0.8); font-size: 16px; margin-top: 8px; font-weight: 300; }\n" +
                "        .content { padding: 40px 30px; }\n" +
                "        .greeting { font-size: 20px; font-weight: 600; color: #1e293b; margin-bottom: 16px; }\n" +
                "        .greeting span { color: #2563eb; }\n" +
                "        .message { color: #475569; font-size: 16px; line-height: 1.8; margin-bottom: 30px; }\n" +
                "        .code-container {\n" +
                "            background: #f1f5f9;\n" +
                "            border-radius: 16px;\n" +
                "            padding: 25px;\n" +
                "            text-align: center;\n" +
                "            margin: 25px 0;\n" +
                "            border: 2px dashed #cbd5e1;\n" +
                "        }\n" +
                "        .code {\n" +
                "            font-size: 48px;\n" +
                "            font-weight: 700;\n" +
                "            color: #2563eb;\n" +
                "            letter-spacing: 12px;\n" +
                "            font-family: 'Courier New', monospace;\n" +
                "            background: white;\n" +
                "            padding: 15px 20px;\n" +
                "            border-radius: 12px;\n" +
                "            display: inline-block;\n" +
                "            box-shadow: 0 2px 8px rgba(0,0,0,0.05);\n" +
                "        }\n" +
                "        .code-label { display: block; font-size: 14px; color: #64748b; margin-top: 12px; font-weight: 500; }\n" +
                "        .info-box {\n" +
                "            background: #f0fdf4;\n" +
                "            border-right: 4px solid #22c55e;\n" +
                "            padding: 16px 20px;\n" +
                "            border-radius: 8px;\n" +
                "            margin: 20px 0;\n" +
                "        }\n" +
                "        .info-box p { color: #166534; font-size: 14px; margin: 0; line-height: 1.6; }\n" +
                "        .warning-box {\n" +
                "            background: #fef2f2;\n" +
                "            border-right: 4px solid #ef4444;\n" +
                "            padding: 16px 20px;\n" +
                "            border-radius: 8px;\n" +
                "            margin: 20px 0;\n" +
                "        }\n" +
                "        .warning-box p { color: #991b1b; font-size: 14px; margin: 0; line-height: 1.6; }\n" +
                "        .footer {\n" +
                "            padding: 25px 30px;\n" +
                "            background: #f8fafc;\n" +
                "            text-align: center;\n" +
                "            border-top: 1px solid #e2e8f0;\n" +
                "        }\n" +
                "        .footer p { color: #94a3b8; font-size: 13px; margin: 4px 0; line-height: 1.6; }\n" +
                "        .footer .company { color: #64748b; font-weight: 500; }\n" +
                "        @media (max-width: 480px) {\n" +
                "            .content { padding: 25px 20px; }\n" +
                "            .header { padding: 30px 20px; }\n" +
                "            .header h1 { font-size: 22px; }\n" +
                "            .code { font-size: 32px; letter-spacing: 8px; padding: 12px 16px; }\n" +
                "            .greeting { font-size: 18px; }\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"container\">\n" +
                "        <div class=\"header\">\n" +
                "            <h1>🔐 کد تأیید</h1>\n" +
                "            <div class=\"subtitle\">تأیید امنیتی</div>\n" +
                "        </div>\n" +
                "        <div class=\"content\">\n" +
                "            <div class=\"greeting\">سلام <span>" + username + "</span> عزیز،</div>\n" +
                "            <div class=\"message\">برای " + action + "، لطفاً کد تأیید زیر را وارد کنید.</div>\n" +
                "            <div class=\"code-container\">\n" +
                "                <div class=\"code\">" + code + "</div>\n" +
                "                <span class=\"code-label\">🔑 کد تأیید شما</span>\n" +
                "            </div>\n" +
                "            <div class=\"info-box\">\n" +
                "                <p>⏱️ این کد تا <strong>۵ دقیقه</strong> اعتبار دارد.</p>\n" +
                "            </div>\n" +
                "            <div class=\"warning-box\">\n" +
                "                <p>🛡️ هرگز این کد را با کسی به اشتراک نگذارید.</p>\n" +
                "            </div>\n" +
                "        </div>\n" +
                "        <div class=\"footer\">\n" +
                "            <p class=\"company\">© 2026 تیم پشتیبانی</p>\n" +
                "            <p>این ایمیل به صورت خودکار ارسال شده است، لطفاً به آن پاسخ ندهید.</p>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";
    }
}