package com.clarolab.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Component;

import java.time.Duration;


@Component
public class BucketService {

    private Bucket bucket;

    public Bucket getBucket(){
        Bandwidth limit = Bandwidth.classic(1, Refill.greedy(1, Duration.ofDays(1)));
        bucket = Bucket4j.builder()
                .addLimit(limit)
                .build();

        return bucket;
    }



}
