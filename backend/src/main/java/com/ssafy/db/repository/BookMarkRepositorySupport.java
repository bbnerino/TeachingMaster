package com.ssafy.db.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ssafy.db.entity.QBookmark;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class BookMarkRepositorySupport {
    @Autowired
    private JPAQueryFactory jpaQueryFactory;
    QBookmark qBookmark = QBookmark.bookmark;

}