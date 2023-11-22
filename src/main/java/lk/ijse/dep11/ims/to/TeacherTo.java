package lk.ijse.dep11.ims.to;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;
import java.io.Serializable;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherTo implements Serializable {
    @Null
    private Integer teacherId;
    @NotBlank
    private String name;
    @NotBlank
    private String contact;
}
