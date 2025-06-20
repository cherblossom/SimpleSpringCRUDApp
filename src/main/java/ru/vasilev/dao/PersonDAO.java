package ru.vasilev.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.vasilev.models.Person;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Component
public class PersonDAO {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public PersonDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Person> index() {
        return jdbcTemplate.query("SELECT * FROM person", new BeanPropertyRowMapper<>(Person.class));
    }

    public Optional<Person> show(String email) {
        return jdbcTemplate.query("SELECT * FROM person WHERE email=?",
                new BeanPropertyRowMapper<>(Person.class), email).stream().findFirst();
    }

    public Person show(int id) {
        return jdbcTemplate.query("SELECT * FROM person WHERE id=?",
                        new BeanPropertyRowMapper<>(Person.class), id).stream().findFirst().orElse(null);
    }

    public void save(Person person) {
        jdbcTemplate.update("INSERT INTO person (name, age, email, address) VALUES (?, ?, ?, ?)",
                person.getName(), person.getAge(), person.getEmail(), person.getAddress());
    }

    public void update(int id, Person updatePerson) {
        jdbcTemplate.update("UPDATE person SET name = ?, age = ?, email = ?, address = ? WHERE id = ?",
                updatePerson.getName(), updatePerson.getAge(), updatePerson.getEmail(), updatePerson.getAddress(),
                updatePerson.getId());
    }

    public void delete(int id) {
        jdbcTemplate.update("DELETE FROM person WHERE id = ?", id);
    }

    //////////////////////////////////////
    /// тест производительности
    /// ///////////////////////

    public void testMultipleUpdates(){
        List<Person> people = create1000people();

        long before = System.currentTimeMillis();

        for(Person person : people){
            jdbcTemplate.update("INSERT INTO person VALUES (?, ?, ?, ?, ,)", person.getId(), person.getName(),
                    person.getAge(), person.getEmail(), person.getAddress());
        }

        long after = System.currentTimeMillis();
        System.out.println("Test multiply update: " + (after - before));
    }

    public void testBatchUpdate(){
        List<Person> people = create1000people();
        long before = System.currentTimeMillis();

        jdbcTemplate.batchUpdate("INSERT INTO person VALUES (?, ?, ?, ?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                        preparedStatement.setInt(1, people.get(i).getId());
                        preparedStatement.setString(2, people.get(i).getName());
                        preparedStatement.setInt(3, people.get(i).getAge());
                        preparedStatement.setString(4, people.get(i).getEmail());
                    }

                    @Override
                    public int getBatchSize() {
                        return people.size();
                    }
                });

        long after = System.currentTimeMillis();

        System.out.println("test Batch Update: " + (after - before));
    }

    private List<Person> create1000people() {
        List<Person> people = new ArrayList<Person>();

        for (int i = 0; i < 1000; i++) {
            people.add(new Person(i, "name" + i, 18, "test" + i + "@mail.ru", "some adress" + i));
        }
        return people;
    }
}
