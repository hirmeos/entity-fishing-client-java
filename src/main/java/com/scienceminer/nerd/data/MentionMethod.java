package com.scienceminer.nerd.data;

public enum MentionMethod {
        wikipedia("wikipedia"),
        ner("ner"),
        wikidata("wikidata"),
        quantities("quantities"),
        grobid("grobid"),
        species("species"),
        user("user");

        private String name;

        MentionMethod(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }