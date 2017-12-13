package org.objectagon.core.rest2.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by christian on 2017-10-01.
 */
public class FieldDTOTest {

    ObjectMapper mapper = new ObjectMapper();

    @Test
    public void test() throws IOException {
        FieldDTO fieldDTO = mapper.readValue(getClass().getClassLoader().getResourceAsStream("FieldDTO.json"), FieldDTO.class);

        assertThat(fieldDTO.getName(), is("myfield"));
        assertThat(fieldDTO.getAlias(), is("myfieldAlias"));
        assertThat(fieldDTO.getType(), is("text"));
        assertThat(fieldDTO.getDefaultValue(), is("defaultIsThis"));
    }

}