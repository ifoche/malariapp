package org.eyeseetea.malariacare.domain.entity;

import static org.eyeseetea.malariacare.domain.common.RequiredChecker.required;

public class OptionSet implements IMetadata{
    private final String uid;
    private final String name;

    public OptionSet(String uid, String name){
        required(uid,"uid is required");
        required(name,"name is required");

        this.uid = uid;
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }
}