package com.example.capsuletoy.record;

import java.util.List;

import com.example.capsuletoy.model.Product;

public record ScrapeCore(int totalCount, List<Product> newProducts) {}  
