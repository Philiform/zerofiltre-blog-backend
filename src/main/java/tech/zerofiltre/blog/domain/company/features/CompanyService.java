package tech.zerofiltre.blog.domain.company.features;

import lombok.RequiredArgsConstructor;
import tech.zerofiltre.blog.domain.company.CompanyProvider;
import tech.zerofiltre.blog.domain.company.model.Company;
import tech.zerofiltre.blog.domain.course.model.Course;
import tech.zerofiltre.blog.domain.error.ForbiddenActionException;
import tech.zerofiltre.blog.domain.error.ResourceNotFoundException;
import tech.zerofiltre.blog.domain.user.UserProvider;
import tech.zerofiltre.blog.domain.user.model.User;

import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
public class CompanyService {

    private final CompanyProvider companyProvider;
    private final UserProvider userProvider;

    public Company save(User user, Company company) throws ForbiddenActionException {
        checkIfUserIsAdmin(user);

        return companyProvider.save(company);
    }

    public Company patch(User user, Company company) throws ForbiddenActionException, ResourceNotFoundException {
        checkIfUserIsAdmin(user);
        checkIfCompanyExists(company.getId());

        return companyProvider.save(company);
    }

    public Optional<Company> getById(User user, long id) throws ForbiddenActionException, ResourceNotFoundException {
        checkIfUserIsAdmin(user);

        return companyProvider.getById(id);
    }

    public void delete(User user, Company company) throws ForbiddenActionException {
        checkIfUserIsAdmin(user);

        //TODO: décommenter les 2 lignes + modifier les tests
//        companyProvider.deleteAllUsersByCompanyId(company.getId());
//        companyProvider.deleteAllCoursesByCompanyId(company.getId());
        companyProvider.delete(company);
    }

    public void addUser(User currentUser, long companyId, long userId) throws ForbiddenActionException, ResourceNotFoundException {
        checkIfUserIsAdmin(currentUser);
        checkIfCompanyExists(companyId);
        checkIfUserExists(userId);

        companyProvider.addUser(companyId, userId);
    }

    public User getUser(User user, long companyId, long userId) throws ForbiddenActionException {
        checkIfUserIsAdmin(user);

        return companyProvider.getUser(companyId, userId);
    }

    public void deleteUser(User currentUser, long companyId, long userId) throws ForbiddenActionException {
        checkIfUserIsAdmin(currentUser);

        companyProvider.deleteUser(companyId, userId);
    }

    public void addCourse(User user, long companyId, long courseId) throws ForbiddenActionException {
        checkIfUserIsAdmin(user);

        companyProvider.addCourse(companyId, courseId);
    }

    public Course getCourse(User user, long companyId, long courseId) throws ForbiddenActionException {
        checkIfUserIsAdmin(user);

        return companyProvider.getCourse(companyId, courseId);
    }

    public void deleteCourse(User user, long companyId, long courseId) throws ForbiddenActionException {
        checkIfUserIsAdmin(user);

        companyProvider.deleteCourse(companyId, courseId);
    }

    public Set<User> getUsers(User user, long companyId) throws ForbiddenActionException {
        checkIfUserIsAdmin(user);

        return companyProvider.getUsers(companyId);
    }

    public Set<Course> getCourses(User user, long companyId) throws ForbiddenActionException {
        checkIfUserIsAdmin(user);

        return companyProvider.getCourses(companyId);
    }

    void checkIfUserIsAdmin(User user) throws ForbiddenActionException {
        if(!user.isAdmin())
            throw new ForbiddenActionException("The user must be administrator.");
    }

    void checkIfCompanyExists(long companyId) throws ResourceNotFoundException {
        companyProvider.getById(companyId).orElseThrow(() ->
                new ResourceNotFoundException("We could not find the company", String.valueOf(companyId)));

    }

    void checkIfUserExists(long userId) throws ResourceNotFoundException {
        userProvider.userOfId(userId).orElseThrow(() ->
                new ResourceNotFoundException("We could not find the user", String.valueOf(userId)));
    }
}
