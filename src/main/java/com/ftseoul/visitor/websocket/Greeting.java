package com.ftseoul.visitor.websocket;

import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@NoArgsConstructor
public class Greeting {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    public Greeting(String content) {
        this.content = content;
    }

    public String getContent(){
        return content;
    }
}
