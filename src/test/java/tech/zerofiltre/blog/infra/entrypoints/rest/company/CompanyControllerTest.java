package tech.zerofiltre.blog.infra.entrypoints.rest.company;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import tech.zerofiltre.blog.domain.company.CompanyProvider;
import tech.zerofiltre.blog.domain.company.features.CompanyService;
import tech.zerofiltre.blog.domain.company.model.Company;
import tech.zerofiltre.blog.domain.error.ForbiddenActionException;
import tech.zerofiltre.blog.domain.error.ZerofiltreException;
import tech.zerofiltre.blog.domain.user.UserProvider;
import tech.zerofiltre.blog.domain.user.features.UserNotFoundException;
import tech.zerofiltre.blog.domain.user.model.User;
import tech.zerofiltre.blog.infra.entrypoints.rest.SecurityContextManager;
import tech.zerofiltre.blog.infra.entrypoints.rest.company.model.CompanyVM;
import tech.zerofiltre.blog.infra.entrypoints.rest.company.model.RegisterCompanyVM;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class CompanyControllerTest {

    private static final String COMPANY_NAME = "company 1";
    private static final String COMPANY_SIREN = "000000001";

    @MockBean
    SecurityContextManager securityContextManager;

    @MockBean
    MessageSource sources;

    @Mock
    UserProvider userProvider;

    @Mock
    CompanyProvider companyProvider;

    @Mock
    CompanyService companyService;
/*
    @Mock
    RegisterCompanyVMMapper registerCompanyVMMapper;

    @Mock
    CompanyVMMapper companyVMMapper;
*/
    CompanyController companyController;
    RegisterCompanyVM registerCompanyVM = new RegisterCompanyVM();
    CompanyVM companyVM = new CompanyVM();
    User currentUser = new User();

    @BeforeEach
    void setUp() {
        companyController = new CompanyController(userProvider, securityContextManager, companyProvider, sources);
    }

    @Test
    void givenRegisterCompanyVM_whenSave_thenVerifyCompany() throws ForbiddenActionException, UserNotFoundException {
        //TODO: malgré le when le test lève l'exception: UserNotFoundException: No authenticated user found
        //GIVEN
        currentUser.getRoles().clear();
        currentUser.getRoles().add("ROLE_ADMIN");

        when(securityContextManager.getAuthenticatedUser()).thenReturn(currentUser);

        registerCompanyVM.setCompanyName(COMPANY_NAME);
        registerCompanyVM.setSiren(COMPANY_SIREN);

        Company company = new Company();
        company.setId(1L);
        company.setCompanyName(registerCompanyVM.getCompanyName());
        company.setSiren(registerCompanyVM.getSiren());
        
        when(companyService.save(any(User.class), any(Company.class))).thenReturn(company);

        //WHEN
        companyController.save(registerCompanyVM);

        //THEN
        verify(companyService).save(any(User.class), any(Company.class));
        ArgumentCaptor<Company> captor = ArgumentCaptor.forClass(Company.class);
        verify(companyProvider).save(captor.capture());
        Company companyCaptor = captor.getValue();
        assertThat(companyCaptor.getCompanyName()).isEqualTo(COMPANY_NAME);
        assertThat(companyCaptor.getSiren()).isEqualTo(COMPANY_SIREN);
    }

    @Test
    void updateCompany() throws ZerofiltreException {
        //TODO: malgré le when le test lève l'exception: UserNotFoundException: No authenticated user found
        //GIVEN
        currentUser.getRoles().clear();
        currentUser.getRoles().add("ROLE_ADMIN");

        when(securityContextManager.getAuthenticatedUser()).thenReturn(currentUser);

        companyVM.setId(1L);
        companyVM.setCompanyName(COMPANY_NAME);
        companyVM.setSiren(COMPANY_SIREN);

        Company company = new Company();
        company.setId(1L);
        company.setCompanyName(companyVM.getCompanyName() + "1");
        company.setSiren(companyVM.getSiren());

        when(companyService.patch(any(User.class), any(Company.class))).thenReturn(company);

        //WHEN
        companyController.patch(companyVM);

        //THEN
        verify(companyService).patch(any(User.class), any(Company.class));
        ArgumentCaptor<Company> captor = ArgumentCaptor.forClass(Company.class);
        verify(companyProvider).save(captor.capture());
        Company companyCaptor = captor.getValue();
        assertThat(companyCaptor.getCompanyName()).isEqualTo(COMPANY_NAME + "1");
        assertThat(companyCaptor.getSiren()).isEqualTo(COMPANY_SIREN);
    }

    @Test
    void getById() {
    }

    @Test
    void delete() {
    }
}