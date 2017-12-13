package org.objectagon.core.rest2.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by christian on 2017-10-01.
 */
public class MethodDTOTest {

    ObjectMapper mapper = new ObjectMapper();

    @Test
    public void test() throws IOException {
        MethodDTO methodDTO = mapper.readValue(getClass().getClassLoader().getResourceAsStream("MethodDTO.json"), MethodDTO.class);

        assertThat(methodDTO.getCode(), is("invokeWorker.setValue(\"sumValue\").set(invokeWorker.getValue(\"sumValue\").asNumber() + invokeWorker.getValue(\"addValue\").asNumber());"));
        assertThat(methodDTO.getParams().size(), is(0));
    }

}