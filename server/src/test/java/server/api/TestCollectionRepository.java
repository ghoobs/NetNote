package server.api;

import commons.Collection;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import server.database.CollectionRepository;

import java.util.*;
import java.util.function.Function;

public class TestCollectionRepository implements CollectionRepository {

    private final HashMap<Long, Collection> collections = new HashMap<>();
    private int counter = 0;

    @Override
    public void flush() {

    }

    @Override
    public <S extends Collection> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends Collection> List<S> saveAllAndFlush(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public void deleteAllInBatch(Iterable<Collection> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> longs) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public Collection getOne(Long aLong) {
        return null;
    }

    @Override
    public Collection getById(Long aLong) {
        return collections.getOrDefault(aLong, null);
    }

    @Override
    public Collection getReferenceById(Long aLong) {
        return collections.getOrDefault(aLong, null);
    }

    @Override
    public <S extends Collection> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends Collection> List<S> findAll(Example<S> example) {
        return List.of();
    }

    @Override
    public <S extends Collection> List<S> findAll(Example<S> example, Sort sort) {
        return List.of();
    }

    @Override
    public <S extends Collection> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends Collection> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends Collection> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends Collection, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public <S extends Collection> S save(S entity) {
        entity.id = counter++;
        collections.put(entity.id, entity);
        return entity;
    }

    @Override
    public <S extends Collection> List<S> saveAll(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public Optional<Collection> findById(Long aLong) {
        if (collections.containsKey(aLong)) {
            return Optional.of(collections.get(aLong));
        }
        return Optional.empty();
    }

    @Override
    public boolean existsById(Long aLong) {
        return collections.containsKey(aLong);
    }

    @Override
    public List<Collection> findAll() {
        return collections.values().stream().toList();
    }

    @Override
    public List<Collection> findAllById(Iterable<Long> longs) {
        return List.of();
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(Long aLong) {
        collections.remove(aLong);
    }

    @Override
    public void delete(Collection entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {

    }

    @Override
    public void deleteAll(Iterable<? extends Collection> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public List<Collection> findAll(Sort sort) {
        return List.of();
    }

    @Override
    public Page<Collection> findAll(Pageable pageable) {
        return null;
    }
}
