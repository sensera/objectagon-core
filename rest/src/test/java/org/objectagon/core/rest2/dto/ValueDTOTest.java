package org.objectagon.core.rest2.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by christian on 2017-10-01.
 */
public class ValueDTOTest {

    ObjectMapper mapper = new ObjectMapper();

    @Test
    public void test() throws IOException {
        ValueDTO valueDTO = mapper.readValue(getClass().getClassLoader().getResourceAsStream("ValueDTO.json"), ValueDTO.class);

        assertThat(valueDTO.getValue(), is("Mycho"));
        assertThat(valueDTO.getField(), is("MyField"));
    }

}