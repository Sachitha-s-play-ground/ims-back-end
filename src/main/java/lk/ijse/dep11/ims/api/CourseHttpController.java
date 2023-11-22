package lk.ijse.dep11.ims.api;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lk.ijse.dep11.ims.to.CourseTO;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.PreDestroy;
import java.sql.*;
import java.util.LinkedList;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/courses")
public class CourseHttpController {

    HikariDataSource pool;
    public CourseHttpController(){

        HikariConfig config = new HikariConfig();
        config.setUsername("postgres");
        config.setPassword("postgres");
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/dep11_todo_app");
        config.setDriverClassName("org.postgresql.Driver");
        config.addDataSourceProperty("maximumPoolSize",10);
        pool=new HikariDataSource(config);
    }
    @PreDestroy
    public void destroy(){
        pool.close();
    }

@PostMapping(produces = "application/json",consumes = "application/json")
@ResponseStatus(HttpStatus.CREATED)
    public CourseTO createCourse(@RequestBody @Validated CourseTO course){

    try (Connection connection = pool.getConnection()){
        PreparedStatement stm = connection.prepareStatement("INSERT INTO course(name, duration_in_months) VALUES (?,?)",
                Statement.RETURN_GENERATED_KEYS);
        stm.setString(1,course.getName());
        stm.setInt(2,course.getDurationInMonths());
        stm.executeUpdate();
        ResultSet generatedKeys = stm.getGeneratedKeys();
        generatedKeys.next();
        int courseId = generatedKeys.getInt(1);
        course.setCourseId(courseId);
        return course;
    } catch (SQLException e) {
        throw new RuntimeException(e);
    }
}

@PatchMapping(value = "/{courseId}",consumes = "application/json")
@ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateCourse(@PathVariable @Validated  int courseId,@RequestBody CourseTO course){

    try(Connection connection = pool.getConnection()) {
        PreparedStatement stmExist = connection.prepareStatement("SELECT * FROM course WHERE id=?");
        stmExist.setInt(1,courseId);
        ResultSet rstExist = stmExist.executeQuery();
        if (!rstExist.next()){
            throw  new ResponseStatusException(HttpStatus.NOT_FOUND,"Course Not Found");
        }


        PreparedStatement stm = connection.prepareStatement("UPDATE course SET name=?,duration_in_months=? WHERE id=?");
        stm.setString(1,course.getName());
        stm.setInt(2,course.getDurationInMonths());
        stm.setInt(3,courseId);
        stm.executeUpdate();
    } catch (SQLException e) {
        throw new RuntimeException(e);
    }
}

@DeleteMapping(value = "/{courseId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCourse(@PathVariable int courseId){

    try (Connection connection = pool.getConnection()){

        PreparedStatement stmExist = connection.prepareStatement("SELECT * FROM course WHERE id=?");
        stmExist.setInt(1,courseId);
        ResultSet rstExist = stmExist.executeQuery();
        if (!rstExist.next()){
            throw  new ResponseStatusException(HttpStatus.NOT_FOUND,"Course Not Found");
        }

        PreparedStatement stm = connection.prepareStatement("DELETE FROM course WHERE id=?");
        stm.setInt(1,courseId);
        stm.executeUpdate();
    } catch (SQLException e) {
        throw new RuntimeException(e);
    }
}

@GetMapping(value = "/{courseId}",produces = "application/json")
@ResponseStatus(HttpStatus.OK)
    public CourseTO getCourseDetails(@PathVariable int courseId){
    try (Connection connection = pool.getConnection()){



        PreparedStatement stmExist = connection.prepareStatement("SELECT * FROM course WHERE id=?");
        stmExist.setInt(1,courseId);
        ResultSet rstExist = stmExist.executeQuery();
        if (!rstExist.next()){
            throw  new ResponseStatusException(HttpStatus.NOT_FOUND,"Course Not Found");
        }
        int id = rstExist.getInt("id");
        String name = rstExist.getString("name");
        int durationInMonths = rstExist.getInt("durationInMonths");
        return new CourseTO(id,name,durationInMonths);
    } catch (SQLException e) {
        throw new RuntimeException(e);
    }
}

@GetMapping(produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public List<CourseTO> getAllCourse(){
    try (Connection connection = pool.getConnection()){
        Statement stm = connection.createStatement();
        ResultSet rst = stm.executeQuery("SELECT * FROM course ORDER BY id");
        List<CourseTO> courseList = new LinkedList<>();
        while (rst.next()){
            int id = rst.getInt("id");
            String name = rst.getString("name");
            int durationInMonths = rst.getInt("durationInMonths");
            CourseTO newCourse = new CourseTO(id,name,durationInMonths);
            courseList.add(newCourse);
        }
        return courseList;
    } catch (SQLException e) {
        throw new RuntimeException(e);
    }
}

}
