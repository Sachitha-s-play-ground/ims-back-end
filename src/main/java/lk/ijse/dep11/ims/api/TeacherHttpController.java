package lk.ijse.dep11.ims.api;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lk.ijse.dep11.ims.to.TeacherTo;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.PreDestroy;
import java.sql.*;
import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping("/teachers")
@CrossOrigin
public class TeacherHttpController {

    HikariDataSource pool;
    public TeacherHttpController() {
        HikariConfig config = new HikariConfig();
        config.setUsername("root");
        config.setPassword("sachitha18");
        config.setJdbcUrl("jdbc:mysql://localhost:3306/dep11_ims");
        config.setDataSourceClassName("org.mysql.Driver");
        config.addDataSourceProperty("maximumPoolSize", 10);
        pool = new HikariDataSource(config);
    }

    @PreDestroy
    public void destroy(){
        pool.close();
    }

    @PostMapping(consumes = "application/json",produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public TeacherTo createTeacher(
            @RequestBody @Validated TeacherTo teacher
            ){
        try (
                Connection connection = pool.getConnection();
                ){
            PreparedStatement stm = connection.prepareStatement("INSERT INTO teacher (name, contact) VALUES (?,?)"
                    , Statement.RETURN_GENERATED_KEYS);
            stm.setString(1,teacher.getName());
            stm.setString(2,teacher.getContact());
            stm.executeUpdate();
            ResultSet generatedKeys = stm.getGeneratedKeys();
            generatedKeys.next();
            int id = generatedKeys.getInt(1);
            teacher.setTeacherId(id);
            return teacher;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @PatchMapping(value = "/{teacherId}",consumes = "application/json")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateTeacher(
            @PathVariable Integer teacherId,
            @RequestBody @Validated TeacherTo teacher
    ){
        try(
                Connection connection = pool.getConnection();
                ) {
            PreparedStatement stm1 = connection.prepareStatement("SELECT * FROM teacher WHERE id=?");
            stm1.setInt(1,teacherId);
            if(!stm1.executeQuery().next()){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Teacher Not Found");
            }

            PreparedStatement stm = connection.prepareStatement("UPDATE teacher SET name = ?, contact=? WHERE id=?");
            stm.setString(1,teacher.getName());
            stm.setString(2,teacher.getContact());
            stm.setInt(3,teacherId);
            stm.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @DeleteMapping("/{teacherId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTeacher(
            @PathVariable Integer teacherId
    ){
        try(
                Connection connection = pool.getConnection();
                ) {

            PreparedStatement stm1 = connection.prepareStatement("SELECT * FROM teacher WHERE id=?");
            stm1.setInt(1,teacherId);
            if(!stm1.executeQuery().next()){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Teacher Not Found");
            }

            PreparedStatement stm = connection.prepareStatement("DELETE FROM teacher WHERE id=?");
            stm.setInt(1,teacherId);
            stm.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @GetMapping(value = "/{teacherId}",produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public TeacherTo getTeacherDetails(
            @PathVariable Integer teacherId
    ){
        try(
                Connection connection = pool.getConnection();
                ) {
            PreparedStatement stm1 = connection.prepareStatement("SELECT * FROM teacher WHERE id=?");
            stm1.setInt(1,teacherId);
            ResultSet rst=stm1.executeQuery();
            if(!rst.next()){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Teacher Not Found");
            }
            int id = rst.getInt("id");
            String name = rst.getString("name");
            String contact = rst.getString("contact");
            return new TeacherTo(id,name,contact);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping(produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public List<TeacherTo> getAllTeachers(){
        try (
                Connection connection = pool.getConnection();
                ){
            Statement stm = connection.createStatement();
            ResultSet rst = stm.executeQuery("SELECT * FROM teacher ORDER BY id");
            List<TeacherTo> teacherList = new LinkedList<>();
            while (rst.next()){
                int id = rst.getInt("id");
                String name = rst.getString("name");
                String contact = rst.getString("contact");
                teacherList.add(new TeacherTo(id,name,contact));
            }
            return teacherList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
