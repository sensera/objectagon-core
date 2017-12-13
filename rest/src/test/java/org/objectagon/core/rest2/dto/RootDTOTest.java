package org.objectagon.core.rest2.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by christian on 2017-10-01.
 */
public class RootDTOTest {

    ObjectMapper mapper = new ObjectMapper();

    @Test public void test() throws IOException {
        RootDTO rootDTO = mapper.readValue(getClass().getClassLoader().getResourceAsStream("RootDTO.json"), RootDTO.class);

        assertThat(rootDTO.getMetas().size(), is(0));
        assertThat(rootDTO.getClasses().size(), is(0));
        assertThat(rootDTO.getInstances().size(), is(0));
    }

}