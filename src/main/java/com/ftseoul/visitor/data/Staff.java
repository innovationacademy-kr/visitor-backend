package com.ftseoul.visitor.data;

import javax.persistence.*;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Staff {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, nullable = false)
    private String name;

    @Column(length = 20, nullable = false)
    private String phone;

    @Builder
    public Staff(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public void update(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "Staff{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
