package org.objectagon.core.rest2.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by christian on 2017-10-01.
 */
public class ClassMethodDTOTest {

    ObjectMapper mapper = new ObjectMapper();

    @Test
    public void test() throws IOException {
        ClassMethodDTO classMethodDTO = mapper.readValue(getClass().getClassLoader().getResourceAsStream("ClassMethodDTO.json"), ClassMethodDTO.class);

        assertThat(classMethodDTO.getMethod(), is("Skunke"));
        assertThat(classMethodDTO.getMeta(), is("Meta"));
        assertThat(classMethodDTO.getParams().size(), is(0));
    }

}