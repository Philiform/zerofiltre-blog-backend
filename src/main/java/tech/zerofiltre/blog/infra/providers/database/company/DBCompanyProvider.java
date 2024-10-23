package tech.zerofiltre.blog.infra.providers.database.company;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tech.zerofiltre.blog.domain.company.CompanyProvider;
import tech.zerofiltre.blog.domain.company.model.Company;
import tech.zerofiltre.blog.domain.course.model.Course;
import tech.zerofiltre.blog.domain.user.model.User;
import tech.zerofiltre.blog.infra.providers.database.company.mapper.CompanyJPAMapper;

import java.util.Optional;
import java.util.Set;

@Component
@Transactional
@RequiredArgsConstructor
public class DBCompanyProvider implements CompanyProvider {
    private final CompanyJPARepository repository;
    private final CompanyJPAMapper mapper = Mappers.getMapper(CompanyJPAMapper.class);

    @Override
    public Company save(Company company) {
        return mapper.fromJPA(repository.save(mapper.toJPA(company)));
    }

    @Override
    public Optional<Company> getById(long id) {
        return repository.findById(id)
                .map(mapper::fromJPA);
    }

    @Override
    public void delete(Company company) {
        repository.delete(mapper.toJPA(company));
    }

    @Override
    public void addUser(long companyId, long userId) {

    }

    @Override
    public User getUser(long companyId, long userId) {
        return null;
    }

    @Override
    public void deleteUser(long companyId, long userId) {

    }

    @Override
    public void deleteAllUsersByCompanyId(long companyId) {

    }

    @Override
    public void addCourse(long companyId, long courseId) {

    }

    @Override
    public Course getCourse(long companyId, long courseId) {
        return null;
    }

    @Override
    public void deleteCourse(long companyId, long courseId) {

    }

    @Override
    public void deleteAllCoursesByCompanyId(long companyId) {

    }

    @Override
    public Set<User> getUsers(long companyId) {
        return Set.of();
    }

    @Override
    public Set<Course> getCourses(long companyId) {
        return Set.of();
    }

}
