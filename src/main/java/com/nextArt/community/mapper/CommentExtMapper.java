package com.nextArt.community.mapper;


import com.nextArt.community.model.Comment;

public interface CommentExtMapper {
    int incCommentCount(Comment comment);
}