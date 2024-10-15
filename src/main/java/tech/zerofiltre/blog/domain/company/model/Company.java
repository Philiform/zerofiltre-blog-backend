package tech.zerofiltre.blog.domain.company.model;

import lombok.*;
import tech.zerofiltre.blog.domain.course.model.Course;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Company {

    private long id;
    private String name;
    private Set<Course> courses = new HashSet<>();
}
