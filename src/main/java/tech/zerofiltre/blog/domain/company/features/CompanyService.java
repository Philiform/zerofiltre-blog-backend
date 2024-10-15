package tech.zerofiltre.blog.domain.company.features;

import lombok.RequiredArgsConstructor;
import tech.zerofiltre.blog.domain.company.CompanyProvider;
import tech.zerofiltre.blog.domain.company.model.Company;
import tech.zerofiltre.blog.domain.course.CourseProvider;
import tech.zerofiltre.blog.domain.course.model.Course;
import tech.zerofiltre.blog.domain.error.ForbiddenActionException;
import tech.zerofiltre.blog.domain.error.ResourceNotFoundException;
import tech.zerofiltre.blog.domain.user.features.UserNotFoundException;
import tech.zerofiltre.blog.domain.user.model.User;

@RequiredArgsConstructor
public class CompanyService {

    public static final String DOES_NOT_EXIST = " does not exist";
    public static final String THE_COURSE_WITH_ID = "The course with id: ";

    private final CompanyProvider companyProvider;
    private final CourseProvider courseProvider;

    public Company addCourse(long companyId, User user, Course course) throws ForbiddenActionException, ResourceNotFoundException {
        Company company = companyProvider.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("The company with id " + companyId + " does not exist.", String.valueOf(companyId)));

        if(user == null || !user.isAdmin() && !user.isCompanyAdmin() && !user.isCompanyEditor())
            throw new ForbiddenActionException("You are not authorized to add a course.");

        if(!user.isAdmin() && !companyProvider.isUserPartOfCompany(companyId, user.getId()))
            throw new UserNotFoundException("The user is not part of the company.", String.valueOf(user.getId()));

        Course foundCourse = courseProvider.courseOfId(course.getId())
                .orElseThrow(() -> new ResourceNotFoundException(THE_COURSE_WITH_ID + course.getId() + DOES_NOT_EXIST, String.valueOf(course.getId())));

        company.getCourses().add(foundCourse);

        return companyProvider.save(company);
    }
}
