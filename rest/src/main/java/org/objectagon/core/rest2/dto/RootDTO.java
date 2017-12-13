package org.objectagon.core.rest2.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import org.objectagon.core.rest2.model.Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RootDTO implements Model {
    private List<MetaDTO> metas;
    private List<ClassDTO> classes;
    private List<InstanceDTO> instances;

    @Override public List<Meta> getMetas() {
        if (metas==null)
            return Collections.emptyList();
        return new ArrayList<>(metas);
    }

    @Override public List<InstanceClass> getClasses() {
        if (classes==null)
            return Collections.emptyList();
        return new ArrayList<>(classes);
    }

    @Override public List<Instance> getInstances() {
        if (instances==null)
            return Collections.emptyList();
        return new ArrayList<>(instances);
    }

    @JsonSetter("metas") public void setMetas(List<MetaDTO> metas) { this.metas = metas; }
    @JsonSetter("classes") public void setClasses(List<ClassDTO> classes) { this.classes = classes; }
    @JsonSetter("instances") public void setInstances(List<InstanceDTO> instances) { this.instances = instances; }

}
