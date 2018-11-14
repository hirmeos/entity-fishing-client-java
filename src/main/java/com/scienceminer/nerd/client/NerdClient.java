package com.scienceminer.nerd.client;


import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.scienceminer.nerd.exception.ClientException;
import com.sun.security.ntlm.Client;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.startsWith;
import static org.apache.http.entity.ContentType.APPLICATION_JSON;

public class NerdClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(NerdClient.class);

    private static String PATH_DISAMBIGUATE = "/disambiguate";
    private static String PATH_CONCEPT = "/kb/concept";
    private static String PATH_LANGUAGE_RECOGNITION = "/language";
    private static String PATH_SEGMENTATION = "/segmentation";

    private static String DEFAULT_HOST = "http://nerd.huma-num.fr/nerd/service";

    private static int MAX_TEXT_LENGTH = 500;
    private static int SENTENCES_PER_GROUP = 10;

    private static final ObjectMapper mapper = new ObjectMapper();

    private String host;
    private int port = -1;

    public NerdClient() {
        this.host = DEFAULT_HOST;
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
    }

    public NerdClient(String host) {
        this();
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


    public ObjectNode segment(String text) {
        int status = 0, retry = 0, retries = 4;
        final URI uri = getUri(PATH_SEGMENTATION);

        HttpPost httpPost = new HttpPost(uri);
        CloseableHttpClient httpResponse = HttpClients.createDefault();

//        httpPost.setHeader("Content-Type", APPLICATION_JSON.toString());
//        httpPost.setHeader("Content-Type", APPLICATION_JSON.toString());

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addTextBody("text", text);

        httpPost.setEntity(builder.build());
        CloseableHttpResponse closeableHttpResponse = null;

        do {
            try {
                closeableHttpResponse = httpResponse.execute(httpPost);
                status = closeableHttpResponse.getStatusLine().getStatusCode();
                if (status == HttpStatus.SC_OK) {
                    JsonNode actualObj = mapper.readTree(closeableHttpResponse.getEntity().getContent());
                    return actualObj.deepCopy();
                } else if (status == HttpStatus.SC_SERVICE_UNAVAILABLE) {
                    try {
                        LOGGER.warn("Got 503. Sleeping and re-trying. ");
                        Thread.sleep(900000);
                        retry++;
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                }
            } catch (JsonParseException | JsonMappingException e) {
                throw new ClientException("Cannot parse query.", e);
            } catch (IOException e) {
                throw new ClientException("Error when sending the request.", e);
            }
        } while (retry < retries && status == HttpStatus.SC_GATEWAY_TIMEOUT);

        throw new ClientException("Cannot call the segmentation service. Tried already several time without success. " +
                "Status code: " + status + ".");
    }

    private URI getUri(String path) {
        final URI uri;
        try {
            uri = new URI(this.host + path);
        } catch (URISyntaxException e) {
            throw new ClientException("Error while setting up the url. ", e);
        }
        return uri;
    }

    private List<ArrayNode> groupSentence(int totalNumberOfSentence, int groupLength) {
        List<ArrayNode> sentenceGroups = new ArrayList<>();
        ArrayNode currentSentenceGroup = mapper.createArrayNode();

        for (int i = 0; i < totalNumberOfSentence; i++) {
            if (i % groupLength == 0) {
                if (currentSentenceGroup.size() > 0) {
                    sentenceGroups.add(currentSentenceGroup);
                }

                currentSentenceGroup = mapper.createArrayNode();
                currentSentenceGroup.add(i);
            } else {
                currentSentenceGroup.add(i);
            }
        }

        if (currentSentenceGroup.size() > 0) {
            sentenceGroups.add(currentSentenceGroup);
        }

        return sentenceGroups;
    }

    public ObjectNode processQuery(ObjectNode query) {
        return processQuery(query, false);
    }

    protected ObjectNode processQuery(ObjectNode query, boolean prepared) {

        if (prepared) {
            //POST
            final URI uri = getUri(PATH_DISAMBIGUATE);

            HttpPost httpPost = new HttpPost(uri);
            CloseableHttpClient httpResponse = HttpClients.createDefault();

            httpPost.setHeader("Content-Type", APPLICATION_JSON.toString());
            String jsonInString;
            try {
                jsonInString = mapper.writeValueAsString(query);
            } catch (JsonProcessingException e) {
                throw new ClientException("Cannot serialise query. ", e);
            }

            try {
                httpPost.setEntity(new StringEntity(jsonInString));
                CloseableHttpResponse closeableHttpResponse = httpResponse.execute(httpPost);
                if (closeableHttpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    ObjectNode response = mapper.readValue(closeableHttpResponse.getEntity().getContent(), ObjectNode.class);
                    return response;
                }
                throw new ClientException("Error (code: " + closeableHttpResponse.getStatusLine().getStatusCode()
                        + ") when processing the query \n " + query);
            } catch (UnsupportedEncodingException e) {
                throw new ClientException("Unsupported encoding when setting entity into post. ", e);
            } catch (ClientProtocolException e) {
                throw new ClientException("Client protocol exception. ", e);
            } catch (IOException e) {
                throw new ClientException("Generic exception when sending POST. ", e);
            }
        }

        String text = query.get("text").asText();

        //prepare single sentence
        ArrayNode sentencesCoordinatesArray = mapper.createArrayNode();
        final ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("offsetStart", 0);
        objectNode.put("offsetEnd", StringUtils.length(text));
        sentencesCoordinatesArray.add(objectNode);

        int totalNumberOfSentences = sentencesCoordinatesArray.size();
        List<ArrayNode> sentenceGroup = new ArrayList<>();

        if (StringUtils.length(text) > MAX_TEXT_LENGTH) {
            // we need to cut the text in more sentences

            final ObjectNode sentences = segment(text);

            sentencesCoordinatesArray = (ArrayNode) sentences.get("sentences");
            totalNumberOfSentences = sentencesCoordinatesArray.size();

            sentenceGroup = groupSentence(totalNumberOfSentences, SENTENCES_PER_GROUP);

        } else {
           query.put("sentence", true); 
        }

        if (totalNumberOfSentences > 1) {
            query.set("sentences", sentencesCoordinatesArray);
        }

        if (sentenceGroup.size() > 0) {
            System.out.println("Splitting the query in " + sentenceGroup.size() + " requests.");
            for (ArrayNode group : sentenceGroup) {
                query.set("processSentence", group);
                final ObjectNode jsonNodes = processQuery(query, true);
                query.set("entities", jsonNodes.get("entities"));
                query.set("language", jsonNodes.get("language"));
            }
        } else {
            final ObjectNode jsonNodes = processQuery(query, true);
            query.set("entities", jsonNodes.get("entities"));
            query.set("language", jsonNodes.get("language"));

        }
        return query;
    }

    public ObjectNode disambiguateText(String text, String language) {
        ObjectNode query = mapper.createObjectNode();
        query.put("text", text);
        if (isNotBlank(language)) {
            final ObjectNode lang = mapper.createObjectNode().put("lang", language);
            query.set("language", lang);
        }

//        if (CollectionUtils.isNotEmpty(entities)) {
//            query.setEntities(entities);
//        }

        return processQuery(query);
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

        } catch (URISyntaxException | UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


}
