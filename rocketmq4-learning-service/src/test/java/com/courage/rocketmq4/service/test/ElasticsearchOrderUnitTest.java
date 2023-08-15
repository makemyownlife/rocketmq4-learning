package com.courage.rocketmq4.service.test;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.courage.rocketmq4.domain.po.ProductPo;
import nl.altindag.ssl.SSLFactory;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by zhangyong on 2023/8/15.
 */
public class ElasticsearchOrderUnitTest {

    @Test
    public void useUsernameAndPasswordHttps() throws IOException {
        SSLFactory sslFactory = SSLFactory.builder().withUnsafeTrustMaterial().withUnsafeHostnameVerifier().build();

        RestClientBuilder builder = RestClient.builder(new HttpHost("localhost", 9200, "https"));

        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("elastic", "ilxw@19841201"));

        builder = builder.setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider).setSSLContext(sslFactory.getSslContext()).setSSLHostnameVerifier(sslFactory.getHostnameVerifier()));

        RestClient restClient = builder.build();

        // Create the transport with a Jackson mapper
        ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());

        // And create the API client
        ElasticsearchClient esClient = new ElasticsearchClient(transport);
        // ElasticsearchAsyncClient  asyncClient = new ElasticsearchAsyncClient(transport);
        // Index data to an index products

        ProductPo product = new ProductPo("abc", "Bag", 42);

        IndexRequest<Object> indexRequest = new IndexRequest.Builder<>().index("products").id("abc").document(product).build();

        esClient.index(indexRequest);

        ProductPo product1 = new ProductPo("efg", "Bag", 42);

        esClient.index(builder2 -> builder2.index("products").id(product1.getId()).document(product1));

    }

    @Test
    public void useApiKey() throws IOException {

        String apiKey = "cnRVUy1Ja0JZYUtuSTRuMG1oRkk6RVFSdTk2T2NRb1cyYVdLRTB4TjktQQ==";

        RestClientBuilder builder = RestClient.builder(new HttpHost("localhost", 9200, "https"));

        SSLFactory sslFactory = SSLFactory.builder().withUnsafeTrustMaterial().withUnsafeHostnameVerifier().build();

        RestClient restClient = builder.setDefaultHeaders(new Header[]{new BasicHeader("Authorization", "ApiKey " + apiKey)}).setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder.setSSLContext(sslFactory.getSslContext()).setSSLHostnameVerifier(sslFactory.getHostnameVerifier())).build();

        // Create the transport with a Jackson mapper
        ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());

        // And create the API client
        ElasticsearchClient esClient = new ElasticsearchClient(transport);
        // ElasticsearchAsyncClient  asyncClient = new ElasticsearchAsyncClient(transport);
        // Index data to an index products

        ProductPo product = new ProductPo("abc", "Bagnew", 42);

        IndexRequest<Object> indexRequest = new IndexRequest.Builder<>().index("products").id("abc").document(product).build();

        esClient.index(indexRequest);
    }

}
