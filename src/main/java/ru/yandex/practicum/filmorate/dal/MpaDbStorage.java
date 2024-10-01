package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

@Repository
public class MpaDbStorage extends BaseDbStorage<Mpa> {
    private static final String FIND_ALL_MPA_QUERY = "SELECT * FROM mpas";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM mpas WHERE id = ?";


    public MpaDbStorage(JdbcTemplate jdbc, RowMapper<Mpa> mapper) {
        super(jdbc, mapper);
    }

    public List<Mpa> findAll() {
        return findMany(FIND_ALL_MPA_QUERY);
    }

    public Optional<Mpa> findById(long mpaId) {
        return findOne(FIND_BY_ID_QUERY, mpaId);
    }
}
