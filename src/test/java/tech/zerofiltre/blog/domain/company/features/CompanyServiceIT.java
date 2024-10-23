package tech.zerofiltre.blog.domain.company.features;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import tech.zerofiltre.blog.domain.company.CompanyProvider;
import tech.zerofiltre.blog.domain.company.model.Company;
import tech.zerofiltre.blog.domain.error.ForbiddenActionException;
import tech.zerofiltre.blog.domain.error.ResourceNotFoundException;
import tech.zerofiltre.blog.domain.user.UserProvider;
import tech.zerofiltre.blog.domain.user.model.User;
import tech.zerofiltre.blog.infra.providers.database.company.DBCompanyProvider;
import tech.zerofiltre.blog.infra.providers.database.user.DBUserProvider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@DataJpaTest
@Import({DBCompanyProvider.class, DBUserProvider.class})
class CompanyServiceIT {

    private CompanyService companyService;

    @Autowired
    CompanyProvider companyProvider;

    @Autowired
    UserProvider userProvider;

    @BeforeEach
    void init() {
        companyService = new CompanyService(companyProvider, userProvider);
    }

    @Test
    void givenAdminUserAndNewCompany_whenSave_thenReturnSavedCompany() throws ForbiddenActionException {
        //GIVEN
        User user = new User();
        user.getRoles().add("ROLE_ADMIN");

        Company company = new Company();
        company.setCompanyName("Company 1");
        company.setSiren("000000001");

        //WHEN
        Company response = companyService.save(user, company);

        //THEN
        assertThat(response.getCompanyName()).isEqualTo(company.getCompanyName());
        assertThat(response.getSiren()).isEqualTo(company.getSiren());
    }

    @Test
    void givenAdminUserAndNewCompany_whenPatch_thenReturnUpdatedCompany() throws ForbiddenActionException, ResourceNotFoundException {
        //GIVEN
        User user = new User();
        user.getRoles().add("ROLE_ADMIN");

        Company company = new Company();
        company.setCompanyName("Company 1");
        company.setSiren("000000001");

        company = companyProvider.save(company);

        Company patchCompany = new Company();
        patchCompany.setId(company.getId());
        patchCompany.setCompanyName(company.getCompanyName());
        company.setSiren("000000002");

        //WHEN
        Company response = companyService.patch(user, patchCompany);

        //THEN
        assertThat(response.getId()).isEqualTo(patchCompany.getId());
        assertThat(response.getCompanyName()).isEqualTo(patchCompany.getCompanyName());
        assertThat(response.getSiren()).isEqualTo(patchCompany.getSiren());
    }

    @Test
    void givenAdminUserAndExistingCompanyId_whenGetById_thenReturnCompany() throws ForbiddenActionException, ResourceNotFoundException {
        //GIVEN
        User user = new User();
        user.getRoles().add("ROLE_ADMIN");

        Company company = new Company();
        company.setCompanyName("Company 2");
        company.setSiren("000000002");

        company = companyService.save(user, company);

        //WHEN
        Company response = companyService.getById(user, company.getId()).get();

        //THEN
        assertThat(response).isEqualTo(company);
    }

    @Test
    void givenAdminUserAndExistingCompany_whenDelete_ById_thenNotThrowResourceNotFoundException() {
        //GIVEN
        User user = new User();
        user.getRoles().add("ROLE_ADMIN");

        Company company = new Company();
        company.setCompanyName("Company 2");
        company.setSiren("000000002");

        Company finalCompany = companyProvider.save(company);

        //THEN
        assertDoesNotThrow(() -> companyService.delete(user, finalCompany));
    }
/*
    @Test
    void givenAdminUserAndExistingCompanyAndNewUserId_whenAddUser_thenVerifyCallCompanyProviderAddUser() throws ForbiddenActionException, ResourceNotFoundException {
        //GIVEN
        User currentUser = new User();
        currentUser.getRoles().add("ROLE_ADMIN");

        Company company = new Company();
        company.setCompanyName("Company 2");
        company.setSiren("000000002");

        company = companyProvider.save(company);

        User user = userProvider.save(ZerofiltreUtils.createMockUser(true));

        //WHEN
        companyService.addUser(currentUser, company.getId(), user.getId());

        //THEN
        verify(companyProvider).addUser(anyLong(), anyLong());
    }
*/
}