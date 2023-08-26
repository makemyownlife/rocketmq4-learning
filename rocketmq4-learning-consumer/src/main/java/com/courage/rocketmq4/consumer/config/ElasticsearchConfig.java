package com.courage.rocketmq4.consumer.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import nl.altindag.ssl.SSLFactory;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by zhangyong on 2023/8/26.
 */
@Configuration
public class ElasticsearchConfig {

    @Bean
    public ElasticsearchClient buildClient() {
        RestClientBuilder builder = RestClient.builder(new HttpHost("localhost", 9200, "https"));

        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("elastic", "ilxw@19841201"));

        SSLFactory sslFactory = SSLFactory.builder().withUnsafeTrustMaterial().withUnsafeHostnameVerifier().build();
        builder = builder.setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider).setSSLContext(sslFactory.getSslContext()).setSSLHostnameVerifier(sslFactory.getHostnameVerifier()));

        RestClient restClient = builder.build();

        // Create the transport with a Jackson mapper
        ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());

        // And create the API client
        ElasticsearchClient esClient = new ElasticsearchClient(transport);
        // ElasticsearchAsyncClient  asyncClient = new ElasticsearchAsyncClient(transport);
        // Index data to an index products
        return esClient;
    }

}
