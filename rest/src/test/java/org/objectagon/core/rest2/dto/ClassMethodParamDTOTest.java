package org.objectagon.core.rest2.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by christian on 2017-10-01.
 */
public class ClassMethodParamDTOTest {

    ObjectMapper mapper = new ObjectMapper();

    @Test
    public void test() throws IOException {
        ClassMethodParamDTO classMethodParamDTO = mapper.readValue(getClass().getClassLoader().getResourceAsStream("ClassMethodParamDTO.json"), ClassMethodParamDTO.class);

        assertThat(classMethodParamDTO.getParam(), is("HelloParam"));
        assertThat(classMethodParamDTO.getField(), is("HelloField"));
        assertThat(classMethodParamDTO.getDefaultValue(), is("init"));
    }

}