package org.objectagon.core.developer.examples;

import org.objectagon.core.developer.*;

@ObjectagonClass(meta = PersonMeta.class)
public class PersonClass {

    @ObjectagonInstance static PersonClass mega() { return new PersonClass(10, "Mega"); }

    @ObjectagonField private int age;
    @ObjectagonField private String name;

    public PersonClass(int age, String name) {
        this.age = age;
        this.name = name;
    }

    @ObjectagonMethod(methodName = "sumValue",
            fieldMappings = @ObjectagonMethodFieldMapping(name = "sumValue", field = "age")
    )
    private void addAge() {}


}

