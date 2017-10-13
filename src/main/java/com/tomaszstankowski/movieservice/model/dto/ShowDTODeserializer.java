package com.tomaszstankowski.movieservice.model.dto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.tomaszstankowski.movieservice.service.exception.invalid_body.InvalidShowException;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ShowDTODeserializer extends StdDeserializer<ShowDTO> {

    private final static DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    public ShowDTODeserializer() {
        this(null);
    }

    public ShowDTODeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public ShowDTO deserialize(JsonParser jp, DeserializationContext context) throws IOException, JsonProcessingException {
        JsonNode root = jp.getCodec().readTree(jp);
        ShowDTO dto = null;
        String title = root.get("title").textValue();
        String description = root.get("description").textValue();
        Date releaseDate;
        try {
            releaseDate = format.parse(root.get("releaseDate").textValue());
        } catch (ParseException e) {
            throw new IOException(e);
        }
        String location = root.get("location").asText();
        Set<String> genres = new HashSet<>();
        Iterator<JsonNode> iterator = root.get("genres").elements();
        while (iterator.hasNext()) {
            String genre = iterator.next().textValue();
            genres.add(genre);
        }
        if (isMovie(root)) {
            short duration = root.get("duration").shortValue();
            int boxoffice = root.get("boxoffice").intValue();
            dto = new MovieDTO(
                    title,
                    description,
                    releaseDate,
                    location,
                    genres,
                    duration,
                    boxoffice
            );
        } else if (isSerial(root)) {
            short seasons = root.get("seasons").shortValue();
            dto = new SerialDTO(
                    title,
                    description,
                    releaseDate,
                    location,
                    genres,
                    seasons
            );
        }
        if (dto == null)
            throw new InvalidShowException();
        return dto;
    }

    private boolean isMovie(JsonNode root) {
        return root.has("duration") && root.has("boxoffice");

    }

    private boolean isSerial(JsonNode root) {
        return root.has("seasons");
    }
}