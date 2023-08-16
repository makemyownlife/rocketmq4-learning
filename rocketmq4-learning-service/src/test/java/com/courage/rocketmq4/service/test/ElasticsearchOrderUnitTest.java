package com.courage.rocketmq4.service.test;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.elasticsearch.core.bulk.UpdateAction;
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
import java.util.*;

/**
 * Created by zhangyong on 2023/8/15.
 */
public class ElasticsearchOrderUnitTest {

    private ElasticsearchClient esClient;

    @Test
    public void useUsernameAndPasswordHttps() throws IOException, InterruptedException {

        RestClientBuilder builder = RestClient.builder(new HttpHost("localhost", 9200, "https"));

        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("elastic", "ilxw@19841201"));

        SSLFactory sslFactory = SSLFactory.builder().withUnsafeTrustMaterial().withUnsafeHostnameVerifier().build();
        builder = builder.setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider).setSSLContext(sslFactory.getSslContext()).setSSLHostnameVerifier(sslFactory.getHostnameVerifier()));

        RestClient restClient = builder.build();

        // Create the transport with a Jackson mapper
        ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());

        // And create the API client
        esClient = new ElasticsearchClient(transport);
        // ElasticsearchAsyncClient  asyncClient = new ElasticsearchAsyncClient(transport);
        // Index data to an index products

        // 创建索引
        {
            ProductPo product = new ProductPo(1, "Bag", 12.12, 0, new Date(), new Date());

            IndexRequest<Object> indexRequest = new IndexRequest.Builder<>().index("t_product").id(String.valueOf(product.getId())).document(product).build();

            IndexResponse response = esClient.index(indexRequest);

            System.out.println("Indexed with version " + response.version());
        }

        // 查询文档
        {
            GetResponse<ProductPo> response = esClient.get(g -> g.index("t_product").id(String.valueOf(1)), ProductPo.class);

            if (response.found()) {
                ProductPo product = response.source();
                System.out.println("Product name " + product.getName());
            } else {
                System.out.println("Product not found");
            }
        }

        Thread.sleep(3000);
        // 修改文档
        {
            Map<String, Object> doc = new HashMap<String, Object>();
            doc.put("name", "my bike");
             doc.put("updateTime", new Date());

            BulkOperation op = new BulkOperation.Builder().update(i -> i.action(new UpdateAction.Builder<>().doc(doc).docAsUpsert(true).build()).id("1")).build();

            List<BulkOperation> list = Collections.singletonList(op);

            BulkResponse response = esClient.bulk(bulkBuilder -> bulkBuilder.index("t_product").operations(list));
        }

        // 模糊查询
        {

            String searchText = "bike";

            SearchResponse<ProductPo> response = esClient.search(s -> s.index("t_product").query(q -> q.match(t -> t.field("name").query(searchText))), ProductPo.class);
            System.out.println(response);
        }

        // 删除文档
        {
            //  esClient.delete(d -> d.index("products").id("1"));
        }
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

        ProductPo product = new ProductPo(2, "Bagnew", 42.1, 1, new Date(), new Date());

        IndexRequest<Object> indexRequest = new IndexRequest.Builder<>().index("products").id("abc").document(product).build();

        esClient.index(indexRequest);
    }

}
