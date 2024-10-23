package tech.zerofiltre.blog.domain.company;

import tech.zerofiltre.blog.domain.company.model.Company;
import tech.zerofiltre.blog.domain.course.model.Course;
import tech.zerofiltre.blog.domain.user.model.User;

import java.util.Optional;
import java.util.Set;

public interface CompanyProvider {
    Company save(Company company);

    Optional<Company> getById(long id);

    void delete(Company company);

    void addUser(long companyId, long userId);

    User getUser(long companyId, long userId);

    void deleteUser(long companyId, long userId);

    void deleteAllUsersByCompanyId(long companyId);

    void addCourse(long companyId, long courseId);

    Course getCourse(long companyId, long courseId);

    void deleteCourse(long companyId, long courseId);

    void deleteAllCoursesByCompanyId(long companyId);

    Set<User> getUsers(long companyId);

    Set<Course> getCourses(long companyId);

}
