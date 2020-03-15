package com.nextArt.community.mapper;

import com.nextArt.community.model.Question;

public interface QuestionExtMapper {
    int incView(Question question);
    int incCommentCount(Question question);
}