package org.objectagon.core.rest2.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by christian on 2017-10-01.
 */
public class MetaDTOTest {

    ObjectMapper mapper = new ObjectMapper();

    @Test
    public void test() throws IOException {
        MetaDTO metaDTO = mapper.readValue(getClass().getClassLoader().getResourceAsStream("MetaDTO.json"), MetaDTO.class);

        assertThat(metaDTO.getName(), is("myname"));
        assertThat(metaDTO.getAlias(), is("alias"));
        assertThat(metaDTO.getMethods().size(), is(0));
    }

}