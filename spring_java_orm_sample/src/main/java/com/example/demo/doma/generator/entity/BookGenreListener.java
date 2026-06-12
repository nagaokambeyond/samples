package com.example.demo.doma.generator.entity;

import org.seasar.doma.jdbc.entity.EntityListener;
import org.seasar.doma.jdbc.entity.PostDeleteContext;
import org.seasar.doma.jdbc.entity.PostInsertContext;
import org.seasar.doma.jdbc.entity.PostUpdateContext;
import org.seasar.doma.jdbc.entity.PreDeleteContext;
import org.seasar.doma.jdbc.entity.PreInsertContext;
import org.seasar.doma.jdbc.entity.PreUpdateContext;

/**
 * 
 */
public class BookGenreListener implements EntityListener<BookGenre> {

    @Override
    public void preInsert(BookGenre entity, PreInsertContext<BookGenre> context) {
    }

    @Override
    public void preUpdate(BookGenre entity, PreUpdateContext<BookGenre> context) {
    }

    @Override
    public void preDelete(BookGenre entity, PreDeleteContext<BookGenre> context) {
    }

    @Override
    public void postInsert(BookGenre entity, PostInsertContext<BookGenre> context) {
    }

    @Override
    public void postUpdate(BookGenre entity, PostUpdateContext<BookGenre> context) {
    }

    @Override
    public void postDelete(BookGenre entity, PostDeleteContext<BookGenre> context) {
    }
}
