//package com.demo.trustdeskAi.config;
//
//import org.springframework.ai.embedding.EmbeddingModel;
//import org.springframework.ai.transformers.TransformersEmbeddingModel;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.io.ClassPathResource;
//
//@Configuration
//public class EmbeddingConfig {
//
//    @Bean
//    public EmbeddingModel embeddingModel() {
//        TransformersEmbeddingModel embeddingModel = new TransformersEmbeddingModel();
//
//        // Force local files directly from src/main/resources/models/
//        embeddingModel.setModelResource(new ClassPathResource("models/bge-small-en-v1.5.onnx"));
//        embeddingModel.setTokenizerResource(new ClassPathResource("models/tokenizer.json"));
//
//        // Disable internet resource caching check
//        embeddingModel.setResourceCacheDirectory(null);
//
//        return embeddingModel;
//    }
//}