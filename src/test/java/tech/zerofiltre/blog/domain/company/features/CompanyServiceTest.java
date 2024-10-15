package tech.zerofiltre.blog.domain.company.features;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.zerofiltre.blog.domain.company.CompanyProvider;
import tech.zerofiltre.blog.domain.company.model.Company;
import tech.zerofiltre.blog.domain.course.CourseProvider;
import tech.zerofiltre.blog.domain.course.model.Course;
import tech.zerofiltre.blog.domain.error.ForbiddenActionException;
import tech.zerofiltre.blog.domain.error.ResourceNotFoundException;
import tech.zerofiltre.blog.domain.user.features.UserNotFoundException;
import tech.zerofiltre.blog.domain.user.model.User;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {

    private Company company;
    private User user;
    private Course course;

    private CompanyService companyService;

    @Mock
    private CompanyProvider companyProvider;

    @Mock
    private CourseProvider courseProvider;

    @BeforeEach
    void init() {
        companyService = new CompanyService(companyProvider, courseProvider);
    }

    @Test
    void givenExistingCompanyIdCompanyIdAndUserWithRoleAdminAndExistingCourse_whenAddCourse_thenReturnCompanySaved() throws ForbiddenActionException, ResourceNotFoundException {
        //GIVEN
        company = new Company();
        company.setId(1L);

        user = new User();
        user.getRoles().add("ROLE_ADMIN");

        course = new Course();

        when(companyProvider.findById(anyLong())).thenReturn(Optional.ofNullable(company));
        when(courseProvider.courseOfId(anyLong())).thenReturn(Optional.of(course));

        company.getCourses().add(course);

        when(companyProvider.save(any(Company.class))).thenReturn(company);

        //WHEN
        companyService.addCourse(company.getId(), user, course);

        //THEN
        verify(companyProvider, times(0)).isUserPartOfCompany(anyLong(), anyLong());
        verify(courseProvider).courseOfId(anyLong());
        verify(companyProvider).save(any(Company.class));
    }

    @Test
    void givenExistingCompanyIdAndUserWithRoleCompanyAdminAndExistingCourse_whenAddCourse_thenReturnCompanySaved() throws ForbiddenActionException, ResourceNotFoundException {
        //GIVEN
        company = new Company();
        company.setId(1L);

        user = new User();
        user.getRoles().add("ROLE_COMPANY_ADMIN");

        course = new Course();

        when(companyProvider.findById(anyLong())).thenReturn(Optional.ofNullable(company));
        when(companyProvider.isUserPartOfCompany(anyLong(), anyLong())).thenReturn(true);
        when(courseProvider.courseOfId(anyLong())).thenReturn(Optional.of(course));

        company.getCourses().add(course);

        when(companyProvider.save(any(Company.class))).thenReturn(company);

        //WHEN
        companyService.addCourse(company.getId(), user, course);

        //THEN
        verify(companyProvider).isUserPartOfCompany(anyLong(), anyLong());
        verify(courseProvider).courseOfId(anyLong());
        verify(companyProvider).save(any(Company.class));
    }

    @Test
    void givenExistingCompanyIdAndUserWithRoleCompanyEditorAndExistingCourse_whenAddCourse_thenReturnCompanySaved() throws ForbiddenActionException, ResourceNotFoundException {
        //GIVEN
        company = new Company();
        company.setId(1L);

        user = new User();
        user.getRoles().add("ROLE_COMPANY_EDITOR");

        course = new Course();

        when(companyProvider.findById(anyLong())).thenReturn(Optional.ofNullable(company));
        when(companyProvider.isUserPartOfCompany(anyLong(), anyLong())).thenReturn(true);
        when(courseProvider.courseOfId(anyLong())).thenReturn(Optional.of(course));

        company.getCourses().add(course);

        when(companyProvider.save(any(Company.class))).thenReturn(company);

        //WHEN
        companyService.addCourse(company.getId(), user, course);

        //THEN
        verify(companyProvider).isUserPartOfCompany(anyLong(), anyLong());
        verify(courseProvider).courseOfId(anyLong());
        verify(companyProvider).save(any(Company.class));
    }

    @Test
    void givenBadCompanyId_whenAddCourse_thenThrowResourceNotFoundException() {
        //GIVEN
        when(companyProvider.findById(anyLong())).thenReturn(Optional.empty());

        //THEN
        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> companyService.addCourse(1L, new User(), new Course()));
    }

    @Test
    void givenUserNull_whenAddCourse_thenThrowForbiddenActionException() {
        //GIVEN
        when(companyProvider.findById(anyLong())).thenReturn(Optional.of(new Company()));

        //THEN
        assertThatExceptionOfType(ForbiddenActionException.class)
                .isThrownBy(() -> companyService.addCourse(1L, null, new Course()));
    }

    @Test
    void givenUserWithRoleUser_whenAddCourse_thenThrowForbiddenActionException() {
        //GIVEN
        user = new User();
        user.getRoles().add("ROLE_USER");

        when(companyProvider.findById(anyLong())).thenReturn(Optional.of(new Company()));

        //THEN
        assertThatExceptionOfType(ForbiddenActionException.class)
                .isThrownBy(() -> companyService.addCourse(1L, user, new Course()));
    }

    @Test
    void givenUserWithRoleCompanyViewer_whenAddCourse_thenThrowForbiddenActionException() {
        //GIVEN
        user = new User();
        user.getRoles().add("ROLE_COMPANY_VIEWER");

        when(companyProvider.findById(anyLong())).thenReturn(Optional.of(new Company()));

        //THEN
        assertThatExceptionOfType(ForbiddenActionException.class)
                .isThrownBy(() -> companyService.addCourse(1L, user, new Course()));
    }

    @Test
    void givenUserNotPartOfCompany_whenAddCourse_thenThrowUserNotFoundException() {
        //GIVEN
        user = new User();
        user.getRoles().add("ROLE_COMPANY_ADMIN");

        when(companyProvider.findById(anyLong())).thenReturn(Optional.of(new Company()));
        when(companyProvider.isUserPartOfCompany(anyLong(), anyLong())).thenReturn(false);

        //THEN
        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> companyService.addCourse(1L, user, new Course()));
    }

    @Test
    void givenCourseInvalid_whenAddCourse_thenThrowUserNotFoundException() {
        //GIVEN
        user = new User();
        user.getRoles().add("ROLE_ADMIN");

        when(companyProvider.findById(anyLong())).thenReturn(Optional.of(new Company()));
        when(courseProvider.courseOfId(anyLong())).thenReturn(Optional.empty());

        //THEN
        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> companyService.addCourse(1L, user, new Course()));
    }
}