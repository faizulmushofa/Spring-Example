package org.example.connectingtosql.Repository;

import lombok.AllArgsConstructor;
import org.example.connectingtosql.Model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@AllArgsConstructor
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    //Ambil data
    public List<User> findAll(){
        String sqlQuery = "SELECT id," +
                "username," +
                "password," +
                "email," +
                "isActive " +
                "FROM user";

        return jdbcTemplate.query(sqlQuery,(rs,row)-> {

            return User.builder()
                    .id(rs.getLong("id"))
                    .password(rs.getString("password"))
                    .username(rs.getString("username"))
                    .email(rs.getString("email"))
                    .isActive(rs.getBoolean("isActive"))
                    .build();

        });

    }

    public User findById(Long id){
        String querySql = "SELECT id," +
                "username," +
                "password," +
                "email," +
                "isActive " +
                "FROM user WHERE id= ?";

        return jdbcTemplate.queryForObject(querySql,new Object[]{id},(rs,rowNum) -> {
            return User.builder()
                    .id(rs.getLong("id"))
                    .password(rs.getString("password"))
                    .username(rs.getString("username"))
                    .email(rs.getString("email"))
                    .isActive(rs.getBoolean("isActive"))
                    .build();
        });
    }

    public int save(User user){
        String querySql = "INSERT INTO user(" +
                "username," +
                "password," +
                "email," +
                "isActive" +
                ") VALUES (?,?,?,?) ";

        return jdbcTemplate.update(querySql,
                user.getUsername(),
                user.getPassword(),
                user.getEmail(),
                user.getIsActive());
    }

    public int update(User user){
        String querySql = "UPDATE user SET " +
                "username=?," +
                "password=?,"+
                "email=?,"+
                "isActive=?" +
                " WHERE id=?";

        return jdbcTemplate.update(querySql,
                user.getUsername(),
                user.getPassword(),
                user.getEmail(),
                user.getIsActive(),
                user.getId()
        );
    }

    public int softDelete(Long id){
        String qurySql = "UPDATE user SET isActive=? WHERE id=?";

        return jdbcTemplate.update(qurySql,
                false,
                id);
    }

}
