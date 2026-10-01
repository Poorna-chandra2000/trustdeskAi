package com.demo.trustdeskAi.tools;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class PolicyLookupTool {

    private final VectorStore vectorStore;

    public PolicyLookupTool(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @Tool(description = "Retrieves trust, warranty, security, shipping, billing, coupon, and refund policies from the vector knowledge base.")
    public String queryPolicyKnowledgeBase(
            @ToolParam(description = "The specific topic or query extracted from the ticket (e.g. 'swelling battery', 'duplicate charge', 'refund return window')", required = true)
            String query,

            @ToolParam(description = "Optional policy category filter (e.g. 'REFUND_POLICY', 'WARRANTY_POLICY', 'BILLING_POLICY')", required = false)
            String category) {

        log.info("==================================================");
        log.info("🔧 [TOOL CALL] Gemini triggered PolicyLookupTool");
        log.info("🔍 [SEARCH QUERY] '{}'", query);
        log.info("==================================================");

        // Fixed: Use SearchRequest.builder() instead of SearchRequest.query()
        SearchRequest.Builder requestBuilder = SearchRequest.builder()
                .query(query)
                .topK(4);

        if (category != null && !category.isBlank()) {
            FilterExpressionBuilder filterBuilder = new FilterExpressionBuilder();

            log.info("Applying category filter: {}", category.toUpperCase());

            requestBuilder.filterExpression(filterBuilder.eq("category", category.toUpperCase()).build());
        }

        List<Document> results = vectorStore.similaritySearch(requestBuilder.build());

        log.info("Found {} matching policy rules in the knowledge base.", results.size());
        log.info("Chunks of {} matching policy rules in the knowledge base. ", results.stream());

        if (results.isEmpty()) {
            return "No matching policy rules found in the knowledge base.";
        }

        return results.stream()
                .map(doc -> String.format("[Source Document: %s]\n%s",
                        doc.getMetadata().getOrDefault("source_file", "UNKNOWN"),
                        doc.getText()))
                .collect(Collectors.joining("\n---\n"));
    }
}