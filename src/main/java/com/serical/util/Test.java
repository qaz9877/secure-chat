package com.serical.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.msgpack.jackson.dataformat.MessagePackFactory;

import java.util.Arrays;

public class Test {

    private final static ObjectMapper objectMapper = new ObjectMapper(new MessagePackFactory());

    public static void main(String[] args) throws JsonProcessingException {
        final A a = new A();
        a.setId(1L);
        a.setName("cao\n");
        a.setAge(100L);
        final byte[] bytes = objectMapper.writeValueAsBytes(a);
        System.out.println(Arrays.toString(bytes));


    }
}

@Data
class A {
    private Long id;
    private String name;
    private Long age;
}
