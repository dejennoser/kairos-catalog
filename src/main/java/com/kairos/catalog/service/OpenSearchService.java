package com.kairos.catalog.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.catalog.entity.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.action.delete.DeleteRequest;
import org.opensearch.action.index.IndexRequest;
import org.opensearch.action.search.SearchRequest;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.common.xcontent.XContentType;
import org.opensearch.index.query.MultiMatchQueryBuilder;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenSearchService {

    private final RestHighLevelClient client;
    private final ObjectMapper objectMapper;

    @Value("${opensearch.index}")
    private String index;

    public void indexProduct(Product product) {
        try {
            Map<String, Object> doc = Map.of(
                    "id", product.getId().toString(),
                    "name", product.getName(),
                    "description", product.getDescription() != null ? product.getDescription() : "",
                    "category", product.getCategory(),
                    "price", product.getPrice(),
                    "stock", product.getStock()
            );

            String json = objectMapper.writeValueAsString(doc);

            IndexRequest request = new IndexRequest(index)
                    .id(product.getId().toString())
                    .source(json, XContentType.JSON);
            client.index(request, RequestOptions.DEFAULT);
            log.info("Index product: {}", product.getId());

        }catch (Exception e) {
            log.error("Failed to index product in OpenSearch", e);
        }
    }

    public void deleteProduct(UUID id) {
        try {
            DeleteRequest request = new DeleteRequest(index, id.toString());
            client.delete(request, RequestOptions.DEFAULT);
            log.info("Delete product from index: {}", id);
        } catch (Exception e) {
            log.error("Failed to delete product from OpenSearch", e);
        }
    }

    public List<UUID> fuzzySearch(String query) {
        try {
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            sourceBuilder.query(
                    QueryBuilders.multiMatchQuery(query, "name", "description", "category")
                            .fuzziness("AUTO")
                            .type(MultiMatchQueryBuilder.Type.BEST_FIELDS)
            );

            SearchRequest searchRequest = new SearchRequest(index);
            searchRequest.source(sourceBuilder);

            SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);

            List<UUID> ids = new ArrayList<>();
            Arrays.stream(response.getHits().getHits())
                    .forEach(hit -> ids.add(UUID.fromString(hit.getId())));
            return ids;

        } catch (Exception e) {
            log.error("Failed to search in OpenSearch", e);
            return List.of();
        }
    }

}
