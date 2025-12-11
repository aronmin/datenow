package com.grepp.datenow.app.model.course.service;

import jakarta.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BadWordFilterService {

    private final List<String> badWords = new ArrayList<>();

    private List<String> loadBadWordsFile() {
        List<String> loadedWords = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
            Objects.requireNonNull(
                getClass().getClassLoader().getResourceAsStream("bad_words.txt")), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    loadedWords.add(line.trim());
                }
            }
        } catch (IOException e) {
            log.error("Failed to load bad words from file: ", e);
        }
        return loadedWords;
    }

    @PostConstruct
    public void init() {
        this.badWords.addAll(loadBadWordsFile());
    }

    public boolean containBadWords(String text) {
        if (text == null || text.trim().isEmpty()) {
            return false;
        }
        for (String badWord : badWords) {
            if (text.contains(badWord)) {
                return true;
            }
        }
        return false;
    }

}
