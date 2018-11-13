package com.scienceminer.nerd.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scienceminer.nerd.exception.ClientException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This is the POJO object for representing input and output "enriched" query.
 * Having Jersey supporting JSON/object mapping, this permits to consume JSON post query.
 *
 * @author Patrice
 */
public class NerdQuery {

    public static final String QUERY_TYPE_TEXT = "text";
    public static final String QUERY_TYPE_SHORT_TEXT = "shortText";
    public static final String QUERY_TYPE_TERM_VECTOR = "termVector";
    public static final String QUERY_TYPE_LAYOUT_TOKENS = "layoutToken";
    public static final String QUERY_TYPE_INVALID = "invalid";

    // main text component
    private String text = null;

    // search query
    private String shortText = null;

    // language of the query
    private Language language = null;

    // the result of the query disambiguation and enrichment for each identified entities
    private List<NerdEntity> entities = null;

    // the sentence position if such segmentation is to be realized
    private List<Sentence> sentences = null;

    // runtime in ms of the last processing
    private long runtime = 0;

    // mention techniques, specify the method for which the mentions are extracted
    private List<MentionMethod> mentions =
            Arrays.asList(MentionMethod.ner, MentionMethod.wikipedia);

    private boolean nbest = false;
    private boolean sentence = false;
    private String customisation = "generic";

    // list of sentences to be processed
    private Integer[] processSentence = null;

    // query-based threshold, override default values in the config file only for the present query
    private double minSelectorScore = 0.0;
    private double minRankerScore = 0.0;

    // the type of document structure to be considered in case of processing 
    // a complete document 
    private String structure = null;

    public NerdQuery() {
    }

    public NerdQuery(NerdQuery query) {
        this.text = query.getText();
        this.shortText = query.getShortText();

        this.language = query.getLanguage();
        this.entities = query.getEntities();
        this.sentences = query.getSentences();

        this.mentions = query.getMentions();
        this.nbest = query.getNbest();
        this.sentence = query.getSentence();
        this.customisation = query.getCustomisation();
        this.processSentence = query.getProcessSentence();

        this.minSelectorScore = query.getMinSelectorScore();
        this.minRankerScore = query.getMinRankerScore();

        this.structure = query.getStructure();
    }


    @JsonIgnore
    public String getTextOrShortText() {
        if (text == null) {
            return shortText;
        }

        return text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


    public void setLanguage(Language lang) {
        this.language = lang;
    }

    public Language getLanguage() {
        return language;
    }


    public List<MentionMethod> getMentions() {
        return this.mentions;
    }

    public void setMentions(List<MentionMethod> mentions) {
        this.mentions = mentions;
    }

    public void addMention(MentionMethod mention) {
        if (this.mentions == null)
            mentions = new ArrayList<MentionMethod>();
        mentions.add(mention);
    }

    public void setRuntime(long tim) {
        runtime = tim;
    }

    public long getRuntime() {
        return runtime;
    }

    public List<NerdEntity> getEntities() {
        return entities;
    }


    public void setEntities(List<NerdEntity> entities) {
        this.entities = entities;
    }

    public void addNerdEntities(List<NerdEntity> theEntities) {
        if (theEntities != null) {
            if (this.entities == null) {
                this.entities = new ArrayList<NerdEntity>();
            }
            for (NerdEntity entity : theEntities) {
                this.entities.add(entity);
            }
        }
    }

    public List<Sentence> getSentences() {
        return sentences;
    }

    public void setSentences(List<Sentence> sentences) {
        this.sentences = sentences;
    }

    public String getShortText() {
        return shortText;
    }

    public void setShortText(String shortText) {
        this.shortText = shortText;
    }

    public boolean getNbest() {
        return nbest;
    }

    public void setNbest(boolean nbest) {
        this.nbest = nbest;
    }

    public boolean getSentence() {
        return sentence;
    }

    public void setSentence(boolean sentence) {
        this.sentence = sentence;
    }

    public String getCustomisation() {
        return customisation;
    }

    public void setCustomisation(String customisation) {
        this.customisation = customisation;
    }

    public Integer[] getProcessSentence() {
        return processSentence;
    }

    public void setProcessSentence(Integer[] processSentence) {
        this.processSentence = processSentence;
    }

    public void addEntities(List<NerdEntity> newEntities) {
        if (entities == null) {
            entities = new ArrayList<>();
        }
        if (CollectionUtils.isEmpty(newEntities)) {
            return;
        }
        for (NerdEntity entity : newEntities) {
            entities.add(entity);
        }
    }

    public void addEntity(NerdEntity entity) {
        if (entities == null) {
            entities = new ArrayList<>();
        }
        entities.add(entity);
    }

    public double getMinSelectorScore() {
        return this.minSelectorScore;
    }

    public void setMinSelectorScore(double minSelectorScore) {
        this.minSelectorScore = minSelectorScore;
    }

    public double getMinRankerScore() {
        return this.minRankerScore;
    }

    public void setMinRankerScore(double minRankerScore) {
        this.minRankerScore = minRankerScore;
    }

    public String getStructure() {
        return this.structure;
    }

    public void setStructure(String structure) {
        this.structure = structure;
    }


    public static NerdQuery fromJson(String theQuery) throws ClientException {
        if (StringUtils.isEmpty(theQuery)) {
            throw new ClientException("The query cannot be null or empty:\n " + theQuery);
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
            return mapper.readValue(theQuery, NerdQuery.class);
        } catch (JsonGenerationException | JsonMappingException e) {
            throw new ClientException("The JSON query is invalid, please check the format.");
        } catch (IOException e) {
            throw new ClientException("Some serious error when deserialize the JSON object. Please check the format.");
        }
    }

}