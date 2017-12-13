package org.objectagon.core.rest2.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by christian on 2017-10-01.
 */
public class ClassDTOTest {

    ObjectMapper mapper = new ObjectMapper();

    @Test
    public void test() throws IOException {
        ClassDTO classDTO = mapper.readValue(getClass().getClassLoader().getResourceAsStream("ClassDTO.json"), ClassDTO.class);

        assertThat(classDTO.getName(), is("Hello"));
        assertThat(classDTO.getAlias(), is("Hello Again"));
        assertThat(classDTO.getFields().size(), is(0));
        assertThat(classDTO.getRelationClasses().size(), is(0));
        assertThat(classDTO.getMethods().size(), is(0));
    }

}