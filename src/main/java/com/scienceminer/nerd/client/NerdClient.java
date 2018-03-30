package com.scienceminer.nerd.client;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

import static org.apache.http.entity.ContentType.APPLICATION_JSON;

public class NerdClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(NerdClient.class);


    private static String PATH_DISAMBIGUATE = "/disambiguate";
    private static String PATH_CONCEPT = "/kb/concept";
    private static String PATH_LANGUAGE_RECOGNIZE = "/kb/concept";
    private static String PATH_SEGMENTER = "/kb/concept";

    private String host;
    private int port = -1;

    public NerdClient() {

    }

    public NerdClient(String host) {
        this.host = host;
    }

    public NerdClient(String host, int port) {
        this(host);
        this.port = port; 
    }

    public String getConcept() {
        throw new UnsupportedOperationException("Method not yet implemented.");
    }


    public String getLanguage() {
        throw new UnsupportedOperationException("Method not yet implemented.");
    }



    public String segment() {
        throw new UnsupportedOperationException("Method not yet implemented.");
    }


    public String disambiguateText(String text, String language) {

        try {
            final URI uri = new URIBuilder()
                    .setScheme("http")
                    .setPort(port)
                    .setHost(this.host + PATH_DISAMBIGUATE)
                    .build();


            ObjectMapper mapper = new ObjectMapper();
            ObjectNode node = mapper.createObjectNode();
            node.put("text", text);
            if (language != null) {
                ObjectNode dataNode = mapper.createObjectNode();
                dataNode.put("lang", language);
                node.set("language", dataNode);
            }


            HttpPost post = new HttpPost(uri);
            CloseableHttpClient httpclient = HttpClients.createDefault();
            post.setHeader("Content-Type", APPLICATION_JSON.toString());
            post.setEntity(new StringEntity(node.toString()));
            CloseableHttpResponse response = httpclient.execute(post);

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                return IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8);
            } else {
                LOGGER.error("Error disambiguating text: " + response.getStatusLine().getStatusCode());
                return null;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

}
