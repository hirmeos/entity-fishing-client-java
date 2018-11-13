package com.scienceminer.nerd.client;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.scienceminer.nerd.data.Sentence;
import com.scienceminer.nerd.exception.ClientException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.startsWith;
import static org.apache.http.entity.ContentType.APPLICATION_JSON;

public class NerdClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(NerdClient.class);

    private static String PATH_DISAMBIGUATE = "/disambiguate";
    private static String PATH_CONCEPT = "/kb/concept";
    private static String PATH_LANGUAGE_RECOGNITION = "/language";
    private static String PATH_SEGMENTER = "/segment";

    private static int MAX_TEXT_LENGTH = 500;
    private static int SENTENCES_PER_GROUP = 10;


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

    public String getConcept(String id) {

        String response = null;
        String urlNerd = this.host + PATH_CONCEPT + "/" + id;
        if ((id != null) || (startsWith(id, "Q") || (startsWith(id, "P")))) {
            try {
                HttpClient client = HttpClientBuilder.create().build();

                HttpGet request = new HttpGet(urlNerd);
                HttpResponse httpResponse = client.execute(request);
                HttpEntity entity = httpResponse.getEntity();

                int responseId = httpResponse.getStatusLine().getStatusCode();
                if (responseId == HttpStatus.SC_OK) {
                    response = IOUtils.toString(entity.getContent(), UTF_8);
                    return response;
                } else {
                    return response;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return response;
    }


    public String getLanguage() {
        throw new UnsupportedOperationException("Method not yet implemented.");
    }


    public List<Sentence> segment(String text) {

        final URI uri;
        try {
            uri = new URIBuilder()
                    .setHost(this.host + PATH_DISAMBIGUATE)
                    .build();
        } catch (URISyntaxException e) {
            throw new ClientException("Error while setting up the url. ", e);
        }

        HttpPost httpPost = new HttpPost(uri);
        CloseableHttpClient httpResponse = HttpClients.createDefault();

        httpPost.setHeader("Content-Type", APPLICATION_JSON.toString());
        httpPost.setEntity(new StringEntity(text, UTF_8));
        CloseableHttpResponse closeableHttpResponse = null;
        try {
            closeableHttpResponse = httpResponse.execute(httpPost);
            if (closeableHttpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String jsonOut = IOUtils.toString(closeableHttpResponse.getEntity().getContent(), UTF_8);

            } else {

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private List<List<Integer>> groupSentence(int totalNumberOfSentence, int groupLength) {
        List<List<Integer>> sentenceGroups = new ArrayList<>();
        List<Integer> currentSentenceGroup = new ArrayList<>();

        for (int i = 0; i < totalNumberOfSentence; i++) {
            if (i % groupLength == 0) {
                if (currentSentenceGroup.size() > 0) {
                    sentenceGroups.add(currentSentenceGroup);
                }

                currentSentenceGroup = new ArrayList<>();
                currentSentenceGroup.add(i);
            } else {
                currentSentenceGroup.add(i);
            }
        }

        if (CollectionUtils.isNotEmpty(currentSentenceGroup)) {
            sentenceGroups.add(currentSentenceGroup);
        }

        return sentenceGroups;
    }

    public String disambiguateText(String text, String language) {

        //prepare single sentence

        List<Sentence> sentenceCoordinates = new ArrayList<>();
        sentenceCoordinates.add(new Sentence(0, StringUtils.length(text)));

        int numberOfSentences = sentenceCoordinates.size();
        List<List<Integer>> sentenceGroup = new ArrayList<>();

        if (StringUtils.length(text) > MAX_TEXT_LENGTH) {
            // we need to cut the text in more sentences

            final List<Sentence> sentences = segment(text);

            numberOfSentences = sentences.size();
            sentenceCoordinates = sentences;

            sentenceGroup = groupSentence(numberOfSentences, SENTENCES_PER_GROUP);

        } else {
//            query['sentence'] = "true"
        }



        String result = null;
        try {
            final URI uri = new URIBuilder()
                    .setScheme("http")
                    .setHost(this.host + PATH_DISAMBIGUATE)
                    .build();


            node.put("text", text);
            if (language != null) {
                ObjectNode dataNode = mapper.createObjectNode();
                dataNode.put("lang", language);
                node.set("language", dataNode);
            }
            HttpPost httpPost = new HttpPost(uri);
            CloseableHttpClient httpResponse = HttpClients.createDefault();

            httpPost.setHeader("Content-Type", APPLICATION_JSON.toString());
            httpPost.setEntity(new StringEntity(node.toString()));
            CloseableHttpResponse closeableHttpResponse = httpResponse.execute(httpPost);

            if (closeableHttpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                return IOUtils.toString(closeableHttpResponse.getEntity().getContent(), UTF_8);
            } else {
                return result;
            }

        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    public String termDisambiguate(Map<String, Double> listOfTerm, String language) {

        String result = null, term = null;
        double score = 0.0;
        try {
            final URI uri = new URIBuilder()
                    .setScheme("http")
                    .setHost(this.host + PATH_DISAMBIGUATE)
                    .build();


            ObjectMapper mapper = new ObjectMapper();
            ObjectNode node = mapper.createObjectNode();

            ArrayNode termsNode = mapper.createArrayNode();

            for (Map.Entry<String, Double> list : listOfTerm.entrySet()) {
                term = list.getKey();
                score = list.getValue();
                ObjectNode termNode = mapper.createObjectNode();
                termNode.put("term", term);
                termNode.put("score", score);
                termsNode.add(termNode);
            }

            node.set("termVector", termsNode);

            if (language != null) {
                ObjectNode dataNode = mapper.createObjectNode();
                dataNode.put("lang", language);
                node.set("language", dataNode);
            }
            HttpPost httpPost = new HttpPost(uri);
            CloseableHttpClient httpResponse = HttpClients.createDefault();

            httpPost.setHeader("Content-Type", APPLICATION_JSON.toString());
            httpPost.setEntity(new StringEntity(node.toString()));
            CloseableHttpResponse closeableHttpResponse = httpResponse.execute(httpPost);

            if (closeableHttpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                return IOUtils.toString(closeableHttpResponse.getEntity().getContent(), UTF_8);
            } else {
                return result;
            }

        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


}
