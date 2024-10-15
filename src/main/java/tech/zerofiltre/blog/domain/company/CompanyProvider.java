package tech.zerofiltre.blog.domain.company;

import tech.zerofiltre.blog.domain.company.model.Company;

import java.util.Optional;

public interface CompanyProvider {
    Optional<Company> findById(long id);

    Company save(Company company);

    void delete(Company company);

    boolean isUserPartOfCompany(long companyId, long userId);
}
