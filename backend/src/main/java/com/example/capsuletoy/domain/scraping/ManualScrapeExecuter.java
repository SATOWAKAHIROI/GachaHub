package com.example.capsuletoy.domain.scraping;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.capsuletoy.model.Product;
import com.example.capsuletoy.scraper.BaseScraper;
import com.example.capsuletoy.service.product.ProductUpdateService;
import com.example.capsuletoy.service.scraping.ScrapeService;

@Component
public class ManualScrapeExecuter {
    private static final Logger logger = LoggerFactory.getLogger(ScrapeService.class);

    @Autowired
    private ProductUpdateService productUpdateService;

    public List<Product> scrapeProducts(BaseScraper scraper){
        List<Product> scrapedProducts = scraper.scrape();
        return scrapedProducts;
    }

    private boolean trySaveProduct(Product product){
        try {
            Product saved = productUpdateService.saveScrapedProduct(product);
            boolean checkNewProduct = saved.getIsNew() != null && saved.getIsNew();
            return checkNewProduct;
        } catch (Exception e) {
            logger.error("Error saving product: {}", product.getProductName(), e);
            return false;
        }
    }

    public List<Product> getNewProductList(List<Product> scrapedProducts){
        Stream<Product> scrapedProductStream = scrapedProducts.stream();
        Stream<Product> scrapedProductStreamFiltered = scrapedProductStream.filter(this::trySaveProduct);
        List<Product> newProducts = scrapedProductStreamFiltered.collect(Collectors.toList());
        return newProducts;
    }
}
