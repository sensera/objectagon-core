package org.objectagon.core.rest2.batch.impl;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.objectagon.core.rest2.batch.BatchUpdate;
import org.objectagon.core.rest2.model.Model;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by christian on 2017-04-17.
 */
public class MapToBatchUpdateTest {

    private BatchUpdate.AddBasis construct;

    private static List<Model.Meta> EMPTY_METAS = Collections.emptyList();
    private static List<Model.InstanceClass> EMPTY_INSTANCE_CLASSES = Collections.emptyList();
    private static List<Model.Instance> EMPTY_INSTANCES = Collections.emptyList();

    Model updateCommandField;

    @Before
    public void setup() throws IOException {
        construct = mock(BatchUpdate.AddBasis.class);
        updateCommandField = mock(Model.class);
    }

    @Test
    @Ignore
    public void transferMeta() throws Exception {
        Model.Meta meta = mock(Model.Meta.class);
        List<Model.Meta> metas = Collections.singletonList(meta);

        when(updateCommandField.getClasses()).thenReturn(EMPTY_INSTANCE_CLASSES);
        when(updateCommandField.getInstances()).thenReturn(EMPTY_INSTANCES);
        when(updateCommandField.getMetas()).thenReturn(metas);

        MapToBatchUpdate.transfer(updateCommandField).accept(construct);

        verify(construct, atMost(0)).addClass(any(BatchUpdate.ClassBasis.class));
        verify(construct, times(1)).addMeta(any(BatchUpdate.MetaBasis.class));
    }

    @Test
    @Ignore
    public void transferClass() throws Exception {
        Model.InstanceClass instanceClass = mock(Model.InstanceClass.class);

        List<Model.InstanceClass> instanceClasses = Collections.singletonList(instanceClass);

        when(updateCommandField.getClasses()).thenReturn(instanceClasses);
        when(updateCommandField.getInstances()).thenReturn(EMPTY_INSTANCES);
        when(updateCommandField.getMetas()).thenReturn(EMPTY_METAS);

        MapToBatchUpdate.transfer(updateCommandField).accept(construct);


        verify(construct, atMost(0)).addMeta(any(BatchUpdate.MetaBasis.class));
        verify(construct, times(1)).addClass(any(BatchUpdate.ClassBasis.class));
    }

}