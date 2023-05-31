/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.model.helper.tag;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

public class Tag {

    private Tag() {
    }

    public static class Builder {

        private Set<String> tags = new TreeSet<String>();

        public Builder with(String...strings) {
            tags.addAll(Arrays.asList(strings));
            return this;
        }

        public Builder with(String string) {
            String[] split = string.split(",");
            tags.addAll(Arrays.asList(split));
            return this;
        }

        public Builder clean(){
            tags.clear();
            return this;
        }

        public String build() {
            return tags.toString();
        }

        public Builder remove(String...strings) {
            tags.removeAll(Arrays.asList(strings));
            return this;
        }
    }

    public static void main(String...s){
        String build = new Builder().with("Hello").with("TTriage").with("World").with("NEED-TRIAGE, NO-PREVIOUS-TRIAGE, MARKED_AS-FAIL").remove("World").build();
        System.out.println(build);
    }
}
