package tech.zerofiltre.blog.domain.course.features.enrollment;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.zerofiltre.blog.domain.FinderRequest;
import tech.zerofiltre.blog.domain.Page;
import tech.zerofiltre.blog.domain.course.ChapterProvider;
import tech.zerofiltre.blog.domain.course.CourseProvider;
import tech.zerofiltre.blog.domain.course.EnrollmentProvider;
import tech.zerofiltre.blog.domain.course.model.Course;
import tech.zerofiltre.blog.domain.course.model.Enrollment;
import tech.zerofiltre.blog.domain.error.ForbiddenActionException;
import tech.zerofiltre.blog.domain.error.ResourceNotFoundException;
import tech.zerofiltre.blog.domain.user.model.User;
import tech.zerofiltre.blog.doubles.EnrollmentProviderSpy;
import tech.zerofiltre.blog.doubles.FoundChapterProviderSpy;
import tech.zerofiltre.blog.doubles.Found_Published_WithKnownAuthor_CourseProvider_Spy_And_2Lessons;
import tech.zerofiltre.blog.doubles.NotFoundEnrollmentProviderDummy;
import tech.zerofiltre.blog.util.DataChecker;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static tech.zerofiltre.blog.domain.FinderRequest.Filter.COMPLETED;
import static tech.zerofiltre.blog.domain.FinderRequest.Filter.INACTIVE;
import static tech.zerofiltre.blog.domain.article.model.Status.DRAFT;

@ExtendWith(MockitoExtension.class)
class FindEnrollmentTest {

    @Mock
    DataChecker checker;

    @Test
    void findEnrollment_returns_theProperPage() {
        //given
        EnrollmentProviderSpy enrollmentProviderSpy = new EnrollmentProviderSpy();
        CourseProvider courseProvider = new Found_Published_WithKnownAuthor_CourseProvider_Spy_And_2Lessons();
        ChapterProvider chapterProvider = new FoundChapterProviderSpy();
        FindEnrollment findEnrollment = new FindEnrollment(enrollmentProviderSpy, courseProvider, chapterProvider, checker);
        //when
        FinderRequest request = new FinderRequest(0, 3, DRAFT, new User());
        Page<Course> courses = findEnrollment.of(request);

        //then
        assertThat(enrollmentProviderSpy.ofCalled).isTrue();
        assertThat(courses).isNotNull();
        assertThat(courses.getContent()).isNotNull();
        assertThat(courses.getContent().size()).isEqualTo(2);
        assertThat(courses.getHasNext()).isTrue();
        assertThat(courses.getHasPrevious()).isTrue();
        assertThat(courses.getNumberOfElements()).isEqualTo(2);
        assertThat(courses.getPageNumber()).isEqualTo(1);
        assertThat(courses.getPageSize()).isEqualTo(2);
        assertThat(courses.getTotalNumberOfElements()).isEqualTo(10);
        assertThat(courses.getTotalNumberOfPages()).isEqualTo(4);
        courses.getContent().forEach(course -> {
            assertThat(course.getEnrolledCount()).isEqualTo(1);
            assertThat(course.getLessonsCount()).isEqualTo(2);
        });
    }

    @Test
    void findEnrollment_calls_EnrollmentProvider_withTheInactiveParam() {
        //given
        EnrollmentProviderSpy enrollmentProvider = new EnrollmentProviderSpy();
        CourseProvider courseProvider = new Found_Published_WithKnownAuthor_CourseProvider_Spy_And_2Lessons();
        ChapterProvider chapterProvider = new FoundChapterProviderSpy();
        FindEnrollment findEnrollment = new FindEnrollment(enrollmentProvider, courseProvider, chapterProvider, checker);
        //when
        FinderRequest request = new FinderRequest(0, 3, DRAFT, new User());
        request.setFilter(INACTIVE);
        findEnrollment.of(request);

        //then
        assertThat(enrollmentProvider.ofCalled).isTrue();
        assertThat(enrollmentProvider.ofFilter).isNotNull();
        assertThat(enrollmentProvider.ofFilter).isEqualTo(INACTIVE);
    }

    @Test
    void findEnrollment_calls_EnrollmentProvider_withTheCompletedParam() {
        //given
        EnrollmentProviderSpy enrollmentProvider = new EnrollmentProviderSpy();
        CourseProvider courseProvider = new Found_Published_WithKnownAuthor_CourseProvider_Spy_And_2Lessons();
        ChapterProvider chapterProvider = new FoundChapterProviderSpy();
        FindEnrollment findEnrollment = new FindEnrollment(enrollmentProvider, courseProvider, chapterProvider, checker);
        //when
        FinderRequest request = new FinderRequest(0, 3, DRAFT, new User());
        request.setFilter(COMPLETED);
        findEnrollment.of(request);

        //then
        assertThat(enrollmentProvider.ofCalled).isTrue();
        assertThat(enrollmentProvider.ofFilter).isNotNull();
        assertThat(enrollmentProvider.ofFilter).isEqualTo(COMPLETED);
    }

    @Test
    void findAEnrollmentForUser_returns_theProperOne() throws ResourceNotFoundException, ForbiddenActionException {
        //given
        EnrollmentProviderSpy enrollmentProvider = new EnrollmentProviderSpy();
        CourseProvider courseProvider = new Found_Published_WithKnownAuthor_CourseProvider_Spy_And_2Lessons();
        ChapterProvider chapterProvider = new FoundChapterProviderSpy();
        FindEnrollment findEnrollment = new FindEnrollment(enrollmentProvider, courseProvider, chapterProvider, checker);
        //when
        Enrollment enrollment = findEnrollment.of(0, 1, 0, false);
        //then
        assertThat(enrollment).isNotNull();
        verify(checker, never()).companyUserExists(anyLong(), anyLong());
        verify(checker, never()).companyCourseExists(anyLong(), anyLong());
    }

    @Test
    void findAEnrollmentForAdminUser_returns_theProperOne() throws ResourceNotFoundException, ForbiddenActionException {
        //given
        EnrollmentProviderSpy enrollmentProvider = new EnrollmentProviderSpy();
        CourseProvider courseProvider = new Found_Published_WithKnownAuthor_CourseProvider_Spy_And_2Lessons();
        ChapterProvider chapterProvider = new FoundChapterProviderSpy();
        FindEnrollment findEnrollment = new FindEnrollment(enrollmentProvider, courseProvider, chapterProvider, checker);

        when(checker.companyUserExists(anyLong(), anyLong())).thenReturn(true);
        when(checker.companyCourseExists(anyLong(), anyLong())).thenReturn(true);

        //when
        Enrollment enrollment = findEnrollment.of(0, 1, 1, true);

        //then
        assertThat(enrollment).isNotNull();
        verify(checker).companyUserExists(anyLong(), anyLong());
        verify(checker).companyCourseExists(anyLong(), anyLong());
    }

    @Test
    void findAnEnrollment_throwsResourceNotFoundException() {
        //given
        EnrollmentProvider enrollmentProvider = new NotFoundEnrollmentProviderDummy();
        CourseProvider courseProvider = new Found_Published_WithKnownAuthor_CourseProvider_Spy_And_2Lessons();
        ChapterProvider chapterProvider = new FoundChapterProviderSpy();
        FindEnrollment findEnrollment = new FindEnrollment(enrollmentProvider, courseProvider, chapterProvider, checker);

        //when
        //then
        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> findEnrollment.of(1, 1, 0, true))
                .withMessage("Enrollment not found");
    }

}