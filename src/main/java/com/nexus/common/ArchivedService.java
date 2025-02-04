package com.nexus.common;

import java.util.List;

public class ArchivedService {

    public static <REPO extends ArchivableRepository<Type,?>, Type> List<Type> determine(ArchivableQueryType a, REPO repo) {

        List<Type> result;

        switch (a) {
            case ALL -> result = repo.findAll();
            case Archived -> result = repo.findAllArchived();
            default -> result = repo.findAllNonArchived();
        }

        return result;
    }
}
