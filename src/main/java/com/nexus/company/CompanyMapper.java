package com.nexus.company;

import com.nexus.utils.Mapper;
import org.springframework.stereotype.Component;

@Component
public class CompanyMapper implements Mapper<Company, CompanyResponse> {
    @Override
    public CompanyResponse map(Company company) {
        return new CompanyResponse(
          company.getId(),
          company.getUser().getId(),
          company.getUser().getAvatarUrl(),
          company.getCompanyName()
        );
    }
}
