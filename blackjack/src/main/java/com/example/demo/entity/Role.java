package com.example.demo.entity;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(nullable = false, unique = true)
    private String name;

    //deal with lombok constructors being weird later
//    public Role() {}
//
    public Role(Long id) {
        this.id = id;
    }
//
//    public Role(String name) {
//        this.name = name;
//    }

    @Override
    public String toString() {
        return this.name;
    }

}
