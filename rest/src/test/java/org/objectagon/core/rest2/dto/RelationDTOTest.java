package org.objectagon.core.rest2.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by christian on 2017-10-01.
 */
public class RelationDTOTest {

    ObjectMapper mapper = new ObjectMapper();

    @Test
    public void test() throws IOException {
        RelationDTO relationDTO = mapper.readValue(getClass().getClassLoader().getResourceAsStream("RelationDTO.json"), RelationDTO.class);

        assertThat(relationDTO.getRelationClass(), is("relCls"));
        assertThat(relationDTO.getTargetInstance(), is("tgtInst"));
    }

}