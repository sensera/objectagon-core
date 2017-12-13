package org.objectagon.core.rest2.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by christian on 2017-10-01.
 */
public class InstanceDTOTest {

    ObjectMapper mapper = new ObjectMapper();

    @Test
    public void test() throws IOException {
        InstanceDTO instanceDTO = mapper.readValue(getClass().getClassLoader().getResourceAsStream("InstanceDTO.json"), InstanceDTO.class);

        assertThat(instanceDTO.getName(), is("person1"));
        assertThat(instanceDTO.getAlias(), is("personaliasiering"));
        assertThat(instanceDTO.getClassName(), is("MyClass"));
        assertThat(instanceDTO.getValues().size(), is(0));
        assertThat(instanceDTO.getRelations().size(), is(0));
    }

}