package ru.javaops.masterjava.persist.model;

import com.bertoncelj.jdbi.entitymapper.Column;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class User extends BaseEntity {
    @Column("full_name")
    private @NonNull String fullName;
    private @NonNull String email;
    private @NonNull UserFlag flag;
    private @NonNull
    String city;

    public User(Integer id, String fullName, String email, UserFlag flag, String city) {
        super(id);
        this.fullName = fullName;
        this.email = email;
        this.flag = flag;
        this.city = city;
    }

    public User(String fullName, String email, UserFlag flag,String city) {
        this.fullName = fullName;
        this.email = email;
        this.flag = flag;
        this.city = city;
    }

    public User(Integer id, String fullName, String email, UserFlag flag) {
        super(id);
        this.fullName = fullName;
        this.email = email;
        this.flag = flag;
    }
}