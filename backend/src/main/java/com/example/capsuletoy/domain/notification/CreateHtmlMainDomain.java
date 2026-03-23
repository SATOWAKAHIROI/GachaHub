package com.example.capsuletoy.domain.notification;

import java.util.List;

import org.springframework.stereotype.Component;

import com.example.capsuletoy.model.Product;

@Component
public class CreateHtmlMainDomain {
     /**
     * 新着商品通知用のHTML生成
     */
    public String buildNewProductsHtml(List<Product> products) {
        StringBuilder sb = new StringBuilder();
        String sendMessage = """
                <html>
                <body style="font-family: sans-serif; padding: 20px; background-color: #F9FAFB;">
                    <div style="max-width: 600px; margin: 0 auto; background: white; border-radius: 8px; padding: 24px;">
                        <h2 style="color: #4F46E5; margin-bottom: 16px;">🎪 スクレイピング完了通知</h2>
                        <p style="color: #374151;">新着商品: <strong>%d件</strong></p>
                        <hr style="border: 1px solid #E5E7EB;">
                """.formatted(products.size());

        sb.append(sendMessage);

        sb.append(buildNewProductsHtmlHeader(products));

        sb.append("""
                        <hr style="border: 1px solid #E5E7EB; margin-top: 16px;">
                        <p style="color: #9CA3AF; font-size: 12px; text-align: center;">
                            GachaHub - ガチャガチャ新着情報サービス<br>
                            通知を停止するにはアカウント設定から通知をOFFにしてください。
                        </p>
                    </div>
                </body>
                </html>
                """);

        return sb.toString();
    }

    // メールのヘッダー作成
    private String buildNewProductsHtmlHeader(List<Product> products) {
        if (products.isEmpty()) {
            return "<p style=\"color: #6B7280; text-align: center; padding: 20px 0;\">新着商品はありませんでした。</p>";
        }
        return buildNewProductsHtmlList(products);
    }

    // メールの新着商品リスト作成
    private String buildNewProductsHtmlList(List<Product> products) {
        StringBuilder sb = new StringBuilder();
        for (Product product : products) {
            sb.append(buildNewProductsHtmlDetail(product));
        }
        return sb.toString();
    }

    // メールの新着商品詳細作成
    private String buildNewProductsHtmlDetail(Product product) {
        StringBuilder sb = new StringBuilder();
        sb.append("<div style=\"padding: 12px 0; border-bottom: 1px solid #F3F4F6;\">");
        sb.append("<h3 style=\"color: #1F2937; margin: 0 0 4px 0;\">").append(escapeHtml(product.getProductName())).append("</h3>");

        if (product.getManufacturer() != null) {
            String displayName = "BANDAI".equals(product.getManufacturer()) ? "バンダイ" : "タカラトミーアーツ";
            sb.append("<span style=\"color: #6B7280; font-size: 13px;\">メーカー: ").append(displayName).append("</span><br>");
        }
        if (product.getPrice() != null) {
            sb.append("<span style=\"color: #4F46E5; font-weight: bold;\">").append(product.getPrice()).append("円</span><br>");
        }
        if (product.getReleaseDate() != null) {
            sb.append("<span style=\"color: #6B7280; font-size: 13px;\">発売日: ").append(product.getReleaseDate()).append("</span><br>");
        }
        if (product.getSourceUrl() != null) {
            sb.append("<a href=\"").append(escapeHtml(product.getSourceUrl())).append("\" style=\"color: #4F46E5; font-size: 13px;\">詳細を見る →</a>");
        }
        sb.append("</div>");
        return sb.toString();
    }

    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
