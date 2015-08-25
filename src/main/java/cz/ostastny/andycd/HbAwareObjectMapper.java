package cz.ostastny.andycd;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;

public class HbAwareObjectMapper extends ObjectMapper {

    public HbAwareObjectMapper() {
        registerModule(new Hibernate4Module());
    }
}