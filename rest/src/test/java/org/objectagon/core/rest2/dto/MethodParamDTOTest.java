package org.objectagon.core.rest2.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by christian on 2017-10-01.
 */
public class MethodParamDTOTest {

    ObjectMapper mapper = new ObjectMapper();

    @Test
    public void test() throws IOException {
        MethodParamDTO methodParamDTO = mapper.readValue(getClass().getClassLoader().getResourceAsStream("MethodParamDTO.json"), MethodParamDTO.class);

        assertThat(methodParamDTO.getField(), is("HelloField"));
        assertThat(methodParamDTO.getDefaultValue(), is("init"));
    }

}