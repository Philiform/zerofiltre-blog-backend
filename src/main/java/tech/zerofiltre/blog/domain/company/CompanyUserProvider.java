package tech.zerofiltre.blog.domain.company;

import tech.zerofiltre.blog.domain.Page;
import tech.zerofiltre.blog.domain.company.model.LinkCompanyUser;

import java.util.Optional;

public interface CompanyUserProvider {
    LinkCompanyUser save(LinkCompanyUser linkCompanyUser);

    Optional<LinkCompanyUser> findByCompanyIdAndUserId(long companyId, long userId);

    Page<LinkCompanyUser> findAllByCompanyId(int pageNumber, int pageSize, long companyId);

    void delete(LinkCompanyUser linkCompanyUser);

    void deleteAllByCompanyId(long companyId);

    void deleteAllByCompanyIdExceptAdminRole(long companyId);

    void deleteAllByUserId(long userId);

}