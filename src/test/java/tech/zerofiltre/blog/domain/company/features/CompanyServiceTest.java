package tech.zerofiltre.blog.domain.company.features;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.zerofiltre.blog.domain.company.CompanyProvider;
import tech.zerofiltre.blog.domain.company.model.Company;
import tech.zerofiltre.blog.domain.error.ForbiddenActionException;
import tech.zerofiltre.blog.domain.error.ResourceNotFoundException;
import tech.zerofiltre.blog.domain.user.UserProvider;
import tech.zerofiltre.blog.domain.user.model.User;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {

    private CompanyService companyService;

    @Mock
    private CompanyProvider companyProvider;

    @Mock
    private UserProvider userProvider;

    @BeforeEach
    void init() {
        companyService = new CompanyService(companyProvider, userProvider);
    }

    @Test
    void givenAdminUserAndNewCompany_whenSave_thenVerifyCallCompanyProviderSave() throws ForbiddenActionException {
        //GIVEN
        User user = new User();
        user.getRoles().add("ROLE_ADMIN");

        Company company = new Company();
        company.setId(1L);

        when(companyProvider.save(any(Company.class))).thenReturn(company);

        //WHEN
        companyService.save(user, company);

        //THEN
        verify(companyProvider).save(any(Company.class));
    }

    @Test
    void givenUserWithRoleUserAndNewCompany_whenSave_thenThrowForbiddenActionException() {
        //GIVEN
        User user = new User();
        user.getRoles().add("ROLE_USER");

        //THEN
        assertThatExceptionOfType(ForbiddenActionException.class)
                .isThrownBy(() -> companyService.save(user, new Company()));
    }

    @Test
    void givenAdminUserAndNewCompany_whenPatch_thenVerifyCallCompanyProviderSave() throws ForbiddenActionException, ResourceNotFoundException {
        //GIVEN
        User user = new User();
        user.getRoles().add("ROLE_ADMIN");

        Company company = new Company();
        company.setId(1L);

        when(companyProvider.getById(anyLong())).thenReturn(Optional.of(company));
        when(companyProvider.save(any(Company.class))).thenReturn(company);

        //WHEN
        companyService.patch(user, company);

        //THEN
        verify(companyProvider).save(any(Company.class));
    }

    @Test
    void givenUserWithRoleUserAndNewCompany_whenPatch_thenThrowForbiddenActionException() {
        //GIVEN
        User user = new User();
        user.getRoles().add("ROLE_USER");

        //THEN
        assertThatExceptionOfType(ForbiddenActionException.class)
                .isThrownBy(() -> companyService.patch(user, new Company()));
    }

    @Test
    void givenAdminUserAndNotExitingCompany_whenPatch_thenThrowResourceNotFoundException() {
        //GIVEN
        User user = new User();
        user.getRoles().add("ROLE_ADMIN");

        when(companyProvider.getById(anyLong())).thenReturn(Optional.empty());

        //THEN
        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> companyService.patch(user, new Company()));
    }

    @Test
    void givenAdminUserAndExistingCompanyId_whenGetById_thenReturnCompany() throws ForbiddenActionException, ResourceNotFoundException {
        //GIVEN
        User user = new User();
        user.getRoles().add("ROLE_ADMIN");

        Company company = new Company();
        company.setId(2L);
        company.setCompanyName("Company 2");
        company.setSiren("000000002");

        when(companyProvider.getById(anyLong())).thenReturn(Optional.of(company));

        //WHEN
        companyService.getById(user, company.getId());

        //THEN
        verify(companyProvider).getById(anyLong());
    }

    @Test
    void givenAdminUserAndBadCompanyId_whenGetById_thenThrowResourceNotFoundException() throws ForbiddenActionException, ResourceNotFoundException {
        //GIVEN
        User user = new User();
        user.getRoles().add("ROLE_ADMIN");

        when(companyProvider.getById(anyLong())).thenReturn(Optional.empty());

        //WHEN
        companyService.getById(user, 1L);

        //THEN
        verify(companyProvider).getById(anyLong());
    }

    @Test
    void givenUserWithRoleUserAndExistingCompanyId_whenGetById_thenThrowForbiddenActionException() {
        //GIVEN
        User user = new User();
        user.getRoles().add("ROLE_USER");

        //THEN
        assertThatExceptionOfType(ForbiddenActionException.class)
                .isThrownBy(() -> companyService.getById(user, 2L));
    }

    @Test
    void givenAdminUserAndExistingCompany_whenDelete_thenVerifyCallCompanyProviderDelete() throws ForbiddenActionException {
        //GIVEN
        User user = new User();
        user.getRoles().add("ROLE_ADMIN");

        Company company = new Company();
        company.setId(2L);
        company.setCompanyName("Company 2");
        company.setSiren("000000002");

        //WHEN
        companyService.delete(user, company);

        //THEN
        verify(companyProvider).delete(any(Company.class));
    }

    @Test
    void givenAdminUserAndExistingCompanyAndNewUserId_whenAddUser_thenVerifyCallCompanyProviderAddUser() throws ForbiddenActionException, ResourceNotFoundException {
        //GIVEN
        User currentUser = new User();
        currentUser.getRoles().add("ROLE_ADMIN");

        Company company = new Company();
        company.setId(1L);

        User user = new User();
        user.setId(1L);

        when(companyProvider.getById(anyLong())).thenReturn(Optional.of(company));
        when(userProvider.userOfId(anyLong())).thenReturn(Optional.of(user));

        //WHEN
        companyService.addUser(currentUser, company.getId(), user.getId());

        //THEN
        verify(companyProvider).addUser(anyLong(), anyLong());
    }

    @Test
    void givenUserWithRoleUserAndExistingCompanyAndNewUserId_whenAddUser_thenVerifyCallCompanyProviderAddUser() {
        //GIVEN
        User currentUser = new User();
        currentUser.getRoles().add("ROLE_USER");

        //THEN
        assertThatExceptionOfType(ForbiddenActionException.class)
                .isThrownBy(() -> companyService.addCourse(currentUser, 2L, 2L));
    }

    @Test
    void givenAdminUser_whenIsAdmin_thenNotThrowException() {
        //GIVEN
        User user = new User();
        user.getRoles().add("ROLE_ADMIN");

        //THEN
        assertDoesNotThrow(() -> companyService.checkIfUserIsAdmin(user));
    }

    @Test
    void givenUserWithRoleUser_whenIsAdmin_thenThrowForbiddenActionException() {
        //GIVEN
        User user = new User();
        user.getRoles().add("ROLE_USER");

        //THEN
        assertThatExceptionOfType(ForbiddenActionException.class)
                .isThrownBy(() -> companyService.checkIfUserIsAdmin(user));
    }

    @Test
    void givenExistingCompany_whenCheckIfCompany_Exists_thenNotThrowException() {
        //GIVEN
        Company company = new Company();
        company.setId(2L);
        company.setCompanyName("Company 2");
        company.setSiren("000000002");

        when(companyProvider.getById(anyLong())).thenReturn(Optional.of(company));

        //THEN
        assertDoesNotThrow(() -> companyService.checkIfCompanyExists(company.getId()));
    }

    @Test
    void givenNotExistingCompany_whenCheckIfCompany_Exists_thenThrowResourceNotFoundException() {
        //THEN
        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> companyService.checkIfCompanyExists(1L));
    }

    @Test
    void givenExistingUser_whenCheckIfUser_Exists_thenNotThrowException() {
        //GIVEN
        User user = new User();
        user.setId(2L);
        user.getRoles().add("ROLE_ADMIN");

        when(userProvider.userOfId(anyLong())).thenReturn(Optional.of(user));

        //THEN
        assertDoesNotThrow(() -> companyService.checkIfUserExists(user.getId()));
    }

    @Test
    void givenNotExistingUser_whenCheckIfUser_Exists_thenThrowResourceNotFoundException() {
        //THEN
        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> companyService.checkIfUserExists(2L));
    }

}