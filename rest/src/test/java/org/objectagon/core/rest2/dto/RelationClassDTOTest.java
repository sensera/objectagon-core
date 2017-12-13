package org.objectagon.core.rest2.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by christian on 2017-10-01.
 */
public class RelationClassDTOTest {

    ObjectMapper mapper = new ObjectMapper();

    @Test
    public void test() throws IOException {
        RelationClassDTO relationClassDTO = mapper.readValue(getClass().getClassLoader().getResourceAsStream("RelationClassDTO.json"), RelationClassDTO.class);

        assertThat(relationClassDTO.getName(), is("More relations"));
        assertThat(relationClassDTO.getAlias(), is("relations alias"));
        assertThat(relationClassDTO.getTarget(), is("fantastic target"));
        assertThat(relationClassDTO.getType(), is("local"));
    }

}