package com.omnibuscode.logic.jbg.mall.sample_receipts;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Emart20241017Extractor {
    // 상품 정보를 저장할 클래스
    static class Product {
        String name;
        int unitPrice;
        int quantity;

        public Product(String name, int unitPrice, int quantity) {
            this.name = name;
            this.unitPrice = unitPrice;
            this.quantity = quantity;
        }

        @Override
        public String toString() {
            return "상품명: " + name + ", 단가: " + unitPrice + ", 수량: " + quantity;
        }
    }

    public static void main(String[] args) {
        // 파일 경로 설정
        String filePath = "D:/SVN/BOX_MANAGER/JINIEBOX/output/server/java/jiniebox/src/test/com/omnibuscode/logic/jbg/mall/sample_receipts/Emart20241017.txt"; // 파일 경로를 여기에 입력

        // 상품 정보를 저장할 리스트
        List<Product> products = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                // 각 줄에서 상품 정보(이름, 단가, 수량)를 추출
                if (line.matches("\\d{2}\\s+.*")) { // 숫자로 시작하는 줄을 상품 정보로 간주
                    // 상품 이름 추출 (숫자와 공백 제거 후, 이름만 남김)
                    String productName = line.replaceAll("^\\d{2}\\s+", "").replaceAll("\\s+\\(?.*", "").trim();

                    // 다음 줄에서 단가와 수량 추출
                    line = br.readLine();
                    if (line != null && line.matches("\\s*\\d{13}\\s+\\d+,?\\d*\\s+\\d+\\s+\\d+,?\\d*")) {
                        String[] parts = line.trim().split("\\s+");
                        int unitPrice = Integer.parseInt(parts[1].replace(",", "")); // 단가
                        int quantity = Integer.parseInt(parts[2]); // 수량

                        // 상품 정보 리스트에 추가
                        products.add(new Product(productName, unitPrice, quantity));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 추출된 상품 정보 출력
        System.out.println("Extracted Product Information:");
        for (Product product : products) {
            System.out.println(product);
        }
    }
}
