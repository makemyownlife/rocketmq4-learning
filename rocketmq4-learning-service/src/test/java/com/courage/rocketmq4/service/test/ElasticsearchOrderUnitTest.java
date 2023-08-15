package com.courage.rocketmq4.service.test;

import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.courage.rocketmq4.domain.po.ProductPo;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;

import java.io.IOException;

/**
 * Created by zhangyong on 2023/8/15.
 */
public class ElasticsearchOrderUnitTest {

    public static void main(String[] args) throws IOException {

        // Create the low-level client
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("elastic", "ilxw@19841201"));

        RestClientBuilder restClientBuilder = RestClient.builder(new HttpHost("localhost", 9200)).setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
            @Override
            public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
            }
        });

        RestClient restClient = restClientBuilder.build();

        // Create the transport with a Jackson mapper
        ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());

        // And create the API client
        ElasticsearchClient esClient = new ElasticsearchClient(transport);
        // ElasticsearchAsyncClient  asyncClient = new ElasticsearchAsyncClient(transport);
        // Index data to an index products


        ProductPo product = new ProductPo("abc", "Bag", 42);

        IndexRequest<Object> indexRequest = new IndexRequest.Builder<>()
                .index("products")
                .id("abc")
                .document(product)
                .build();

        esClient.index(indexRequest);

//        ProductPo product1 = new ProductPo("efg", "Bag", 42);
//
//        esClient.index(builder -> builder
//                        .index("products")
//                        .id(product1.getId())
//                        .document(product1));

    }

}
