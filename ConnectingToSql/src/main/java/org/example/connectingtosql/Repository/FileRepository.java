package org.example.connectingtosql.Repository;

import lombok.AllArgsConstructor;
import org.example.connectingtosql.Model.StoredFile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@AllArgsConstructor
public class FileRepository {

    private final JdbcTemplate jdbcTemplate;

    // Ambil semua file aktif
    public List<StoredFile> findAll() {
        String sql = "SELECT id, userId, nameFile, hashName, size, isRemoved " +
                "FROM stored_file " +
                "WHERE isRemoved = false";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            StoredFile file = new StoredFile();
            file.setId(rs.getLong("id"));
            file.setUserId(rs.getLong("userId"));
            file.setNameFile(rs.getString("nameFile"));
            file.setHashName(rs.getString("hashName"));
            file.setSize(rs.getLong("size"));
            file.setIsRemoved(rs.getBoolean("isRemoved"));
            return file;
        });
    }

    // Ambil file berdasarkan id
    public StoredFile findById(Long id) {
        String sql = "SELECT id, userId, nameFile, hashName, size, isRemoved " +
                "FROM stored_file " +
                "WHERE id = ?";

        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            StoredFile file = new StoredFile();
            file.setId(rs.getLong("id"));
            file.setUserId(rs.getLong("userId"));
            file.setNameFile(rs.getString("nameFile"));
            file.setHashName(rs.getString("hashName"));
            file.setSize(rs.getLong("size"));
            file.setIsRemoved(rs.getBoolean("isRemoved"));
            return file;
        }, id);
    }

    // Ambil semua file milik user tertentu (aktif saja)
    public List<StoredFile> findByUserId(Long userId) {
        String sql = "SELECT id, userId, nameFile, hashName, size, isRemoved " +
                "FROM stored_file " +
                "WHERE userId = ? AND isRemoved = false";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            StoredFile file = new StoredFile();
            file.setId(rs.getLong("id"));
            file.setUserId(rs.getLong("userId"));
            file.setNameFile(rs.getString("nameFile"));
            file.setHashName(rs.getString("hashName"));
            file.setSize(rs.getLong("size"));
            file.setIsRemoved(rs.getBoolean("isRemoved"));
            return file;
        }, userId);
    }

    // Simpan file
    public int save(StoredFile file) {
        String sql = "INSERT INTO stored_file " +
                "(userId, nameFile, hashName, size, isRemoved) " +
                "VALUES (?, ?, ?, ?, ?)";

        return jdbcTemplate.update(
                sql,
                file.getUserId(),
                file.getNameFile(),
                file.getHashName(),
                file.getSize(),
                false
        );
    }

    // Update file
    public int update(StoredFile file) {
        String sql = "UPDATE stored_file SET " +
                "nameFile = ?, " +
                "hashName = ?, " +
                "size = ?, " +
                "isRemoved = ? " +
                "WHERE id = ?";

        return jdbcTemplate.update(
                sql,
                file.getNameFile(),
                file.getHashName(),
                file.getSize(),
                file.getIsRemoved(),
                file.getId()
        );
    }

    // Soft delete
    public int softDelete(Long id) {
        String sql = "UPDATE stored_file SET isRemoved = true " +
                "WHERE id = ?";

        return jdbcTemplate.update(sql, id);
    }
}