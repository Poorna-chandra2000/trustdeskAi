package com.demo.trustdeskAi.service;

import com.knuddels.jtokkit.api.EncodingType;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class MarkdownIngestionService {

    private final VectorStore vectorStore;

    public MarkdownIngestionService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public void ingestAllPolicyMarkdownFiles() throws IOException {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath:policies/*.md");

        List<Document> allChunks = new ArrayList<>();
        TextSplitter splitter=new TokenTextSplitter(); // Chunk size of 500 tokens with 50 tokens overlap

        for (Resource resource : resources) {
            TextReader textReader = new TextReader(resource);
            List<Document> docs = textReader.get();

            // Split Markdown into chunks
            List<Document> chunks = splitter.apply(docs);

            String filename = resource.getFilename();
            String docCategory = extractCategoryFromFilename(filename);

            for (Document chunk : chunks) {
                Map<String, Object> metadata = chunk.getMetadata();
                metadata.put("source_file", filename);
                metadata.put("category", docCategory);
                metadata.put("ingested_at", System.currentTimeMillis());
            }
            allChunks.addAll(chunks);
        }

        // Store vectors generated via BAAI embedding model in PgVector
        vectorStore.accept(allChunks);
    }

    private String extractCategoryFromFilename(String filename) {
        if (filename == null) return "GENERAL";
        return filename.replace(".md", "").toUpperCase();
    }
}