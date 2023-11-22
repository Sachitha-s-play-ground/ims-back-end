package lk.ijse.dep11.ims.to;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;
import java.io.Serializable;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseTO implements Serializable {
    @Null
    private Integer courseId;
    @NotBlank
    private  String name;
    @NotBlank
    private Integer durationInMonths;


}
